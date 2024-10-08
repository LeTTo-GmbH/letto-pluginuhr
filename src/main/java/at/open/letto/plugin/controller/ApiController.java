package at.open.letto.plugin.controller;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.plugins.interfaces.PluginService;
import at.letto.plugins.restclient.BasePluginConnectionService;
import at.letto.tools.JSON;
import at.letto.tools.dto.ImageBase64Dto;
import at.open.letto.plugin.config.Endpoint;
import at.open.letto.plugin.service.ConnectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Vector;

/**
 * REST-Schnittstelle des Plugins zwischen LeTTo und Plugin (nur aus dem Docker-Netzwerk erreichbar)
 */
@RestController
@RequestMapping(Endpoint.LOCAL_API)
@Tag(name = "Api Controller",
     description = "REST-Schnittstelle des Plugins zwischen LeTTo und Plugin (nur aus dem Docker-Netzwerk erreichbar) " +
                   "[JavaDoc](https://build.letto.at/pluginuhr/open/javadoc/at/open/letto/plugin/controller/ApiController.html)"
)
public class ApiController {

    @Autowired private ConnectionService connectionService;

    /** @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden */
    @Operation(summary = "liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden")
    @GetMapping(PluginConnectionEndpoint.getPluginList)
    public ResponseEntity<List<String>> pluginList() {
        List<String> result = connectionService.pm.getPluginList();
        return ResponseEntity.ok(result);
    }

    /** @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services*/
    @Operation(
            summary = "Liste aller PluginsInformationen",
            description = "liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services<br>" +
                    "Result: [PluginGeneralInfoList](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfoList.html)"
    )
    @GetMapping(PluginConnectionEndpoint.getPluginGeneralInfoList)
    public ResponseEntity<PluginGeneralInfoList> pluginGeneralInfoList() {
        List<PluginGeneralInfo> resultList = connectionService.pm.getPluginGeneralInfoList();
        PluginGeneralInfoList result = new PluginGeneralInfoList(resultList);
        return ResponseEntity.ok(result);
    }

    /**
     * @param plugintyp Typ des Plugins (z.B. Wsr) mit dem das Plugin auch in LeTTo angesprochen wird
     * @return          liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @Operation(
            summary = "PluginInformation",
            description = "liefert die allgemeinen Konfigurationsinformationen zu einem Plugin<br>" +
                    "Body: String - Name des Plugins<br>" +
                    "Result: [PluginGeneralInfo](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginGeneralInfo.html)"
    )
    @PostMapping(PluginConnectionEndpoint.getPluginGeneralInfo)
    public ResponseEntity<PluginGeneralInfo> pluginGeneralInfo(
            @RequestBody String plugintyp) {
        PluginGeneralInfo result = connectionService.pm.getPluginGeneralInfo(plugintyp);
        return ResponseEntity.ok(result);
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     HTML Text des Plugins
     */
    @Operation(
            summary = "HTML Fragetext",
            description = "Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag <br>" +
                          "Body: [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                          " - .typ  : String - Typ des Plugins<br>" +
                          " - .name : String - Name des Plugins in der Frage<br>" +
                          " - .config : String - Konfigurationsstring des Plugins<br>" +
                          " - .params : String - Plugin-Parameter<br>" +
                          " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                          "Result: String"
    )
    @PostMapping(PluginConnectionEndpoint.getHTML)
    public ResponseEntity<String> getHtml(
            @RequestBody PluginRequestDto r) {
        String result = connectionService.pm.getHTML(r.getTyp(),r.getName(),r.getConfig(),r.getParams(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert einen Angabestring für die Text-Angabe
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     * @return     Angabetext für das Textfeld der Frage
     */
    @Operation(
            summary = "Angabetext erzeugen",
            description = "Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT TagLiefert einen Angabestring für die Text-Angabe <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .params : String - Plugin-Parameter<br>" +
                    "Result: String"
    )
    @PostMapping(PluginConnectionEndpoint.getAngabe)
    public ResponseEntity<String> getAngabe(@RequestBody PluginRequestDto r) {
        String result = connectionService.pm.getAngabe(r.getTyp(),r.getName(),r.getConfig(),r.getParams());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return     Liste der Datensatzdefinitionen welche vom Plugin in der Frage angefordert werden
     */
    @Operation(
            summary = "alle Datensätze bestimmen",
            description = "Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    "Result: [PluginDatasetListDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDatasetListDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.generateDatasets)
    public ResponseEntity<PluginDatasetListDto> generateDatasets(@RequestBody PluginRequestDto r) {
        PluginDatasetListDto result = new PluginDatasetListDto(connectionService.pm.generateDatasets(r.getTyp(),r.getName(),r.getConfig()));
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     *   r.pluginMaximaCalcMode PluginMaximaCalcModeDto: Berechnungsmode der Frage <br>
     * @return     Maxima Berechnungs-String
     */
    @Operation(
            summary = "Maxima Berechung des Plugins",
            description = "Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .params : String - Plugin-Parameter<br>" +
                    " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                    " - .pluginsMaximaCalcMode : [PluginMaximaCalcModeDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginMaximaCalcModeDto.html) - Berechnungsmode der Frage<br>" +
                    "Result: String"
    )
    @PostMapping(PluginConnectionEndpoint.getMaxima)
    public ResponseEntity<String> getMaxima(@RequestBody PluginRequestDto r) {
        String result = connectionService.pm.getMaxima(r.getTyp(),r.getName(),r.getConfig(),r.getParams(),r.getQ(),r.getPluginMaximaCalcMode());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.params  String: Plugin-Parameter<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     Base64 kodiertes Bild
     */
    @Operation(
            summary = "Bild berechnen",
            description = "Liefert ein Base64 codiertes Bild mit den angegebenen Parametern <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .params : String - Plugin-Parameter<br>" +
                    " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                    "Result: [ImageBase64Dto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/tools/dto/ImageBase64Dto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.getImage)
    public ResponseEntity<ImageBase64Dto> getImage(@RequestBody PluginRequestDto r) {
        ImageBase64Dto result = connectionService.pm.getImage(r.getTyp(),r.getName(),r.getConfig(),r.getParams(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays
     *      Element 0 : beschreibender Text
     *      Element 1 : PIG Tag
     *      Element 2 : Hilfetext
     */
    @Operation(
            summary = "PIG Templates bestimmen",
            description = "Liefert eine Liste aller möglichen Varianten von Bildern in String-Arrays <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    "Result: Vector&lt;String[]&gt;<br>" +
                    " Element 0 : beschreibender Text<br>" +
                    " Element 1 : PIG Tag<br>" +
                    " Element 2 : Hilfetext"
    )
    @PostMapping(PluginConnectionEndpoint.getImageTemplates)
    public ResponseEntity<Vector<String[]>> getImageTemplates(@RequestBody PluginRequestDto r) {
        Vector<String[]> result = connectionService.pm.getImageTemplates(r.getTyp(),r.getName(),r.getConfig());
        return ResponseEntity.ok(result);
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     * @param r    PluginParserRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.vars    VarHashDto: Alle Variablen der Frage<br>
     *   r.cp      CalcParamsDto: Berechnungsparameter<br>
     *   r.p       CalcErgebnisDto[]: Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return     Ergebnis der Funktion als CalcErgebnisDto
     */
    @Operation(
            summary = "Parser Plugin Methode",
            description = "Wird verwendet wenn im Lösungsfeld die Funktion plugin(\"pluginname\",p1,p2,p3) verwendet wird <br>" +
                    "Body  : [PluginParserRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginParserRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .vars : [VarHashDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/VarHashDto.html) - Alle Variablen der Frage<br>" +
                    " - .cp   : [CalcParamsDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/CalcParamsDto.html) - Berechnungsparameter<br>" +
                    " - .p    : [CalcErgebnisDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/CalcErgebnisDto.html)[]: Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können<br>" +
                    "Result: [CalcErgebnisDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/CalcErgebnisDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.parserPlugin)
    public ResponseEntity<CalcErgebnisDto> parserPlugin(@RequestBody PluginParserRequestDto r) {
        CalcErgebnisDto result = connectionService.pm.parserPlugin(r.getTyp(),r.getName(),r.getConfig(),r.getVars(),r.getCp(),r.getP());
        return ResponseEntity.ok(result);
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     * @param r    PluginEinheitRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.p       String[]: Einheiten der Parameter als Recheneinheiten
     * @return     Recheneinheit des Ergebnisses
     */
    @Operation(
            summary = "Recheneinheit von parserPlugin",
            description = "Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben<br>" +
                    "Body  : [PluginEinheitRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginEinheitRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .p      : String[] - Einheiten der Parameter als Recheneinheiten<br>" +
                    "Result: String"
    )
    @PostMapping(PluginConnectionEndpoint.parserPluginEinheit)
    public ResponseEntity<String> parserPluginEinheit(@RequestBody PluginEinheitRequestDto r) {
        String result = connectionService.pm.parserPluginEinheit(r.getTyp(),r.getName(),r.getConfig(),r.getP());
        return ResponseEntity.ok(result);
    }

    /**
     * Prüft die Eingabe eines Schülers
     * @param r    PluginScoreRequestDto:<br>
     *   r.typ          String: Typ des Plugins<br>
     *   r.name         String: Name des Plugins in der Frage<br>
     *   r.config       String: Konfigurationsstring des Plugins<br>
     *   r.pluginDto    PluginDto: PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde<br>
     *   r.antwort      String: Antwort die der Schüler eingegeben hat<br>
     *   r.toleranz     ToleranzDto: Toleranz für die Lösung<br>
     *   r.varsQuestion VarHashDto: VarHash aller Variablen der Frage<br>
     *   r.answerDto    PluginAnswerDto: korrekte Antwort und Informationen für den Scorer des Plugins<br>
     *   r.grade        double: Maximale Punktanzahl für die richtige Antwort<br>
     * @return     PluginScoreInfoDto: Bewertung
     */
    @Operation(
            summary = "Score Methode",
            description = "Prüft die Eingabe eines Schülers <br>" +
                    "Body  : [PluginScoreRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginScoreRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .pluginDto    : [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html) - PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde<br>" +
                    " - .antwort      : String - Antwort die der Schüler eingegeben hat<br>" +
                    " - .toleranz     : [ToleranzDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/ToleranzDto.html) - Toleranz für die Lösung<br>" +
                    " - .varsQuestion : [VarHashDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/VarHashDto.html) - Alle Variablen der Frage<br>" +
                    " - .answerDto    : [PluginAnswerDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginAnswerDto.html) - korrekte Antwort und Informationen für den Scorer des Plugins<br>" +
                    " - .grade        : double - Maximale Punktanzahl für die richtige Antwort<br>" +
                    "Result: [PluginScoreInfoDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginScoreInfoDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.score)
    public ResponseEntity<PluginScoreInfoDto> score(@RequestBody PluginScoreRequestDto r) {
        PluginScoreInfoDto result = connectionService.pm.score(r.getTyp(),r.getName(),r.getConfig(),r.getPluginDto(),r.getAntwort(),r.getToleranz(),r.getVarsQuestion(),r.getAnswerDto(), r.getGrade());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     * @param r    PluginRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     * @return     Liste aller Variablen des Plugins
     */
    @Operation(
            summary = "benötigte Variablen bestimmen",
            description = "Liefert eine Liste aller Variablen welche als Dataset benötigt werden <br>" +
                    "Body  : [PluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    "Result: Vector&lt;String&gt;"
    )
    @PostMapping(PluginConnectionEndpoint.getVars)
    public ResponseEntity<Vector<String>> getVars(@RequestBody PluginRequestDto r) {
        Vector<String> result = connectionService.pm.getVars(r.getTyp(),r.getName(),r.getConfig());
        return ResponseEntity.ok(result);
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param r    PluginAngabeRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.text    String: Text der innerhalb der PI Tags gestanden ist<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     veränderter Text
     */
    @Operation(
            summary = "Angabe anpassen",
            description = "verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>" +
                    "Body  : [PluginAngabeRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginAngabeRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .text  : String - Text der innerhalb der PI Tags gestanden ist<br>" +
                    " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                    "Result: String"
    )
    @PostMapping(PluginConnectionEndpoint.modifyAngabe)
    public ResponseEntity<String> modifyAngabe(@RequestBody PluginAngabeRequestDto r) {
        String result = connectionService.pm.modifyAngabe(r.getTyp(),r.getName(),r.getConfig(),r.getText(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param r    PluginAngabeRequestDto:<br>
     *   r.typ     String: Typ des Plugins<br>
     *   r.name    String: Name des Plugins in der Frage<br>
     *   r.config  String: Konfigurationsstring des Plugins<br>
     *   r.text    String: Angabetext der Frage<br>
     *   r.q       PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     * @return     veränderter AngabeText
     */
    @Operation(
            summary = "Angabe neu",
            description = "verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>" +
                    "Body  : [PluginAngabeRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginAngabeRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .text  : String - Text der innerhalb der PI Tags gestanden ist<br>" +
                    " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                    "Result: String - veränderter Angabetext"
    )
    @PostMapping(PluginConnectionEndpoint.modifyAngabeTextkomplett)
    public ResponseEntity<String> modifyAngabeTextkomplett(@RequestBody PluginAngabeRequestDto r) {
        String result = connectionService.pm.modifyAngabeTextkomplett(r.getTyp(),r.getName(),r.getConfig(),r.getText(),r.getQ());
        return ResponseEntity.ok(result);
    }

    /**
     *Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param r    PluginUpdateJavascriptRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.pluginDef String: akt. Plugin-Definition<br>
     *   r.jsResult  String: Rückgabe von Javascript<br>
     * @return     aktualiesierte Plugindefinition
     */
    @Operation(
            summary = "update des Pluginstrings ",
            description = "Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte<br>" +
                    "Body  : [PluginUpdateJavascriptRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginUpdateJavascriptRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .pluginDef  : String - akt. Plugin-Definition<br>" +
                    " - .jsResult   : String - Rückgabe von Javascript<br>" +
                    "Result: String - aktualiesierte Plugindefinition"
    )
    @PostMapping(PluginConnectionEndpoint.updatePluginstringJavascript)
    public ResponseEntity<String> updatePluginstringJavascript(@RequestBody PluginUpdateJavascriptRequestDto r) {
        String result = connectionService.pm.updatePluginstringJavascript(r.getTyp(),r.getName(),r.getConfig(),r.getPluginDef(),r.getJsResult());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param r    LoadPluginRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.params    String: Plugin-Parameter<br>
     *   r.q         PluginQuestionDto: Frage wo das Plugin eingebettet ist<br>
     *   r.nr        int: Laufende Nummer für alle PIG-Tags und Question-Plugins<br>
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @Operation(
            summary = "berechne das PluginDto",
            description = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung <br>" +
                    "Body  : [LoadPluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/LoadPluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config : String - Konfigurationsstring des Plugins<br>" +
                    " - .params : String - Plugin-Parameter<br>" +
                    " - .q      : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Frage wo das Plugin eingebettet ist<br>" +
                    " - .nr     : int - Laufende Nummer für alle PIG-Tags und Question-Plugins<br>" +
                    "Result: [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.loadPluginDto)
    public ResponseEntity<PluginDto> loadPluginDto(@RequestBody LoadPluginRequestDto r) {
        PluginDto result = connectionService.pm.loadPluginDto(r.getTyp(),r.getName(),r.getConfig(),r.getParams(),r.getQ(),r.getNr());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param r    PluginRenderLatexRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.pluginDto PluginDto: Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>
     *   r.answer    String: Inhalt des Antwortfeldes welches der Schüler eingegeben hat<br>
     *   r.mode      String: Druckmode<br>
     * @return     PluginDto welches von LeTTo an JavaScript übergeben wird
     */
    @Operation(
            summary = "Latex rendern",
            description = "Rendert ein Plugins für den Fragedruck als Latex-Sourcode <br>" +
                    "Body  : [PluginRenderLatexRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRenderLatexRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config    : String - Konfigurationsstring des Plugins<br>" +
                    " - .pluginDto : [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html) -  Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>" +
                    " - .answer    : String - Inhalt des Antwortfeldes welches der Schüler eingegeben hat<br>" +
                    " - .mode      : String - Druckmode<br>" +
                    "Result: [PluginRenderDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRenderDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.renderLatex)
    public ResponseEntity<PluginRenderDto> renderLatex(@RequestBody PluginRenderLatexRequestDto r) {
        PluginRenderDto result = connectionService.pm.renderLatex(r.getTyp(),r.getName(),r.getConfig(),r.getPluginDto(),r.getAnswer(),r.getMode());
        return ResponseEntity.ok(result);
    }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param r    PluginRenderResultRequestDto:<br>
     *   r.typ          String: Typ des Plugins<br>
     *   r.name         String: Name des Plugins in der Frage<br>
     *   r.config       String: Konfigurationsstring des Plugins<br>
     *   r.tex          boolean: true für LaTeX-Code, false für html-Code<br>
     *   r.pluginDto    PluginDto: Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>
     *   r.antwort      String: Antwort die der Schüler eingegeben hat<br>
     *   r.toleranz     ToleranzDto: Toleranz für die Lösung<br>
     *   r.varsQuestion VarHashDto: VarHash aller Variablen der Frage<br>
     *   r.answerDto    PluginAnswerDto: korrekte Antwort und Informationen für den Scorer des Plugins<br>
     *   r.grade        double: Maximale Punktanzahl für die richtige Antwort<br>
     * @return     HTML-Code oder LaTeX-Code mit Bildern
     */
    @Operation(
            summary = "Plugin rendern",
            description = "Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung. Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt <br>" +
                    "Body  : [PluginRenderResultRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRenderResultRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config    : String - Konfigurationsstring des Plugins<br>" +
                    " - .tex    : boolean - true für LaTeX-Code, false für html-Code<br>" +
                    " - .pluginDto : [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html) -  Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird<br>" +
                    " - .antwort    : String - Antwort die der Schüler eingegeben hat<br>" +
                    " - .toleranz     : [ToleranzDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/ToleranzDto.html) - Toleranz für die Lösung<br>" +
                    " - .varsQuestion : [VarHashDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/math/dto/VarHashDto.html) - Alle Variablen der Frage<br>" +
                    " - .answerDto    : [PluginAnswerDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginAnswerDto.html) - korrekte Antwort und Informationen für den Scorer des Plugins<br>" +
                    " - .grade        : double - Maximale Punktanzahl für die richtige Antwort<br>" +
                    "Result: [PluginRenderDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginRenderDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.renderPluginResult)
    public ResponseEntity<PluginRenderDto> renderPluginResult(@RequestBody PluginRenderResultRequestDto r) {
        PluginRenderDto result = connectionService.pm.renderPluginResult(r.getTyp(),r.getName(),r.getConfig(),r.isTex(),r.getPluginDto(),r.getAntwort(),r.getToleranz(),r.getVarsQuestion(),r.getAnswerDto(),r.getGrade());
        return ResponseEntity.ok(result);
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param r    PluginConfigurationInfoRequestDto:<br>
     *   r.typ       String: Typ des Plugins<br>
     *   r.name      String: Name des Plugins in der Frage<br>
     *   r.config    String: Konfigurationsstring des Plugins<br>
     *   r.configurationID String:eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird<br>
     *   r.timeout   long:maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen<br>
     * @return     alle notwendigen Konfigurationen
     */
    @Operation(
            summary = "Konfigurations Information",
            description = "Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten. Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die Konfiguration aktiviert. <br>" +
                    "Body  : [PluginConfigurationInfoRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginConfigurationInfoRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config    : String - Konfigurationsstring des Plugins<br>" +
                    " - .configurationID    : String - eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird<br>" +
                    " - .timeout    : long - maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen<br>" +
                    "Result: [PluginConfigurationInfoDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginConfigurationInfoDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.configurationInfo)
    //public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
    public ResponseEntity<PluginConfigurationInfoDto> configurationInfo(@RequestBody PluginConfigurationInfoRequestDto r) {
        String configurationID = r.getConfigurationID();
        String typ             = r.getTyp();
        BasePluginConnectionService   pcs  = connectionService.getPluginConnectionService(typ);
        if (pcs!=null) {
            PluginConfigurationInfoDto result = pcs.configurationInfo(r.getTyp(),r.getName(),r.getConfig(),r.getConfigurationID(),r.getTimeout());
            result.setConfigurationUrl(connectionService.getBaseUriExtern()+Endpoint.iframeConfig+
                    "?typ="+typ+"&configurationID="+configurationID);
            return ResponseEntity.ok(result);
        }
        return null;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param r    PluginSetConfigurationDataRequestDto:<br>
     *   r.configurationID String:zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)<br>
     *   r.configuration   String:aktueller Konfigurations-String des Plugins<br>
     *   r.questionDto     PluginQuestionDto: Question-DTO mit Varhashes<br>
     * @return     Fehlermeldung wenn etwas nicht korrekt funktioniert hat
     */
    @Operation(
            summary = "setze Konfigurationsdaten",
            description = "Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration <br>" +
                    "Body  : [PluginSetConfigurationDataRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginSetConfigurationDataRequestDto.html)<br>" +
                    " - .configurationID    : String - eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird<br>" +
                    " - .configuration      : String - aktueller Konfigurationsstring des Plugins<br>" +
                    " - .questionDto        : [PluginQuestionDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginQuestionDto.html) - Question-DTO mit Varhashes<br>" +
                    "Result: [PluginConfigDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginConfigDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.setConfigurationData)
    public ResponseEntity<PluginConfigDto> setConfigurationData(@RequestBody PluginSetConfigurationDataRequestDto r) {
        PluginConfigDto pluginConfigDto = connectionService.pm.setConfigurationData(r.getTyp(), r.getConfigurationID(),r.getConfiguration(),r.getQuestionDto());
        pluginConfigDto.setPluginDtoUri(connectionService.getUriGetPluginDto());
        pluginConfigDto.setPluginDtoToken("");
        return ResponseEntity.ok(pluginConfigDto);
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param      dto    typ und Configuration-ID<br>
     *   r.typ              String: PluginTyp<br>
     *   r.configurationID  String:zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)<br>
     * @return     Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Operation(
            summary = "liefert die Konfigurationsdaten",
            description = "Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet <br>" +
                    "Body  : [PluginConfigurationRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginConfigurationRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .configurationID    : String - zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)<br>" +
                    "Result: String - Konfigurationsstring des Plugins"
    )
    @PostMapping(PluginConnectionEndpoint.getConfiguration)
    public ResponseEntity<String> getConfiguration(@RequestBody PluginConfigurationRequestDto dto) {
        String result = connectionService.pm.getConfiguration(dto.getTyp(), dto.getConfigurationID());
        return ResponseEntity.ok(result);
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
            description = "Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung <br>" +
                    "Body  : [LoadPluginRequestDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/LoadPluginRequestDto.html)<br>" +
                    " - .typ  : String - Typ des Plugins<br>" +
                    " - .name : String - Name des Plugins in der Frage<br>" +
                    " - .config    : String - Konfigurationsstring des Plugins<br>" +
                    " - .params    : String - Plugin-Parameter<br>" +
                    " - .nr        : int - Laufende Nummer für alle PIG-Tags und Question-Plugins<br>" +
                    " - .configurationID    : String - ID der aktuellen Konfiguration<br>" +
                    "Result: [PluginDto](https://build.letto.at/pluginuhr/open/javadoc/at/letto/plugins/dto/PluginDto.html)"
    )
    @PostMapping(PluginConnectionEndpoint.reloadPluginDto)
    public ResponseEntity<PluginDto> reloadPluginDto(@RequestBody LoadPluginRequestDto r) {
        String configurationID = r.getConfigurationID();
        String typ = r.getTyp();
        // Suche zuerst das richtige PluginConnectionService zu dem angegebenen Plugin
        BasePluginConnectionService   pcs  = connectionService.getPluginConnectionService(typ);
        if (pcs!=null) {
            // Suche nun eine bestehende Verbindung mit der confingurationID
            PluginConfigurationConnection conn = pcs.getConfigurationConnection(typ, configurationID);
            if (conn != null) {
                // Lade das Plugin mit der Konfiguration aus den Request-Parametern
                PluginService pluginService = pcs.createPluginService(r.getTyp(),conn.getName(),r.getConfig());
                // aktualisiere die Verbindung, damit dann LeTTo die Konfiguration wieder korrekt abrufen kann
                conn.changeConfig(r.getConfig(), pluginService);
                PluginDto result;
                // erzeuge das PluginDto welches dann zum Rendern des Plugins verwendet wird
                result = pcs.loadPluginDto(r.getTyp(),conn.getName(),r.getConfig(),r.getParams(),conn.pluginQuestionDto,r.getNr());
                return ResponseEntity.ok(result);
            }
        }
        return null;
    }

}
