package at.letto.bashtools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse für das Laden einer Linux-Bash Ausgabe mit ESC-Formatierungs-Elementen für Farbe, Schrift, etc. <br>
 * Ausgabemöglichkeit als HTML-Code
 */
public class BashOutput {

    public static final String filename = "C:/letto/install/setup-stable.log";
    public static final String filenamehtml = "C:/letto/install/setup-stable.html";

    // Formatierungssequenzin der Form (ohne Leezeichen und xxx ist der Code)
    // ESC [ xxx m
    // ESC [ xxx ; xxx m
    // ESC [ m                   zählt wie ESC [ 0 m
    public static final int ESC_M_RESET     = 0;
    public static final int ESC_M_BOLD      = 1; // fett , hervorgehoben
    public static final int ESC_M_DIM       = 2; // schwach, abgedunkelt
    public static final int ESC_M_ITALIC    = 3; // kursiv
    public static final int ESC_M_UNDERLINE = 4; // unterstrichen
    public static final int ESC_M_BLINK     = 5; // blinkend
    public static final int ESC_M_BLINK1    = 6; // blinkend
    public static final int ESC_M_INVERSE   = 7; // Farben invertiert
    public static final int ESC_M_HIDDEN    = 8; // nicht sichtbar
    public static final int ESC_M_RESET_BOLD      = 21;
    public static final int ESC_M_RESET_DIM       = 22; //
    public static final int ESC_M_RESET_ITALIC    = 23; //
    public static final int ESC_M_RESET_UNDERLINE = 24; //
    public static final int ESC_M_RESET_BLINK     = 25; //
    public static final int ESC_M_RESET_BLINK1    = 26; //
    public static final int ESC_M_RESET_INVERSE   = 27; //
    public static final int ESC_M_RESET_HIDDEN    = 28; //
    public static final int ESC_M_COLOR_DEFAULT   = 39; //

    public static final int ESC_M_COLOR_BLACK     = 30; //
    public static final int ESC_M_COLOR_RED       = 31; //
    public static final int ESC_M_COLOR_GREEN     = 32; //
    public static final int ESC_M_COLOR_YELLOW    = 33; //
    public static final int ESC_M_COLOR_BLUE      = 34; //
    public static final int ESC_M_COLOR_MAGENTA   = 35; //
    public static final int ESC_M_COLOR_CYAN      = 36; //
    public static final int ESC_M_COLOR_LIGHT_GRAY= 37; //
    public static final int ESC_M_COLOR_DARK_GRAY = 90; //
    public static final int ESC_M_COLOR_LIGHT_RED  = 91; //
    public static final int ESC_M_COLOR_LIGHT_GREEN  = 92; //
    public static final int ESC_M_COLOR_LIGHT_YELLOW = 93; //
    public static final int ESC_M_COLOR_LIGHT_BLUE   = 94; //
    public static final int ESC_M_COLOR_LIGHT_MAGENTA= 95; //
    public static final int ESC_M_COLOR_LIGHT_CYAN   = 96; //
    public static final int ESC_M_COLOR_WHITE        = 97; //
    public static final int ESC_M_BG_DEFAULT      = 49; //
    public static final int ESC_M_BG_BLACK        = 40; //
    public static final int ESC_M_BG_RED          = 41; //
    public static final int ESC_M_BG_GREEN        = 42; //
    public static final int ESC_M_BG_YELLOW       = 43; //
    public static final int ESC_M_BG_BLUE         = 44; //
    public static final int ESC_M_BG_MAGENTA      = 45; //
    public static final int ESC_M_BG_CYAN         = 46; //
    public static final int ESC_M_BG_LIGHT_GRAY   = 47; //
    public static final int ESC_M_BG_DARK_GRAY    = 100; //
    public static final int ESC_M_BG_LIGHT_RED    = 101; //
    public static final int ESC_M_BG_LIGHT_GREEN  = 102; //
    public static final int ESC_M_BG_LIGHT_YELLOW = 103; //
    public static final int ESC_M_BG_LIGHT_BLUE   = 104; //
    public static final int ESC_M_BG_LIGHT_MAGENTA= 105; //
    public static final int ESC_M_BG_LIGHT_CYAN   = 106; //
    public static final int ESC_M_BG_WHITE        = 107; //

    public static final String ESC_REGEXP_M  = "\u001B\\[([0-9]*)(?:;([0-9]+))*m";

    public static final String[] ESC_REGEXP_IGNORE = {
            "\u001B\\%\\/(.)",
            "\u001B\\%[^\\/]",
            "\u001B\\&([0-9])",
            "\u001B\\'([0-9])",
            "\u001B\\(\\!([@A-F])",
            "\u001B\\(\\\"([1-4><\\?])",
            "\u001B\\(\\%([0-9\\=])",
            "\u001B\\(\\&([0-9])",
            "\u001B\\(([0-9<=>@A-Z\\[\\\\\\]\\^_\\`a-z\\{\\}\\|\\~])",
            "\u001B\\-([A-C\\[\\]\\\\\\^_`a-z\\{\\}])",
            "\u001B([0-9\\=\\>Z`abcdlmno\\|\\}\\~A-Za-z])",
            "\u001B\\[(\\d*[A-Z])",
            "\u001B\\[\\?(\\d*[a-z])"
    };

    public static final String ESC_STOP_WAS = "\u001B(B";  // ???????
    public static final String ESC_TO_LINE_BEGIN = "\u001B[K";

    // Commandos wo die Zahl vor dem Buchstaben etwas angibt und auch weggelassen werden kann
    // ESC 1 B
    // ESC 1 A
    // ESC 1 K

    /******************************************************************************************************
     *     VARIABLE
     ******************************************************************************************************/
    private List<String> data = new ArrayList<>();
    private String color = "";
    private String bgcolor = "";
    private boolean bold    = false;
    private boolean dim     = false;
    private boolean italic  = false;
    private boolean blink   = false;
    private boolean underline = false;
    private boolean invers    = false;
    private boolean hidden    = false;

    /******************************************************************************************************
     *     CONSTRUCTOREN
     ******************************************************************************************************/
    /**
     * Erzeugt aus der Ausgabe in der Bash welche in einem Vector aus String vorliegen muss ein neues BashOutput Objekt
     * @param data  Ausgabe in der Bash
     */
    public BashOutput(Vector<String> data) throws IOException {
        for (String line:data)
            this.data.add(line);
    }

    /**
     * Erzeugt aus der Ausgabe in der Bash welche in einem Vector aus String vorliegen muss ein neues BashOutput Objekt
     * @param data  Ausgabe in der Bash
     */
    public BashOutput(List<String> data) {
        for (String line:data)
            this.data.add(line);
    }

    /**
     * Erzeugt aus der Ausgabe in der Bash welche in einem Vector aus String vorliegen muss ein neues BashOutput Objekt
     * @param data  Ausgabe in der Bash
     */
    public BashOutput(Iterable<String> data) {
        for (String line:data)
            this.data.add(line);
    }

    public BashOutput(File file) throws IOException  {
        this(Files.readAllLines((new File(filename)).toPath()));
    }

    public BashOutput(String filename) throws IOException  {
        this(new File(filename));
    }

    /******************************************************************************************************
     *     METHODS
     ******************************************************************************************************/
    public List<String> toHTML() {
        List<String> htmldata = new ArrayList<>();
        htmldata.add("<pre style='background-color:black;color:lightgray;line-height: 60%;font-weight: 1000;'>");
        Matcher m;
        boolean styleStarted = false;
        for (String line:data) {
            for (String esc : ESC_REGEXP_IGNORE) {
                line = line.replaceAll(esc,"");
            }
            Pattern p = Pattern.compile(ESC_REGEXP_M);
            m = p.matcher(line);
            StringBuilder sb = new StringBuilder();
            String suffix = line;
            while (m.find()) {
                String prefix = suffix.substring(0,m.start());
                suffix = suffix.substring(m.end());
                try {
                    for (int i=1;m.group(i)!=null;i++) {
                        String g = m.group(i);
                        if (g.length()==0) resetDefault();
                        else {
                            try {
                                int n = Integer.parseInt(g);
                                switch (n) {
                                    case ESC_M_RESET      : resetDefault(); break;
                                    case ESC_M_BOLD       : bold=true; break;
                                    case ESC_M_DIM        : dim =true; break;
                                    case ESC_M_ITALIC     : italic=true; break;
                                    case ESC_M_UNDERLINE  : underline=true; break;
                                    case ESC_M_BLINK      : blink=true; break;
                                    case ESC_M_BLINK1     : blink=true; break;
                                    case ESC_M_INVERSE    : invers = true; break;
                                    case ESC_M_HIDDEN     : hidden = true; break;
                                    case ESC_M_RESET_BOLD : bold = false; break;
                                    case ESC_M_RESET_DIM  : dim = false; break;
                                    case ESC_M_RESET_ITALIC    : italic = false; break;
                                    case ESC_M_RESET_UNDERLINE : underline = false; break;
                                    case ESC_M_RESET_BLINK     : blink = false; break;
                                    case ESC_M_RESET_BLINK1    : blink = false; break;
                                    case ESC_M_RESET_INVERSE   : invers = false; break;
                                    case ESC_M_RESET_HIDDEN    : hidden = false; break;
                                    case ESC_M_COLOR_DEFAULT   : color = ""; break;
                                    case ESC_M_COLOR_BLACK     : color = "black"; break;
                                    case ESC_M_COLOR_RED       : color = "red"; break;
                                    case ESC_M_COLOR_GREEN     : color = "green"; break;
                                    case ESC_M_COLOR_YELLOW    : color = "yellow"; break;
                                    case ESC_M_COLOR_BLUE      : color = "blue"; break;
                                    case ESC_M_COLOR_MAGENTA   : color = "magenta"; break;
                                    case ESC_M_COLOR_CYAN      : color = "cyan"; break;
                                    case ESC_M_COLOR_LIGHT_GRAY: color = "lightgray"; break;
                                    case ESC_M_COLOR_DARK_GRAY : color = "gray"; break;
                                    case ESC_M_COLOR_LIGHT_RED : color = "lightred"; break;
                                    case ESC_M_COLOR_LIGHT_GREEN  : color = "lightgreen"; break;
                                    case ESC_M_COLOR_LIGHT_YELLOW : color = "lightyellow"; break;
                                    case ESC_M_COLOR_LIGHT_BLUE   : color = "lightblue"; break;
                                    case ESC_M_COLOR_LIGHT_MAGENTA: color = "lightmagenta"; break;
                                    case ESC_M_COLOR_LIGHT_CYAN   : color = "lightcyan"; break;
                                    case ESC_M_COLOR_WHITE     : color = "white"; break;
                                    case ESC_M_BG_DEFAULT      : bgcolor = ""; break;
                                    case ESC_M_BG_BLACK        : bgcolor = "black"; break;
                                    case ESC_M_BG_RED          : bgcolor = "red"; break;
                                    case ESC_M_BG_GREEN        : bgcolor = "green"; break;
                                    case ESC_M_BG_YELLOW       : bgcolor = "yellow"; break;
                                    case ESC_M_BG_BLUE         : bgcolor = "blue"; break;
                                    case ESC_M_BG_MAGENTA      : bgcolor = "magenta"; break;
                                    case ESC_M_BG_CYAN         : bgcolor = "cyan"; break;
                                    case ESC_M_BG_LIGHT_GRAY   : bgcolor = "lightgray"; break;
                                    case ESC_M_BG_DARK_GRAY    : bgcolor = "gray"; break;
                                    case ESC_M_BG_LIGHT_RED    : bgcolor = "lightred"; break;
                                    case ESC_M_BG_LIGHT_GREEN  : bgcolor = "lightgreen"; break;
                                    case ESC_M_BG_LIGHT_YELLOW : bgcolor = "lightyellow"; break;
                                    case ESC_M_BG_LIGHT_BLUE   : bgcolor = "lightblue"; break;
                                    case ESC_M_BG_LIGHT_MAGENTA: bgcolor = "lightmagenta"; break;
                                    case ESC_M_BG_LIGHT_CYAN   : bgcolor = "lightcyan"; break;
                                    case ESC_M_BG_WHITE        : bgcolor = "white"; break;
                                }
                            } catch (Exception ex) {}
                        }
                    }
                } catch (Exception ex) {}
                sb.append(replaceEntityHTML(prefix));
                if (styleStarted)
                    sb.append("</span>");
                styleStarted=false;
                if (!isDefault()) {
                    String style = "";
                    if (invers) {
                        if (color.length()>0)    style += "background-color:"+color+";";
                        else                     style += "background-color:lightgray;";
                        if (hidden )                  style += "color:lightgray;";
                        else if (bgcolor.length()>0)  style += "color:"+bgcolor+";";
                        else                          style += "color:black;";
                    } else if (hidden) {
                        if (bgcolor.length()>0)  style += "background-color:"+bgcolor+"; color:"+bgcolor+";";
                        else                     style += "background-color:black;color:black;";
                    } else {
                        if (color.length() > 0) style += "color:" + color + ";";
                        if (bgcolor.length() > 0) style += "background-color:" + bgcolor + ";";
                    }
                    if (bold)     style += "font-weight: 2000;";
                    if (dim)      style += "font-weight: 500;";
                    if (italic)   style += "font-style: italic;";
                    // if (blink)    style += "font-weight: 2000;";
                    if (underline)style += "text-decoration: underline;;";
                    sb.append("<span style='"+style+"'>");
                    styleStarted=true;
                }
                m = p.matcher(suffix);
            }
            sb.append(replaceEntityHTML(suffix));
            sb.append("<br>");
            String htmlline = sb.toString();
            htmldata.add(htmlline);
        }
        htmldata.add("</pre>");
        return htmldata;
    }

    private String replaceEntityHTML(String s) {
        s = s.replace("&","&amp;");
        s = s.replace("ü","&uuml;");
        s = s.replace("ö","&ouml;");
        s = s.replace("ä","&auml;");
        s = s.replace("Ü","&Uuml;");
        s = s.replace("Ö","&Ouml;");
        s = s.replace("Ä","&Auml;");
        s = s.replace("ß","&szlig;");
        s = s.replace("€","&euro;");
        s = s.replace("<","&lt;");
        s = s.replace(">","&gt;");
        return s;
    }

    private void resetDefault() {
        color     = "";
        bgcolor   = "";
        bold      = false;
        dim       = false;
        italic    = false;
        blink     = false;
        underline = false;
        invers    = false;
        hidden    = false;
    }
    private boolean isDefault() {
        if (!"".equals(color)) return false;
        if (!"".equals(bgcolor)) return false;
        if (bold) return false;
        if (dim) return false;
        if (italic) return false;
        if (blink)  return false;
        if (underline) return false;
        if (invers) return false;
        if (hidden) return false;
        return true;
    }

    /******************************************************************************************************
     *     MAIN
     ******************************************************************************************************/
    public static void main(String[] args) throws IOException {
        BashOutput bashOutput = new BashOutput(filename);
        Files.write((new File(filenamehtml)).toPath(),bashOutput.toHTML());
    }
}
