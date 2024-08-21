package at.open.letto.plugin.controller;

import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * offene Endpoints welche von extern erreichbar sein müssen für ajax und
 * allgemeine Informationen (von extern erreichbar)
 */
@RestController
@RequestMapping(Endpoint.EXTERN_OPEN)
@Tag(name = "Api Extern Open Controller",
        description = "offene Endpoints welche von extern erreichbar sein müssen für ajax und allgemeine Informationen (von extern erreichbar)" +
                "[JavaDoc](https://build.letto.at/pluginuhr/open/javadoc/at/open/letto/plugin/controller/ApiExternOpenController.html)"
)
public class ApiExternOpenController {

    @Autowired private ConnectionService connectionService;
    @Autowired private ApiController apiController;

    @Operation(summary = "liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden")
    @GetMapping(PluginConnectionEndpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        return apiController.pluginList();
    }

    @Operation(
            summary = "Liste aller PluginsInformationen",
            description = "liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services<br>" +
                    "Result: [PluginGeneralInfoList](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfoList.html)"
    )
    @GetMapping(PluginConnectionEndpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        return apiController.pluginGeneralInfoList();
    }

    @Operation(
            summary = "PluginInformation",
            description = "liefert die allgemeinen Konfigurationsinformationen zu einem Plugin<br>" +
                    "Body: String - Name des Plugins<br>" +
                    "Result: [PluginGeneralInfo](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfo.html)"
    )
    @PostMapping(PluginConnectionEndpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(@RequestBody String plugintyp) {
        return apiController.pluginGeneralInfo(plugintyp);
    }

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
    @Operation(
            summary = "setze Konfigurationsdaten",
            description = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung für den ajax-Zugriff <br>" +
                    "Body  : [LoadPluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/LoadPluginRequestDto.html)<br>" +
                    "Result: [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.reloadPluginDto(r);
    }

}
