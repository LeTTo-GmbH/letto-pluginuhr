package at.open.letto.plugin.controller;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.plugins.restclient.BasePluginConnectionService;
import at.letto.tools.dto.ImageBase64Dto;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden")
    @GetMapping(PluginConnectionEndpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        return apiController.pluginList();
    }

    @Operation(summary = "liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services")
    @GetMapping(PluginConnectionEndpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        return apiController.pluginGeneralInfoList();
    }

    @Operation(summary = "liefert die allgemeinen Konfigurationsinformationen zu einem Plugin")
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
    @Operation(summary = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung für den ajax-Zugriff")
    @PostMapping(PluginConnectionEndpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.reloadPluginDto(r);
    }

}
