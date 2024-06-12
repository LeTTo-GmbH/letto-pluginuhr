package at.open.letto.plugin.controller;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.plugins.restclient.BasePluginConnectionService;
import at.letto.tools.dto.ImageBase64Dto;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping(Endpoint.EXTERN_OPEN)
public class ApiExternOpenController {

    @Autowired private ConnectionService connectionService;
    @Autowired private ApiController apiController;

    @GetMapping(PluginConnectionEndpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        return apiController.pluginList();
    }

    @GetMapping(PluginConnectionEndpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        return apiController.pluginGeneralInfoList();
    }

    @PostMapping(PluginConnectionEndpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(@RequestBody String plugintyp) {
        return apiController.pluginGeneralInfo(plugintyp);
    }
    /*
    @PostMapping(PluginConnectionEndpoint.getHTML)
    public ResponseEntity<String> getHtml(@RequestBody PluginRequestDto r) {
        return apiController.getHtml(r);
    }

    @PostMapping(PluginConnectionEndpoint.getAngabe)
    public ResponseEntity<String> getAngabe(@RequestBody PluginRequestDto r) {
        return apiController.getAngabe(r);
    }

    @PostMapping(PluginConnectionEndpoint.generateDatasets)
    public ResponseEntity<PluginDatasetListDto> generateDatasets(@RequestBody PluginRequestDto r) {
        return apiController.generateDatasets(r);
    }

    @PostMapping(PluginConnectionEndpoint.getMaxima)
    public ResponseEntity<String> getMaxima(@RequestBody PluginRequestDto r) {
        return apiController.getMaxima(r);
    }

    @PostMapping(PluginConnectionEndpoint.getImage)
    public ResponseEntity<ImageBase64Dto> getImage(@RequestBody PluginRequestDto r) {
        return apiController.getImage(r);
    }

    @PostMapping(PluginConnectionEndpoint.getImageTemplates)
    public ResponseEntity<Vector<String[]>> getImageTemplates(@RequestBody PluginRequestDto r) {
        return apiController.getImageTemplates(r);
    }

    @PostMapping(PluginConnectionEndpoint.parserPlugin)
    public ResponseEntity<CalcErgebnisDto> parserPlugin(@RequestBody PluginParserRequestDto r) {
        return apiController.parserPlugin(r);
    }

    @PostMapping(PluginConnectionEndpoint.parserPluginEinheit)
    public ResponseEntity<String> parserPluginEinheit(@RequestBody PluginEinheitRequestDto r) {
        return apiController.parserPluginEinheit(r);
    }

    @PostMapping(PluginConnectionEndpoint.score)
    public ResponseEntity<PluginScoreInfoDto> score(@RequestBody PluginScoreRequestDto r) {
        return apiController.score(r);
    }

    @PostMapping(PluginConnectionEndpoint.getVars)
    public ResponseEntity<Vector<String>> getVars(@RequestBody PluginRequestDto r) {
        return apiController.getVars(r);
    }

    @PostMapping(PluginConnectionEndpoint.modifyAngabe)
    public ResponseEntity<String> modifyAngabe(@RequestBody PluginAngabeRequestDto r) {
        return apiController.modifyAngabe(r);
    }

    @PostMapping(PluginConnectionEndpoint.modifyAngabeTextkomplett)
    public ResponseEntity<String> modifyAngabeTextkomplett(@RequestBody PluginAngabeRequestDto r) {
        return apiController.modifyAngabeTextkomplett(r);
    }

    @PostMapping(PluginConnectionEndpoint.updatePluginstringJavascript)
    public ResponseEntity<String> updatePluginstringJavascript(@RequestBody PluginUpdateJavascriptRequestDto r) {
        return apiController.updatePluginstringJavascript(r);
    }

    @PostMapping(PluginConnectionEndpoint.loadPluginDto)
    public ResponseEntity<PluginDto> loadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.loadPluginDto(r);
    }

    @PostMapping(PluginConnectionEndpoint.renderLatex)
    public ResponseEntity<PluginRenderDto> renderLatex(@RequestBody PluginRenderLatexRequestDto r) {
        return apiController.renderLatex(r);
    }

    @PostMapping(PluginConnectionEndpoint.renderPluginResult)
    public ResponseEntity<PluginRenderDto> renderPluginResult(@RequestBody PluginRenderResultRequestDto r) {
        return apiController.renderPluginResult(r);
    }

    @PostMapping(PluginConnectionEndpoint.configurationInfo)
    public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
        return apiController.configurationInfo(r);
    }

    @PostMapping(PluginConnectionEndpoint.setConfigurationData)
    public ResponseEntity<PluginConfigDto> setConfigurationData(@RequestBody PluginSetConfigurationDataRequestDto r) {
        return apiController.setConfigurationData(r);
    }

    @PostMapping(PluginConnectionEndpoint.getConfiguration)
    public ResponseEntity<String> getConfiguration(@RequestBody PluginConfigurationRequestDto configurationID) {
        return apiController.getConfiguration(configurationID);
    }*/

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param r    LoadPluginRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.params    String: Plugin-Parameter<br>
     *   r.nr        int: Laufende Nummer für alle PIG-Tags und Question-Plugins<br>
     *   r.configurationID String:ID der aktuellen Konfiguration
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @PostMapping(PluginConnectionEndpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.reloadPluginDto(r);
    }

}
