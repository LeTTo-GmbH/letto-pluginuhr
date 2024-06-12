package at.letto.plugins.restclient;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.math.enums.CALCERGEBNISTYPE;
import at.letto.plugins.dto.*;
import at.letto.plugins.interfaces.PluginService;
import at.letto.service.rest.BaseRestClient;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.enums.Score;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Pluginconnectionservices welches eine Verbindung zu einem Spring-Boot-Service über die
 * REST-Schnittstelle herstellt. Aber andererseits auch das Plugin direkt intern mit der JSF-Schnittstelle
 * konfigurieren kann.
 *
 * Dieses PluginConnectionService sollte später entfernt werden, genauso wie auch das BasePluginConnectionService
 */
public class RestPluginConnectionServiceJSF extends BaseRestClient implements PluginConnectionService {

    protected HashMap<String, PluginGeneralInfo> plugins=new HashMap<>();

    public RestPluginConnectionServiceJSF(String baseURI) {
        super(baseURI);
    }

    public RestPluginConnectionServiceJSF(String baseURI, String user, String password) {
        super(baseURI, user, password);
    }

    /**
     * Registriert ein Plugin im Services,
     * @param typ         Name des Plugins
     * @param classname   Klassenpfad des Plugins
     */
    protected void registerPlugin(String typ, String classname) {
        try {
            Class<?> c = Class.forName(classname);
            Constructor<?> constr = c.getConstructor(String.class, String.class);
            PluginService pi = (PluginService) constr.newInstance("", "");
            PluginGeneralInfo info = pi.getPluginGeneralInfo();
            info.setTyp(typ);
            info.setPluginType(classname);
            plugins.put(typ, info);
        } catch (NoClassDefFoundError e) {
        } catch (Exception e) {
        }
    }

    /**
     * @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden
     */
    @Override
    public List<String> getPluginList() {
        List<String> pluginList = new ArrayList<>();
        for (String typ:plugins.keySet()) pluginList.add(typ);
        return pluginList;
    }

    /**
     * @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services
     */
    @Override
    public List<PluginGeneralInfo> getPluginGeneralInfoList() {
        List<PluginGeneralInfo> pluginList = new ArrayList<>();
        for (String typ:plugins.keySet()) pluginList.add(plugins.get(typ));
        return pluginList;
    }

    /**
     * @param typ Plugin Typ
     * @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @Override
    public PluginGeneralInfo getPluginGeneralInfo(String typ) {
        if (plugins.containsKey(typ)) return plugins.get(typ);
        return null;
    }

    /**
     * Erzeugt aus dem Plugin-Typ und den Parametern ein PluginService Objekt<br>
     * Sollte nur intern im SpringBoot-Service verwendet werden!<br>
     * Bis der Plugin-Config-Dialog umgestellt ist wird die Funktion auch von
     * JSF für den Konfigurationsdialog verwendet, sollte später aber auf
     * private gesetzt werden!!
     * @param typ     Typ des Plugins
     * @param name    Namen des Plugins in  der Frage
     * @param params  Parameterstring des Plugins für die Konfiguration
     * @return        erzeugtes Plugin
     */
    public PluginService createPluginService(String typ, String name, String params) {
        //TODO Werner : setzte das createPluginService auf private wenn es aus JSF entfernt wurde
        //TODO Thomas : Baue den Plugin-Configuration-Dialog so um, dass das Plugin in JSF nicht mehr erzeugt werden muss!!
        if (plugins.containsKey(typ)) {
            try {
                PluginGeneralInfo info = plugins.get(typ);
                Class<?> c = Class.forName(info.getPluginType());
                Constructor<?> constr = c.getConstructor(String.class, String.class);
                PluginService pi = (PluginService) constr.newInstance(name, params);
                return pi;
            } catch (Exception ex) {};
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getHTML(params, q);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getAngabe(params);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.generateDatasets();
        return new ArrayList<>();
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getMaxima(params, q, pluginMaximaCalcMode);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getImageDto(params, q);
        return null;
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getImageTemplates();
        return new Vector<>();
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.parserPlugin(vars,cp,p);
        return null;
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.parserPluginEinheit(p);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.score(pluginDto, antwort, toleranz, varsQuestion, answerDto, grade);
        return new PluginScoreInfoDto(new CalcErgebnisDto(antwort,"", CALCERGEBNISTYPE.STRING),
                answerDto.getZe(), 0, grade, Score.FALSCH,"score-error!","");
    }

    @Override
    public Vector<String> getVars(String typ, String name, String config) {
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.getVars();
        return new Vector<>();
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.modifyAngabe(text,q);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.modifyAngabeTextkomplett(text,q);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.updatePluginstringJavascript(pluginDef,jsResult);
        return "";
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.loadPluginDto(params,q,nr);
        return null;
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.renderLatex(pluginDto,answer,mode);
        return new PluginRenderDto();
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.renderPluginResult(tex,pluginDto,antwort,toleranz,varsQuestion,answerDto,grade);
        return new PluginRenderDto();
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
        PluginService pluginService = createPluginService(typ, name, config);
        if (pluginService != null) return pluginService.configurationInfo(configurationID);
        return new PluginConfigurationInfoDto();
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Fehlermeldung wenn etwas nicht korrekt funktioniert hat
     */
    @Override
    public PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto) {
        //FIXME Implement !!
        return null;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param configurationID zu verwendende Konfigurations-ID
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Override
    public String getConfiguration(String typ, String configurationID) {
        //FIXME Implement !!
        return "";
    }

}
