package at.open.letto.plugin.controller;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
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
@RequestMapping(Endpoint.EXTERN_API)
public class ApiExternController {

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

    @Operation(summary = "Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag")
    @PostMapping(PluginConnectionEndpoint.getHTML)
    public ResponseEntity<String> getHtml(@RequestBody PluginRequestDto r) {
        return apiController.getHtml(r);
    }

    @Operation(summary = "Liefert einen Angabestring für die Text-Angabe")
    @PostMapping(PluginConnectionEndpoint.getAngabe)
    public ResponseEntity<String> getAngabe(@RequestBody PluginRequestDto r) {
        return apiController.getAngabe(r);
    }

    @Operation(summary = "Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten")
    @PostMapping(PluginConnectionEndpoint.generateDatasets)
    public ResponseEntity<PluginDatasetListDto> generateDatasets(@RequestBody PluginRequestDto r) {
        return apiController.generateDatasets(r);
    }

    @Operation(summary = "Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins")
    @PostMapping(PluginConnectionEndpoint.getMaxima)
    public ResponseEntity<String> getMaxima(@RequestBody PluginRequestDto r) {
        return apiController.getMaxima(r);
    }

    @Operation(summary = "Liefert ein Base64 codiertes Bild mit den angegebenen Parametern")
    @PostMapping(PluginConnectionEndpoint.getImage)
    public ResponseEntity<ImageBase64Dto> getImage(@RequestBody PluginRequestDto r) {
        return apiController.getImage(r);
    }

    @Operation(summary = "Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays")
    @PostMapping(PluginConnectionEndpoint.getImageTemplates)
    public ResponseEntity<Vector<String[]>> getImageTemplates(@RequestBody PluginRequestDto r) {
        return apiController.getImageTemplates(r);
    }

    @Operation(summary = "Wird verwendet wenn im Lösungsfeld die Funktion plugin(\"pluginname\",p1,p2,p3) verwendet wird")
    @PostMapping(PluginConnectionEndpoint.parserPlugin)
    public ResponseEntity<CalcErgebnisDto> parserPlugin(@RequestBody PluginParserRequestDto r) {
        return apiController.parserPlugin(r);
    }

    @Operation(summary = "Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben")
    @PostMapping(PluginConnectionEndpoint.parserPluginEinheit)
    public ResponseEntity<String> parserPluginEinheit(@RequestBody PluginEinheitRequestDto r) {
        return apiController.parserPluginEinheit(r);
    }

    @Operation(summary = "Prüft die Eingabe eines Schülers")
    @PostMapping(PluginConnectionEndpoint.score)
    public ResponseEntity<PluginScoreInfoDto> score(@RequestBody PluginScoreRequestDto r) {
        return apiController.score(r);
    }

    @Operation(summary = "Liefert eine Liste aller Variablen welche als Dataset benötigt werden.")
    @PostMapping(PluginConnectionEndpoint.getVars)
    public ResponseEntity<Vector<String>> getVars(@RequestBody PluginRequestDto r) {
        return apiController.getVars(r);
    }

    @Operation(summary = "verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde")
    @PostMapping(PluginConnectionEndpoint.modifyAngabe)
    public ResponseEntity<String> modifyAngabe(@RequestBody PluginAngabeRequestDto r) {
        return apiController.modifyAngabe(r);
    }

    @Operation(summary = "verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!")
    @PostMapping(PluginConnectionEndpoint.modifyAngabeTextkomplett)
    public ResponseEntity<String> modifyAngabeTextkomplett(@RequestBody PluginAngabeRequestDto r) {
        return apiController.modifyAngabeTextkomplett(r);
    }

    @Operation(summary = "Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte")
    @PostMapping(PluginConnectionEndpoint.updatePluginstringJavascript)
    public ResponseEntity<String> updatePluginstringJavascript(@RequestBody PluginUpdateJavascriptRequestDto r) {
        return apiController.updatePluginstringJavascript(r);
    }

    @Operation(summary = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung")
    @PostMapping(PluginConnectionEndpoint.loadPluginDto)
    public ResponseEntity<PluginDto> loadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.loadPluginDto(r);
    }

    @Operation(summary = "Rendert ein Plugins für den Fragedruck als Latex-Sourcode")
    @PostMapping(PluginConnectionEndpoint.renderLatex)
    public ResponseEntity<PluginRenderDto> renderLatex(@RequestBody PluginRenderLatexRequestDto r) {
        return apiController.renderLatex(r);
    }

    @Operation(summary = "Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung. Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt")
    @PostMapping(PluginConnectionEndpoint.renderPluginResult)
    public ResponseEntity<PluginRenderDto> renderPluginResult(@RequestBody PluginRenderResultRequestDto r) {
        return apiController.renderPluginResult(r);
    }

    @Operation(summary = "Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten. Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die Konfiguration aktiviert.")
    @PostMapping(PluginConnectionEndpoint.configurationInfo)
    public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
        return apiController.configurationInfo(r);
    }

    @Operation(summary = "Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration")
    @PostMapping(PluginConnectionEndpoint.setConfigurationData)
    public ResponseEntity<PluginConfigDto> setConfigurationData(@RequestBody PluginSetConfigurationDataRequestDto r) {
        return apiController.setConfigurationData(r);
    }

    @Operation(summary = "Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet")
    @PostMapping(PluginConnectionEndpoint.getConfiguration)
    public ResponseEntity<String> getConfiguration(@RequestBody PluginConfigurationRequestDto configurationID) {
        return apiController.getConfiguration(configurationID);
    }

    @Operation(summary = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung")
    @PostMapping(PluginConnectionEndpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        return apiController.reloadPluginDto(r);
    }

}
