package at.letto.plugins.service;

import at.letto.globalinterfaces.ImageService;
import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.enums.InputElement;
import at.letto.plugins.interfaces.PluginService;
import at.letto.tools.JSON;
import at.letto.tools.JavascriptLibrary;
import at.letto.tools.RegExp;
import at.letto.tools.dto.IMAGEUNIT;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.dto.ImageInfoDto;
import at.letto.tools.html.HTMLtool;
import at.letto.tools.threads.CallAdapter;
import at.letto.tools.threads.LettoTimer;
import at.letto.tools.threads.TimerCall;
import lombok.Getter;
import lombok.Setter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class BasePlugin implements PluginService {

    // ---------------------------------------------------------------------------------------------------------------------
    //        Eigenschaften
    // ---------------------------------------------------------------------------------------------------------------------
    /** Name des Elements */
    @Setter protected String name;

    /** Namen der Wiki-Seite wenn eine Doku am LeTTo-Wiki vorliegt */
    protected String wikiHelp="Plugins";

    /** Hilfe-URL für die Beschreibung des Plugins */
    protected String helpUrl="";

    /** Gibt an ob die Standard-Plugin-Configuration verwendet werden soll */
    protected boolean defaultPluginConfig=false;

    /** Konfigurationsstring des Elements */
    protected String config;

    /** Typ des Plugins */
    protected String typ;

    /** Breite des zu erzeugenden Bildes */
    protected int width  = 400;

    /** Höhe des zu erzeugenden Bildes */
    protected int height = 400;

    /** Größe des Bildes in Prozent */
    protected int imageWidthProzent=100;

    /** True wenn das Plugin CalcErgebnis und VarHash als JSON verarbeiten kann */
    protected boolean math = false;

    /** Version des Plugins */
    protected String   version = "1.0";

    /** Plugin Hilfe als HTML für den Plugin - Dialog */
    protected String[] helpfiles = {"plugins/plugin.html"};

    /** Javascript Libraries für das Plugin */
    protected String[] javascriptLibs = {"plugins/plugintools.js"};

    /** Name der JAVA-Script Methode zur Plugin-Initialisierung für die interaktive Ergebniseingabe */
    protected String initPluginJS="";

    /** gibt an ob das Plugin eine Java-Script Schnittstelle bei der Beispieldarstellung hat */
    protected boolean javaScript=true;

    /** Plugin ist stateless und liefert bei gleicher Angabe immer das gleiche Verhalten */
    protected boolean cacheable = true;

    /** Gibt an ob im Plugin die Frage benötigt wird */
    protected boolean useQuestion=true;

    /** gibt an ob die Datensatz-Variable ohne Konstante benötigt werden */
    protected boolean useVars=true;

    /** gibt an ob die Datensatz-Variable mit Konstanten benötigt werden */
    protected boolean useCVars=true;

    /** gibt an ob die Maxima-Durchrechnungen ohne eingesetzte Datensätze benötigt werden */
    protected boolean useMaximaVars=true;

    /** gibt an ob die Maxima-Durchrechnungen mit eingesetzten Datensätzen benötigt werden */
    protected boolean useMVars=true;

    /** Konfigurations-Mode für die Konfiguration des Plugins */
    protected int configurationMode = PluginConfigurationInfoDto.CONFIGMODE_JSF;

    /** Gibt an, ob im Plugin-Konfig-Dialog Datensätze hinzugefügt werden können.
     * => Button AddDataset in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    protected boolean addDataSet=true;

    /** Gibt an, ob das Plugin über den Browser direkt erreichbar ist */
    protected boolean externUrl=false;

    /** Gibt an ob im Plugin bei der Konfiguration die Maxima-Berechnung durchlaufen werden kann.
     * => Button Maxima in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    protected boolean calcMaxima=true;

    /** Name der JAVA-Script Methode zur Configuration des Plugins */
    protected String  configPluginJS="configPlugin";

    /** URL des Plugin-Services für die direkte Kommunikation*/
    protected String pluginServiceURL="";

    // ---------------------------------------------------------------------------------------------------------------------
    //        Constructor
    // ---------------------------------------------------------------------------------------------------------------------
    /**
     * Erzeugt ein Question Plugin
     * @param name   Name des Plugins
     * @param params Parameter des Plugins
     */
    public BasePlugin(String name, String params) {
        this.name   = name;
        this.config = params;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    //        Methoden
    // ---------------------------------------------------------------------------------------------------------------------
    /** @return Liefert den Typ des Plugins - Klasse des Plugins  */
    @Override
    public String getPluginType() {
        return this.getClass().toString();
    }

    /** @return Liefert den String welcher für die Definition des Plugins gespeichert wird [PI name typ "config"] */
    @Override
    public String getTag() {
        return "[PI "+ name + " " + typ + " \""+config+"\"]";
    }

    @Override
    public String getPluginVersion(){
        return getVersion();
    }

    @Override
    public PluginGeneralInfo getPluginGeneralInfo() {
        PluginGeneralInfo info = new PluginGeneralInfo(
                getTyp(),
                getVersion(),
                getWikiHelp(),
                getHelpUrl(),
                getHelp(),
                isDefaultPluginConfig(),
                isMath(),
                getClass().toString(),
                getInitPluginJS(),
                isJavaScript(),
                javascriptLibraries(),
                javascriptLibrariesLocal(),
                getInputElement().toString(),
                isCacheable(),
                isUseVars(),
                isUseCVars(),
                isUseMaximaVars(),
                isUseMVars(),
                getPluginServiceURL()
        );
        return info;
    }

    /**
     * Liefert eine HTML-Hilfe zu dem Plugin.<br>
     * Standardmäßig liegt die Hilfe in einer HTML-Datei welche gleich heisst wie die Klasse, im selben Verzeichnis sitzt und die Endung .html hat<br>
     * @return HTML Hilfe
     */
    @Override
    public String getHelp() {
        String help = "";
        if (this.getHelpfiles()!=null)
            for (String hf : this.getHelpfiles())
                help += PluginResource.replaceUmlautHTML(getHelpMessageFromResource(hf));
        if (help.length()<1) help = "<h1>Plugin-Template</h1>Help ist noch nicht konfiguriert!";
        return help;
    }

    protected String loadHelpFile(String resource) {
        return PluginResource.replaceUmlautHTML(getHelpMessageFromResource(resource));
    }

    /**
            * Lädt eine HTML-Hilfedatei aus den Resourcen
	 * @param   name Pfad der Hilfedate
	 * @return  Inhalt der Hilfedatei als String
	 */
    protected final String getHelpMessageFromResource(String name) {
        try {
            Vector<String> helptext = PluginResource.readResourceFile(name);
            boolean body=false;
            StringBuilder sb = new StringBuilder();
            for (String s:helptext) {
                s = s.replaceAll("\\r","");
                if (!body && s.contains("<body")) {
                    Matcher m;
                    if ((m = Pattern.compile("^.*\\<body[^\\>]*\\>(.*)$").matcher(s)).find()) {
                        sb.append( m.group(1) );
                    }
                    body=true;
                } else if (body) {
                    if (s.contains("</body")) {
                        Matcher m;
                        if ((m = Pattern.compile("^(.*)\\<\\/body[^\\>]*\\>.*$").matcher(s)).find()) {
                            sb.append( m.group(1) );
                        }
                        body=false;
                    }
                    else sb.append( "\n"+s );
                }
            }
            return sb.toString().trim();
        } catch (Exception | Error e) {
            return "Hilfe Resource "+name+" für das Plugin wurde nicht gefunden!";
        }
    }

    /**
     * Liefert einen Angabestring für die MoodleText Angabe und erzeugt gegebenenfalls fehlende Datasets in der Datasetliste
     *
     * @param params Parameter für die Einstellungen
     * @return String für das MoodleText-Feld
     */
    @Override
    public String getAngabe(String params) {
        return null;
    }

    /**
     * Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten <br>
     * als Feld [Name,Datasetdefinition,Einheit]
     */
    @Override
    public List<PluginDatasetDto> generateDatasets() {
        return new ArrayList<PluginDatasetDto>();
    }

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     *
     * @param params Parameter für die Antworterzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return HTML Text
     */
    @Override
    public String getHTML(String params, PluginQuestionDto q) {
        return "";
    }

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     *
     * @param params               Parameter
     * @param q                    Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return Maxima Berechnungs-String
     */
    @Override
    public String getMaxima(String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode) {
        return "";
    }

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     *
     * @return Liste aller Variablen des Plugins
     */
    @Override
    public Vector<String> getVars() {
        return null;
    }

    /**
     * parst die Parameter des Plugins
     * @param params  Parameterstring
     * @param q       Frage
     * @return        Fehlermeldung oder Leerstring wenn alles ok ist
     */
    private String parseDrawParamsTimer(String params, PluginQuestionDto q, PluginImageResultDto pluginImageResultDto) {
        // Ohne Timer



        // Mit Timer
        TimerCall.CallResult ret = TimerCall.callMethode(new CallAdapter() {
            BasePlugin plugin = null;
            @Override
            public Object callMethode(Object ... objects) {
                plugin = (BasePlugin)objects[0];
                plugin.parseDrawParams((String)objects[1],(PluginQuestionDto)objects[2],(PluginImageResultDto)objects[3]);
                return Integer.valueOf(0);
            }
            @Override public String getMethodeName() { return "parseDrawParamsTimer"; }
            @Override public String getMethodeInfo() { return  plugin!=null?plugin.getName():"null"; }
            },LettoTimer.getPluginImageTimer(),this,params,q,pluginImageResultDto);
        LettoTimer.checkInterrupt();
        if (ret.status== TimerCall.RESULT.OK) {
            return "";
        } else if (ret.status== TimerCall.RESULT.TIMEOUT) {
            return "Timeout beim Parsen der Plugin-Parameter!";
        } else if (ret.status== TimerCall.RESULT.TIMEOUTKILLED) {
            return "Timeout beim Parsen der Plugin-Parameter bei dem der Task gekillt wurde!";
        } else if (ret.status== TimerCall.RESULT.ZOMBIE) {
            return "Timeout beim Parsen der Plugin-Parameter bei dem der Task als Zombie hängenblieb!";
        } else if (ret.status== TimerCall.RESULT.RUNTIMEEXCEPTION) {
            RuntimeException e = ret.getRuntimeException();
            return "undefinierte Runtime-Exception beim Parsen der Plugin-Parameter!";
        } else if (ret.status== TimerCall.RESULT.EXCEPTION) {
            Exception e = ret.getException();
            return "undefinierte Exception beim Parsen der Plugin-Parameter!";
        } else if (ret.status== TimerCall.RESULT.ERROR) {
            Error e = ret.getError();
            if (e==null) throw new RuntimeException("undefinierter Error beim Parsen der Plugin-Parameter!");
            String msg = e.getClass().getTypeName() +" " + (e.getMessage()!=null?e.getMessage():"");
            return msg;
        } else {
            return "undefinierter Fehler beim Parsen der Plugin-Parameter!";
        }
    }

    /**
     * Bestimmt einen eindeutigen String, welcher ein Plugin-Bild beschreibt um daraus den Dateinamen bestimmen zu können.
     * @param imageParams Parameter des PIG-Tags
     * @param q           Frage in der das Plugin eingebettet ist
     * @return            eindeutiger String der das Plugin-Bild eindeutig beschreibt.
     */
    @Override
    public String getPluginImageDescription(String imageParams, PluginQuestionDto q) {
        String description = "typ:"+this.typ;
        description += " version:"+this.getPluginVersion();
        description += " name:"+this.getName();
        description += " config:"+this.getConfig();
        description += " params:"+imageParams;
        description += " question:"+ JSON.objToJson(q);
        int i=0;
        /*for (Image image: ((Question)q).getImages()) {
            description += (i==0?"":",")+image.getMd5();
            i++;
        }
        if (q instanceof Question) {
            Question question = (Question)q;
            description += " vars:" + question.getTestDataset();
        } else {
            description += " vars:" + q.getVars().toString();
        }*/
        return description;
    }

    private BufferedImage errorMessageImage(String msg) {
        BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(10,10,480,480);
        g.setColor(Color.red);
        g.fillRect(245, 30, 10, 70);
        g.fillOval(243, 105, 12, 12);
        g.setColor(Color.black);
        g.drawRect(10,10,480,480);
        g.drawString("Fehler bei der Plugin-Graphik", 20, 50);
        try {
            String lines[] = msg.split("\\n");
            for (int i=0;i<lines.length;i++)
                g.drawString(lines[i], 20, 80+20*i);
        } catch(Exception e2) {
            g.drawString("Fehler bei der Bilderstellung!", 20, 250);
        }
        g.dispose();
        return bi;
    }

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     *
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild in einem PluginFileDto
     */
    @Override
    public ImageBase64Dto getImageDto(String params, PluginQuestionDto q) {
        String error="";
        String extension = "png";
        PluginImageResultDto pluginImageResultDto = new PluginImageResultDto();
        if (params.startsWith("\"") && params.endsWith("\"")) params = params.substring(1,params.length()-1);
        // Bestimmung des aktuellen Prüfstrings des Bildes aus Parametern, Pluginname, Pluginversion und Plugindefinition
        String description = getPluginImageDescription(params,q);
        String filename = ImageService.generateFilename(description,extension);
        String base64image="";
        // Berechnung des Bildes und speichern mit dem imageService
        if (filename.length()>0) {
            try {
                BufferedImage image=null;
                String parseresult;
                // ohne Timer
                parseDrawParams(params, q, pluginImageResultDto);
                image = getAWTImage(params, q, pluginImageResultDto);
                if (!pluginImageResultDto.isOk())
                    error = pluginImageResultDto.getMessages().toString();
                // Mit Timer
                /*parseresult = parseDrawParamsTimer(params, q, pluginImageResultDto);
                if (parseresult.length() == 0) {
                    image = getAWTImage(params, q, pluginImageResultDto);
                    if (!pluginImageResultDto.isOk())
                        error = pluginImageResultDto.getMessages().toString();
                } else {
                    image = errorMessageImage(parseresult);
                    error = parseresult;
                }*/

                try
                {   final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, extension, os);
                    base64image = Base64.getEncoder().encodeToString(os.toByteArray());
                } catch (final Exception ioe) {
                    error="cannot write image for "+getTyp()+"-Plugin "+getName();
                }

            } catch (Exception ignored) {
                error = "Datei für das "+this.getTyp()+"-Plugin "+this.getName()+" kann nicht erzeugt werden!";
            }
        } else error = "Datei für das "+this.getTyp()+"-Plugin "+this.getName()+" kann nicht erzeugt werden!";

        ImageBase64Dto pluginImageDto = new ImageBase64Dto(
                base64image,
                new ImageInfoDto(
                        getVersion(),
                        getTyp(),
                        filename,
                        "",
                        getWidth(),
                        getHeight(),
                        IMAGEUNIT.percent,
                        getImageWidthProzent(),
                        "",
                        "Plugin-Bild",
                        getTyp()+" "+getName(),
                        (new Date()).getTime()+365L*24L*3600L*1000L
                ),
                error
        );
        return pluginImageDto;
    }

    /**
     * Liefert ein java.awt.BufferedImage Bild mit den angegebenen Parametern
     * @param   params   Parameter für die Bilderzeugung
     * @param q          Frage wo das Plugin eingebettet ist
     * @param pluginImageResultDto  nimmt alle Fehlermeldungen auf die beim Rendern des Bildes entstehen
     * @return           Bild als java.awt.BufferedImage
     */
    public BufferedImage getAWTImage(String params, PluginQuestionDto q, PluginImageResultDto pluginImageResultDto) {
        // Bild erzeugen
        params = HTMLtool.XMLToString(params);
        for (MatchResult m: RegExp.findMatches("\\s*\"(.*)\"", params))
            params = m.group(1);
        try {
            // ohne Timer starten
            // plugin.parseDrawParams(params,q);
            BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            paint(g, pluginImageResultDto);
            g.dispose();
            return bi;
            // Mit Timer starten
            /*TimerCall.CallResult ret = TimerCall.callMethode(new CallAdapter() {
                @Override
                public Object callMethode(Object ... objects) {
                    BasePlugin plugin = (BasePlugin)objects[0];
                    PluginImageResultDto pluginImageResultDto1 = (PluginImageResultDto)objects[1];
                    // plugin.parseDrawParams((String)objects[1],(Question)objects[2]);
                    BufferedImage bi = new BufferedImage(plugin.width,plugin.height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = bi.createGraphics();
                    paint(g, pluginImageResultDto1);
                    g.dispose();
                    return bi;
                }}, LettoTimer.getPluginImageTimer(),this, pluginImageResultDto);
            LettoTimer.checkInterrupt();
            if (ret.status== TimerCall.RESULT.OK) {
                if (ret.getResult() instanceof BufferedImage) return (BufferedImage)ret.getResult();
                throw new RuntimeException("Ergebnis ist nicht berechenbar!");
            } else if (ret.status== TimerCall.RESULT.TIMEOUT) {
                throw new RuntimeException("Timeout bei der Ergebnisberechnung!");
            } else if (ret.status== TimerCall.RESULT.TIMEOUTKILLED) {
                throw new RuntimeException("Timeout bei der Ergebnisberechnung bei dem der Task gekillt wurde!");
            } else if (ret.status== TimerCall.RESULT.RUNTIMEEXCEPTION) {
                RuntimeException e = ret.getRuntimeException();
                if (e==null) throw new RuntimeException("undefinierte Runtime-Exception bei der Berechnung!");
                throw e;
            } else if (ret.status== TimerCall.RESULT.EXCEPTION) {
                Exception e = ret.getException();
                if (e==null) throw new RuntimeException("undefinierte Exception bei der Berechnung!");
                throw new RuntimeException("Exception bei der Berechnung!");
            } else if (ret.status== TimerCall.RESULT.ERROR) {
                Error e = ret.getError();
                if (e==null) throw new RuntimeException("undefinierter Error bei der Berechnung!");
                String msg = e.getClass().getTypeName() +" " + (e.getMessage()!=null?e.getMessage():"");
                throw new RuntimeException(msg);
            } else {
                throw new RuntimeException("undefinierter Fehler bei der Berechnung!");
            }*/
        } catch (Exception ex) {
            String s;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            s = sw.toString();
            return errorMessageImage(s);
        }
    }

    /**
     * Methode welche das Bild für die Ausgabe erzeugt
     * @param g Graphikhandle
     * @param pluginImageResultDto Nimmt alle Fehlermeldungen auf, welche beim Rendern des Bildes entstehen
     */
    public abstract void paint(Graphics2D g, PluginImageResultDto pluginImageResultDto);

    /**
     * Gibt eine Fehlermeldung im Graphics-Objekt aus
     * @param g   Graphics-Handler
     * @param msg Meldung
     */
    public void paintFehler(Graphics g, String msg) {
        int f = height/20;
        int y = f+f/3;
        g.setFont(new Font(g.getFont().getFamily(),g.getFont().getStyle(),f));
        g.setColor(Color.red);
        g.drawString("Fehler:", f, y);
        String m[] = msg.split("\\n");
        for (String s : m) {
            y+=(f*3)/2;
            msg = s.trim();
            g.setColor(Color.BLUE);
            g.drawString(msg, f, y);
        }
    }

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     *
     * @return Liefert eine Liste aller möglichen Varianten von Bildern
     */
    @Override
    public Vector<String[]> getImageTemplates() {
        return null;
    }

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     *
     * @param vars Alle Variablen der Frage
     * @param cp   Berechnungsparameter
     * @param p    Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return Ergebnis der Funktion
     */
    @Override
    public CalcErgebnisDto parserPlugin(VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto ... p) {
        return null;
    }

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     *
     * @param p Einheiten der Parameter
     * @return Recheneinheit des Ergebnisses
     */
    @Override
    public String parserPluginEinheit(String... p) {
        return null;
    }

    /**
     * Methode definiert das Eingabeelement der Subquestion, die das
     * Plugin verwendet
     * @return anzuzeigendes Eingabeelement, default: TextField
     */
    @Override
    public InputElement getInputElement() {
        if (isJavaScript()) return InputElement.JAVASCRIPT;
        return InputElement.TextArea;
    }

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param pluginDef	akt. Plugin-Definition
     * @param jsResult	Rückgabe von Javascript
     * @return	aktualiesierte Plugindefinition
     */
    @Override
    public String updatePluginstringJavascript(String pluginDef, String jsResult) {
        return pluginDef;
    }

    /**
     * Liefert eine Liste von Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden!
     *
     * @return für das Plugin notwendige JS-Libraries
     */
    @Override
    public List<JavascriptLibrary> javascriptLibraries() {
        return new Vector<>();
    }

    /**
     * Liefert eine Liste von LOKALEN Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     *
     * @return für das Plugin notwendige JS-Libraries
     */
    @Override
    public List<JavascriptLibrary> javascriptLibrariesLocal() {
        Vector<JavascriptLibrary> ret = new Vector<>();
        if (this.getJavascriptLibs()!=null)
            for (String lib:this.getJavascriptLibs()) {
                ret.add(new JavascriptLibrary(lib, PluginResource.readResourceFileString(lib), true));
            }
        return ret;
    }

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     *
     * @param params       Plugin-Parameter
     * @param q            Question, in die das Plugin eingebettet ist
     * @param nr           Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return PluginDto
     */
    @Override
    public PluginDto loadPluginDto(String params, PluginQuestionDto q, int nr) {
        return null;
    }

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param pluginDto Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return          Latexsourcode und zugehörige Bilder in einer Hashmap
     */
    @Override
    public PluginRenderDto renderLatex(PluginDto pluginDto, String answer, String mode){return new PluginRenderDto(); }

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
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
    public PluginRenderDto renderPluginResult(boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade) {
        return new PluginRenderDto();
    }

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @return                   alle notwendigen Konfig
     */
    @Override
    public PluginConfigurationInfoDto configurationInfo(String configurationID) {
        PluginConfigurationInfoDto pluginConfigurationInfoDto = new PluginConfigurationInfoDto(
            configurationID,
            getConfigurationMode(),
            isUseQuestion(),
            isUseVars(),
            isUseCVars(),
            isUseMaximaVars(),
            isUseMVars(),
            isAddDataSet(),
            isCalcMaxima(),
            isExternUrl(),
            getConfigPluginJS(),
            getPluginServiceURL()
        );
        return pluginConfigurationInfoDto;
    }

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     */
    @Override
    public PluginConfigDto setConfigurationData(String configuration, PluginQuestionDto questionDto) {
        this.config = configuration;
        PluginConfigDto pluginConfigDto = new PluginConfigDto();
        return pluginConfigDto;
    }

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    @Override
    public String getConfiguration() {
        return this.config;
    }

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Text der innerhalb der PI Tags gestanden ist
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter Text
     */
    @Override
    public String modifyAngabe(String text, PluginQuestionDto q) {
        return text;
    }

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Angabetext der Frage
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter AngabeText
     */
    @Override
    public String modifyAngabeTextkomplett(String text, PluginQuestionDto q) {
        return text;
    }


}
