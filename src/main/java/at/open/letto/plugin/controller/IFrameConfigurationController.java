package at.open.letto.plugin.controller;

import at.letto.plugins.dto.PluginConfigurationConnection;
import at.letto.plugins.dto.PluginDto;
import at.letto.plugins.dto.PluginGeneralInfo;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.plugins.restclient.BasePluginConnectionService;
import at.letto.tools.JSON;
import at.letto.tools.JavascriptLibrary;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.config.MicroServiceConfiguration;
import at.open.letto.plugin.dto.IFrameConfigDto;
import at.open.letto.plugin.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(Endpoint.open)
public class IFrameConfigurationController {


    @Autowired private MicroServiceConfiguration microServiceConfiguration;
    @Autowired private ConnectionService connectionService;

    /**
     * Aufbau des IFrames beim ersten Zugang
     * @param typ             Get-Parameter - Typ des Plugins
     * @param configurationID Get-Parameter - ConfigurationID für die Verbindung
     * @param model           Model für die Thymeleaf-Bindung zwischen Controller und View
     * @return                View-Template
     */
    @GetMapping(PluginConnectionEndpoint.configurationHttp)
    public String configForm(@RequestParam(name = "typ", required = true) String typ,
                           @RequestParam(name = "configurationID", required = true) String configurationID,
                           Model model) {
        model.addAttribute("configurationID", configurationID);
        // Initialisiere das Formular-Objekt und setze es im Model
        IFrameConfigDto form = new IFrameConfigDto(typ, configurationID);
        // Setze das Model für das Formular
        return prepareForm(true,form,model);
    }

    /**
     * Auswertung des Formulares
     * @param form            Formulardaten
     * @param bindingResult   Ergebnis des Bindings
     * @param model           Model für die Thymeleaf-Bindung zwischen Controller und View
     * @return                View-Template
     */
    @PostMapping(PluginConnectionEndpoint.configurationHttp)
    public String configFormHandler(@ModelAttribute("form") IFrameConfigDto form, BindingResult bindingResult, Model model) {
        // Verarbeite das Formular und füge die Ergebnisse dem Model hinzu
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg","Fehler beim Binden der Parameter!");
            return "error"; // Bei Fehlern das Formular erneut anzeigen
        }
        // Auswerten des Formulars

        // Setze das Model für das Formular
        return prepareForm(false, form, model);
    }

    private String prepareForm(boolean loadForm, IFrameConfigDto form, Model model) {
        // Plugin-Connection-Service laden
        BasePluginConnectionService   pcs  = connectionService.getPluginConnectionService(form.getTyp());
        if (pcs==null) {
            model.addAttribute("msg","connectionID is not valid!");
            return "error";
        }
        // Connection-Konfiguration laden
        PluginConfigurationConnection conn = pcs.getConfigurationConnection(form.getTyp(), form.getConfigurationID());
        if (conn == null) {
            model.addAttribute("msg","connectionID is not valid!");
            return "error";
        }
        if (loadForm) {
            form.setConfig(conn.config);
            form.setParams("");
        } else {
            String config = form.getConfig();
            conn.changeConfig(config,pcs.createPluginService(form.getTyp(),conn.getName(),config));
        }
        PluginDto pluginDto = pcs.loadPluginDto(form.getTyp(),conn.getName(),form.getConfig(),form.getParams(),conn.pluginQuestionDto,0);
        // setzt die Konfigurations-ID
        model.addAttribute("configurationID", form.getConfigurationID());
        // Initialisiere das Formular-Objekt und setze es im Model
        model.addAttribute("form", form);
        // Endpoints für das Formular setzen
        model.addAttribute("endpoints" ,microServiceConfiguration.getEndpoints());
        // setze das QuestionDto
        model.addAttribute("pluginQuestionDto",conn.pluginQuestionDto);
        // setzt den Plugin-Javsscript-Code
        model.addAttribute("jscode"    ,getLibsHeader(conn.pluginService.getPluginGeneralInfo()));
        // setze das PluginDto für die Vorschau
        model.addAttribute("pluginDto",pluginDto);
        // setze das div Element für die Vorschau
        model.addAttribute("answerFieldClass",pluginDto.getTagName() + "_inp");
        model.addAttribute("divName",pluginDto.getTagName() + "_div");
        String tab = "\n        ";
        model.addAttribute("loadpreviewjs",tab+"let pluginDto="+JSON.objToJson(pluginDto)+";"+tab+
            "function initPlugin(pluginDto){"+tab+"   "+conn.pluginService.getPluginGeneralInfo().getInitPluginJS()+"(JSON.stringify(pluginDto),true);"+tab+"};"+tab+
            "initPlugin(pluginDto);" +tab+
            "const restUri   = '"+conn.getPluginConfigDto().getPluginDtoUri()+"';" +tab+
            "const restToken = '"+conn.getPluginConfigDto().getPluginDtoToken()+"';"+tab+
            "const plugintyp = '"+form.getTyp()+"';"+tab+
            "const pluginname= '"+conn.getName()+"';"+tab+
            "const configurationID= '"+conn.getConfigurationID()+"';"
        );
        // setze die Plugin-Hilfe
        model.addAttribute("help",conn.pluginConfigDto.getParams().get("help"));

        // User-Action zurücksetzen
        form.setUserAction(null);
        // Rückgabe
        return "configFormView";
    }

    public List<JavascriptLibrary> getLibsJavascript(PluginGeneralInfo info) {
        List<JavascriptLibrary> libs = new ArrayList<>();
        if (info==null) return libs;
        for (JavascriptLibrary lib:info.getJavascriptLibraries())
            libs.add(lib);
        for (JavascriptLibrary lib:info.getJavascriptLibrariesLocal())
            libs.add(lib);
        return libs;
    }

    public String getLibsHeader(PluginGeneralInfo info) {
        List<JavascriptLibrary> libs = getLibsJavascript(info);
        StringBuilder sb = new StringBuilder();
        for (JavascriptLibrary lib:libs) {
            if (lib.getName().endsWith(".js"))
                sb.append("<script>"+lib.getJs_code()+"</script>\n");
            else if (lib.getName().endsWith(".css"))
                sb.append("<style>"+lib.getJs_code()+"</style>\n");
            else
                sb.append("<span>Fehler bei "+lib.getName()+"</span>");
        }
        return sb.toString();
    }

}
