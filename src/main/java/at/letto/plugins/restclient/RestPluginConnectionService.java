package at.letto.plugins.restclient;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.math.enums.CALCERGEBNISTYPE;
import at.letto.plugins.dto.*;
import at.letto.plugins.endpoints.PluginConnectionEndpoint;
import at.letto.service.rest.BaseRestClient;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.enums.Score;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Pluginconnectionservices welches eine Verbindung zu einem Spring-Boot-Service über die
 * REST-Schnittstelle herstellt. Sämtliche Verbindungen zum Plugin müssen über die Rest-Schnittstelle an
 * das Microservice weitergeleitet werden!
 */
public class RestPluginConnectionService extends BaseRestClient implements PluginConnectionService {

    private List<PluginGeneralInfo> plugins=new ArrayList<>();

    private long lastcheck=0;
    private final long CHECK_INTERVAL_MS = 3600000L;

    public RestPluginConnectionService(String baseURI) {
        super(baseURI);
        loadPluginGeneralInfoList();
    }

    public RestPluginConnectionService(String baseURI, String user, String password) {
        super(baseURI, user, password);
        loadPluginGeneralInfoList();
    }

    /**
     * @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden
     */
    @Override
    public List<String> getPluginList() {
        List<String> pluginList = get(PluginConnectionEndpoint.getPluginList, List.class);
        return pluginList;
    }

    /**
     * @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services
     */
    @Override
    public List<PluginGeneralInfo> getPluginGeneralInfoList() {
        if (plugins==null || (System.currentTimeMillis()-lastcheck)>CHECK_INTERVAL_MS) {
            loadPluginGeneralInfoList();
        }
        return plugins;
    }

    private void loadPluginGeneralInfoList() {
        PluginGeneralInfoList pluginList = get(PluginConnectionEndpoint.getPluginGeneralInfoList, PluginGeneralInfoList.class);
        if (pluginList!=null && pluginList.getPluginInfos()!=null) {
            lastcheck = System.currentTimeMillis();
            plugins = pluginList.getPluginInfos();
        }
    }

    /**
     * @param typ Plugin Typ
     * @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @Override
    public PluginGeneralInfo getPluginGeneralInfo(String typ) {
        for (PluginGeneralInfo pi:plugins) {
            if (pi.getTyp().trim().equals(typ.trim())) return pi;
        }
        PluginGeneralInfo pluginInfo = post(PluginConnectionEndpoint.getPluginGeneralInfo, typ, PluginGeneralInfo.class);
        if (pluginInfo!=null) {
            plugins.add(pluginInfo);
            return pluginInfo;
        }
        return null;
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Antworterzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return HTML Text
     */
    @Override
    public String getHTML(String typ, String name, String config, String params, PluginQuestionDto q) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,params,q,null);
        String html = post(PluginConnectionEndpoint.getHTML, r, String.class);
        if (html==null) return "";
        return html;
    }

    /**
     * Liefert einen Angabestring für die MoodleText Angabe
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Einstellungen
     * @return String für das MoodleText-Feld
     */
    @Override
    public String getAngabe(String typ, String name, String config, String params) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,params,null,null);
        String angabe = post(PluginConnectionEndpoint.getAngabe, r, String.class);
        if (angabe==null) return "";
        return angabe;
    }

    /**
     * Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liste der Datensatzdefinitionen welche vom Plugin in der Frage angefordert werden
     */
    @Override
    public List<PluginDatasetDto> generateDatasets(String typ, String name, String config) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,null,null,null);
        PluginDatasetListDto result = post(PluginConnectionEndpoint.generateDatasets, r, PluginDatasetListDto.class);
        if (result==null || result.getDatasets()==null) return new ArrayList<>();
        return result.getDatasets();
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     *
     * @param typ                  Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config               Konfigurationsstring des Plugins
     * @param params               Parameter
     * @param q                    Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return Maxima Berechnungs-String
     */
    @Override
    public String getMaxima(String typ, String name, String config, String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,params,q,pluginMaximaCalcMode);
        String result = post(PluginConnectionEndpoint.getMaxima, r, String.class);
        if (result==null) return "";
        return result;
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild in einem MoodleFile
     */
    @Override
    public ImageBase64Dto getImage(String typ, String name, String config, String params, PluginQuestionDto q) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,params,q,null);
        ImageBase64Dto result = post(PluginConnectionEndpoint.getImage, r, ImageBase64Dto.class);
        return result;
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liefert eine Liste aller möglichen Varianten von Bildern
     */
    @Override
    public Vector<String[]> getImageTemplates(String typ, String name, String config) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,null,null,null);
        Vector<String[]> result = post(PluginConnectionEndpoint.getImageTemplates, r, Vector.class);
        if (result==null) return new Vector<>();
        return result;
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param vars   Alle Variablen der Frage
     * @param cp     Berechnungsparameter
     * @param p      Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return Ergebnis der Funktion
     */
    @Override
    public CalcErgebnisDto parserPlugin(String typ, String name, String config, VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto... p) {
        PluginParserRequestDto r = new PluginParserRequestDto(typ,name,config,vars,cp,p);
        CalcErgebnisDto result = post(PluginConnectionEndpoint.parserPlugin, r, CalcErgebnisDto.class);
        return result;
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param p      Einheiten der Parameter als Recheneinheiten
     * @return Recheneinheit des Ergebnisses
     */
    @Override
    public String parserPluginEinheit(String typ, String name, String config, String... p) {
        PluginEinheitRequestDto r = new PluginEinheitRequestDto(typ,name,config,p);
        String result = post(PluginConnectionEndpoint.parserPluginEinheit, r, String.class);
        if (result==null) return "";
        return result;
    }

    /**
     * Prüft die Eingabe eines Schülers
     *
     * @param typ          Typ des Plugins
     * @param name         Name des Plugins in der Frage
     * @param config       Konfigurationsstring des Plugins
     * @param pluginDto    PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort      Antwort die der Schüler eingegeben hat
     * @param toleranz     Toleranz für die Lösung
     * @param varsQuestion Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto    Antwort des Schülers
     * @param grade        Maximale Punktanzahl für die richtige Antwort
     * @return Bewertung
     */
    @Override
    public PluginScoreInfoDto score(String typ, String name, String config, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade) {
        PluginScoreRequestDto r = new PluginScoreRequestDto(typ,name,config,pluginDto,antwort,toleranz,varsQuestion,answerDto,grade);
        PluginScoreInfoDto result = post(PluginConnectionEndpoint.score, r, PluginScoreInfoDto.class);
        if (result==null)
            new PluginScoreInfoDto(new CalcErgebnisDto(antwort,"", CALCERGEBNISTYPE.STRING),
                    answerDto.getZe(), 0, grade, Score.FALSCH,"score-error!","");
        return result;
    }

    @Override
    public Vector<String> getVars(String typ, String name, String config) {
        PluginRequestDto r = new PluginRequestDto(typ,name,config,null,null,null);
        Vector<String> result = post(PluginConnectionEndpoint.getVars, r, Vector.class);
        if (result==null) return new Vector<>();
        return result;
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text   Text der innerhalb der PI Tags gestanden ist
     * @param q      Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return veränderter Text
     */
    @Override
    public String modifyAngabe(String typ, String name, String config, String text, PluginQuestionDto q) {
        PluginAngabeRequestDto r = new PluginAngabeRequestDto(typ,name,config,text,q);
        String result = post(PluginConnectionEndpoint.modifyAngabe, r, String.class);
        if (result==null) return "";
        return result;
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text   Angabetext der Frage
     * @param q      Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return veränderter AngabeText
     */
    @Override
    public String modifyAngabeTextkomplett(String typ, String name, String config, String text, PluginQuestionDto q) {
        PluginAngabeRequestDto r = new PluginAngabeRequestDto(typ,name,config,text,q);
        String result = post(PluginConnectionEndpoint.modifyAngabeTextkomplett, r, String.class);
        if (result==null) return "";
        return result;
    }

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     *
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDef akt. Plugin-Definition
     * @param jsResult  Rückgabe von Javascript
     * @return aktualiesierte Plugindefinition
     */
    @Override
    public String updatePluginstringJavascript(String typ, String name, String config, String pluginDef, String jsResult) {
        PluginUpdateJavascriptRequestDto r = new PluginUpdateJavascriptRequestDto(typ,name,config,pluginDef,jsResult);
        String result = post(PluginConnectionEndpoint.updatePluginstringJavascript, r, String.class);
        if (result==null) return "";
        return result;
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params        Plugin-Parameter
     * @param q             Question, in die das Plugin eingebettet ist
     * @param nr            Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return              PluginDto
     */
    @Override
    public PluginDto loadPluginDto(String typ, String name, String config, String params, PluginQuestionDto q, int nr) {
        /** @return Plugin wird durch einen PIG-Tag aufgerufen
        boolean isPig();
        void setPig(boolean pig);
        /** @return Plugin ist in einer Subquestion definiert */
        LoadPluginRequestDto r = new LoadPluginRequestDto(typ,name,config,params,q,nr,"");
        PluginDto result = post(PluginConnectionEndpoint.loadPluginDto, r, PluginDto.class);
        return result;
    }

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDto Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return          Latexsourcode und zugehörige Bilder in einer Hashmap
     */
    @Override
    public PluginRenderDto renderLatex(String typ, String name, String config, PluginDto pluginDto, String answer, String mode) {
        PluginRenderLatexRequestDto r = new PluginRenderLatexRequestDto(typ,name,config,pluginDto,answer,mode);
        PluginRenderDto result = post(PluginConnectionEndpoint.renderLatex, r, PluginRenderDto.class);
        return result;
    }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param typ                 Typ des Plugins
     * @param name                Name des Plugins in der Frage
     * @param config              Konfigurationsstring des Plugins
     * @param tex                 true für LaTeX-Code, false für html-Code
     * @param pluginDto           PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto           Antwort des Schülers
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    HTML-Code oder LaTeX-Code mit Bildern
     */
    @Override
    public PluginRenderDto renderPluginResult(String typ, String name, String config, boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade){
        PluginRenderResultRequestDto r = new PluginRenderResultRequestDto(typ,name,config,tex,pluginDto,antwort,toleranz,varsQuestion,answerDto,grade);
        PluginRenderDto result = post(PluginConnectionEndpoint.renderPluginResult, r, PluginRenderDto.class);
        return result;
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param typ                Typ des Plugins
     * @param name               Name des Plugins in der Frage
     * @param config             Konfigurationsstring des Plugins
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @param timeout            maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen
     * @return                   alle notwendigen Konfig
     */
    @Override
    public PluginConfigurationInfoDto configurationInfo(String typ, String name, String config, String configurationID, long timeout){
        PluginConfigurationInfoRequestDto r = new PluginConfigurationInfoRequestDto(typ,name,config,configurationID,timeout);
        PluginConfigurationInfoDto result = post(PluginConnectionEndpoint.configurationInfo, r, PluginConfigurationInfoDto.class);
        return result;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     */
    @Override
    public PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto) {
        PluginSetConfigurationDataRequestDto r = new PluginSetConfigurationDataRequestDto(typ, configurationID,configuration,questionDto);
        PluginConfigDto result = post(PluginConnectionEndpoint.setConfigurationData, r, PluginConfigDto.class);
        return result;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param configurationID zu verwendende Konfigurations-ID
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Override
    public String getConfiguration(String typ, String configurationID) {
        PluginConfigurationRequestDto data = new PluginConfigurationRequestDto(typ,configurationID);
        String result = post(PluginConnectionEndpoint.getConfiguration, data, String.class);
        if (result==null) return "";
        return result;
    }






}
