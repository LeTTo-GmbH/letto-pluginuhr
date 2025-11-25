package at.letto.plugins.restclient;

import at.letto.config.LeTToProperties;
import at.letto.globalinterfaces.ImageService;
import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.plugins.dto.*;
import at.letto.tools.Cmd;
import at.letto.tools.JSON;
import at.letto.tools.JavascriptLibrary;
import at.letto.tools.dto.ImageBase64Dto;
import lombok.Getter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Implementierung welche lokale Plugins und auch externe Plugin-Services verwalten kann
 */
public class BasePluginManagerService implements PluginManagerService {

    /** alle JavaScript-Libraries zum gemeinsamen Speichern */
    protected HashMap<String,String> jsLibs;

    @Getter private String publicJs = null;

    private Vector<PluginConnectionService> pluginConnections;

    @Getter private List<JavascriptLibrary> javascriptLibraries = new ArrayList<>();
    @Getter private List<JavascriptLibrary> javascriptLibrariesLocal = new ArrayList<>();

    public BasePluginManagerService(){
        jsLibs = new HashMap<>();
        pluginConnections = new Vector<>();
        /** Prüft ob das Verzeichnis public_js vorhanden ist - dort werden alle JavaScript und Css-Dateien aus den Resourcen gespeichert */
        try {
            String letto_public_js = LeTToProperties.getEnv("public_js");
            if (letto_public_js!=null) publicJs = letto_public_js;
            if (isPublicJs())
                try {
                    while (publicJs.endsWith("/") || publicJs.endsWith("\\"))
                        publicJs = publicJs.substring(0,publicJs.length()-1);
                    File dir = new File(publicJs);
                    if (!dir.exists()) dir.mkdirs();
                    if (!dir.exists()) throw new Exception("Fehler Libs können nicht geschrieben werden!!");
                } catch (Exception ex) {
                    System.out.println("ERROR: JavaScript-Libraries können nicht nach "+publicJs+" gespeichert werden !!");
                    publicJs=null;
                }
        } catch (Exception ex) {}
    }

    /**
     * Registriert ein PluginConnectionService im PluginManagerService
     * @param connection PluginConnection zu einem Plugin-Service
     */
    @Override
    public void registerPluginConnectionService(PluginConnectionService connection) {
        // Prüfe ob das Service schon registriert ist
        boolean registrated = false;
        for (PluginConnectionService conn:pluginConnections)
            if (conn==connection)
                registrated=true;
        if (!registrated) {
            pluginConnections.add(connection);
            if (isPublicJs()) {
                //speichere alle Java-Script und CSS-Dateien in das public_js Verzeichnis
                for (PluginGeneralInfo info: connection.getPluginGeneralInfoList()) {
                    for (JavascriptLibrary lib : info.getJavascriptLibrariesLocal())
                        if (lib.getName()!=null && lib.getName().length()>0 && lib.getJs_code()!=null && lib.getJs_code().length()>0) {
                            Cmd.writefile(lib.getJs_code(), publicJs + "/" + lib.getName());
                            jsLibs.put(lib.getName(), lib.getJs_code());
                        }
                }
            }

            // ermittle alle globalen Java-Script Libraries
            List<PluginGeneralInfo> infos = getPluginGeneralInfoList();
            List<JavascriptLibrary> libs=new ArrayList<>();
            for (PluginGeneralInfo info:infos)
                for (JavascriptLibrary lib:info.getJavascriptLibraries()) {
                    boolean found=false;
                    for (JavascriptLibrary libx : libs)
                        if (libx.equals(lib))
                            found=true;
                    if (!found)
                        libs.add(lib);
                }
            this.javascriptLibraries = libs;

            // ermittle alle lokalen Java-Script Libraries
            libs=new ArrayList<>();
            for (PluginGeneralInfo info:infos)
                for (JavascriptLibrary lib:info.getJavascriptLibrariesLocal()) {
                    boolean found=false;
                    for (JavascriptLibrary libx:libs)
                        if (libx.equals(lib))
                            found=true;
                    if (!found)
                        libs.add(lib);
                }
            this.javascriptLibrariesLocal = libs;
        }
    }

    /** speichert alle registrierten JS-Libraries in einer Datei */
    public void saveJsLibs() {
        StringBuilder sb = new StringBuilder();
        for(String libname:jsLibs.keySet()) {
            sb.append(jsLibs.get(libname));
            sb.append("\n");
        }
        Cmd.writefile(sb.toString(), publicJs + "/plugins.js");
    }

    @Override
    public boolean isPublicJs() {
        if (publicJs!=null && publicJs.length()>0) return true;
        return false;
    }

    /** @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden */
    @Override
    public List<String> getPluginList() {
        List<String> pluginList = new ArrayList<>();
        for (PluginConnectionService conn:pluginConnections)
            if (conn.getPluginList()!=null) for (String plugin:conn.getPluginList())
                pluginList.add(plugin);
        return pluginList;
    }

    /**
     * @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services
     */
    @Override
    public List<PluginGeneralInfo> getPluginGeneralInfoList() {
        List<PluginGeneralInfo> pluginList = new ArrayList<>();
        for (PluginConnectionService conn:pluginConnections)
            for (PluginGeneralInfo plugin:conn.getPluginGeneralInfoList())
                pluginList.add(plugin);
        return pluginList;
    }

    @Override
    public PluginConnectionService getPluginConnectionService(String typ) {
        for (PluginConnectionService conn:pluginConnections)
            for (String plugin:conn.getPluginList())
                if (typ.equals(plugin)) {
                    //TODO Werner : Prüfe ggf. ob das Service erreichbar ist
                    return conn;
                }
        return null;
    }

    /**
     * @param typ Plugin Typ
     * @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin
     */
    @Override
    public PluginGeneralInfo getPluginGeneralInfo(String typ) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getPluginGeneralInfo(typ);
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getHTML(typ,name,config,params,q);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getAngabe(typ,name,config,params);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.generateDatasets(typ,name,config);
        return null;
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     *
     * @param typ                  Typ des Plugins
     * @param name                 Name des Plugins in der Frage
     * @param config               Konfigurationsstring des Plugins
     * @param params               Parameter
     * @param q                    Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return Maxima Berechnungs-String
     */
    @Override
    public String getMaxima(String typ, String name, String config, String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getMaxima(typ,name,config,params,q,pluginMaximaCalcMode);
        return null;
    }

    /**
     * Liefert einen String welcher die Parameter eines Plugins exakt beschreibt um eine Prüfsumme davon zu bilden
     * @param typ     Typ des Plugins
     * @param name    Name des Plugins
     * @param config  Konfigurationsparameter-String
     * @param params  Parameter des PIG-Tags
     * @param q       Frage in der das Plugin eingebettet ist
     * @return        String als Kombination aus alle den Parametern
     */
    @Override
    public String getPluginImageDescription(String typ, String name, String config, String params, PluginQuestionDto q) {
        try {
            String description = "pluginversion:" + getPluginGeneralInfo(typ).getVersion() + "typ:" + typ + ", name:" + name + ", config:" + config + ", params:" + params + ", question:" + JSON.objToJson(q);
            return description;
        } catch (Exception ex) { }
        return null;
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild
     */
    @Override
    public ImageBase64Dto getImage(String typ, String name, String config, String params, PluginQuestionDto q) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getImage(typ,name,config,params,q);
        return null;
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern und speichert es in einem ImageService
     * @param imageService ImageService mit dem das Bild gespeichert wird
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild
     */
    @Override
    public ImageBase64Dto getImage(ImageService imageService, String typ, String name, String config, String params, PluginQuestionDto q) {
        if (imageService==null) return getImage(typ,name,config,params,q);
        ImageBase64Dto image = null;
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn==null) return null;

        PluginGeneralInfo pluginGeneralInfo = conn.getPluginGeneralInfo(typ);
        String description = getPluginImageDescription(typ,name,config,params,q);
        String filename = ImageService.generateFilename(description,"png");
        // Berechnung des Bildes und speichern mit dem imageService
        if (filename.length()>0)
            try {
                if (imageService.existImage(filename)) {
                    image = imageService.loadImageBase64Dto(filename);
                    if (image!=null && image.getImageInfoDto()==null) image=null;
                    if (image!=null && image.getImageInfoDto().lifetimeOutdated())
                        image=null;
                }
                if (image==null) {
                    image = conn.getImage(typ,name,config,params,q);
                    image.getImageInfoDto().setFilename(filename);
                    imageService.saveImage(image);
                }
            } catch (Exception ignored) {  }
        return image;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getImageTemplates(typ,name,config);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.parserPlugin(typ,name,config,vars,cp,p);
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.parserPluginEinheit(typ,name,config,p);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.score(typ,name,config,pluginDto,antwort,toleranz,varsQuestion,answerDto,grade);
        return null;
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     *
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liste aller Variablen des Plugins
     */
    @Override
    public Vector<String> getVars(String typ, String name, String config) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getVars(typ,name,config);
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.modifyAngabe(typ,name,config,text,q);
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.modifyAngabeTextkomplett(typ,name,config,text,q);
        return "";
    }

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDef akt. Plugin-Definition
     * @param jsResult  Rückgabe von Javascript
     * @return aktualiesierte Plugindefinition
     */
    @Override
    public String updatePluginstringJavascript(String typ, String name, String config, String pluginDef, String jsResult) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.updatePluginstringJavascript(typ,name,config,pluginDef,jsResult);
        return "";
    }

    /**
     * Liefert eine Liste von Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden!
     * @return	für alle Plugin notwendige JS-Libraries
    @Override
    public List<JavascriptLibrary> getJavascriptLibraries() {
        List<PluginGeneralInfo> infos = getPluginGeneralInfoList();
        List<JavascriptLibrary> libs=new ArrayList<>();
        for (PluginGeneralInfo info:infos)
            for (JavascriptLibrary lib:info.getJavascriptLibraries()) {
                boolean found=false;
                for (JavascriptLibrary libx : libs)
                    if (libx.equals(lib) || libx.toString().equals(lib.toString()))
                        found=true;
                if (!found)
                    libs.add(lib);
            }
        return libs;
    }*/

    /**
     * Liefert eine Liste von LOKALEN Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     * @return	für alle Plugin notwendige JS-Libraries

    @Override
    public List<JavascriptLibrary> getJavascriptLibrariesLocal() {
        System.out.println("GetJSLibLocal-START:"+System.currentTimeMillis());
        List<PluginGeneralInfo> infos = getPluginGeneralInfoList();
        List<JavascriptLibrary> libs=new ArrayList<>();
        for (PluginGeneralInfo info:infos)
            for (JavascriptLibrary lib:info.getJavascriptLibrariesLocal()) {
                boolean found=false;
                for (JavascriptLibrary libx:libs)
                    if (libx.equals(lib))
                        found=true;
                if (!found)
                    libs.add(lib);
            }
        System.out.println("GetJSLibLocal-STOP:"+System.currentTimeMillis());
        return libs;
    }*/

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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.loadPluginDto(typ,name,config,params,q,nr);
        return null;
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param imageService  ImageService für den persistenten Bild-Cache
     * @param typ           Typ des Plugins
     * @param name          Name des Plugins in der Frage
     * @param config        Konfigurationsstring des Plugins
     * @param params        Plugin-Parameter
     * @param q             Question, in die das Plugin eingebettet ist
     * @param nr            Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return              PluginDto
     */
    @Override
    public PluginDto loadPluginDto(ImageService imageService, String typ, String name, String config, String params, PluginQuestionDto q, int nr) {
        //TODO Werner: implementiere den persistenen Bilder-Cache mit einem ImageService oder auch anders
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.loadPluginDto(typ,name,config,params,q,nr);
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
    public PluginRenderDto renderLatex(String typ, String name, String config, PluginDto pluginDto, String answer, String mode){
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.renderLatex(typ,name,config,pluginDto,answer,mode);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.renderPluginResult(typ,name,config,tex,pluginDto,antwort,toleranz,varsQuestion,answerDto,grade);
        return null;
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
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.configurationInfo(typ,name,config,configurationID,timeout);
        return null;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param typ                Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Fehlermeldung wenn etwas nicht korrekt funktioniert hat
     */
    @Override
    public PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.setConfigurationData(typ, configurationID,configuration,questionDto);
        return null;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param typ                Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Override
    public String getConfiguration(String typ, String configurationID) {
        PluginConnectionService conn = getPluginConnectionService(typ);
        if (conn!=null) return conn.getConfiguration(typ, configurationID);
        return null;
    }


}
