package at.letto.tools.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLtool {

    private static final Pattern P_URLtoString = Pattern.compile("^(.*)\\%([a-fA-F0-9][a-fA-F0-9])(.*)$");

    /**
     * Ersetzt in einem HTML-String alle Umlaute durch Entitäten
     * @param s   String
     * @return    XML String
     */
    public static String HTMLreplaceUmlaute(String s) {
        s = s.replace("ü","&uuml;");
        s = s.replace("ö","&ouml;");
        s = s.replace("ä","&auml;");
        s = s.replace("Ü","&Uuml;");
        s = s.replace("Ö","&Ouml;");
        s = s.replace("Ä","&Auml;");
        s = s.replace("ß","&szlig;");
        s = s.replace("€","&euro;");
        return s;
    }

    /**
     * Ersetzt in einem String alle &gt; &lt; &amp; durch Entitäten
     * @param s   String
     * @return    XML String
     */
    public static String StringToXML(String s) {
        s = s.replace("&","&amp;");
        s = s.replace("<","&lt;");
        s = s.replace(">","&gt;");
        return s;
    }

    /**
     * Ersetzt in einem String alle Entitäten für &amp; &lt; &gt; Umlaute und Zeilenvorschübe durch HTML-Kommandos
     * @param s   nicht HTML-formatierter String
     * @return    html-formatierter String
     */
    public static String toHTML(String s) {
        return HTMLreplaceUmlaute(StringToXML(s)).replaceAll("\\n", "<br />");
    }

    /**
     * Ersetzt XML-Entitäten durch die entsprechenden Zeichen
     * @param s        String
     * @return String  Ersetzt XML-Entitäten durch die entsprechenden Zeichen
     */
    public static String XMLToString(String s) {
        s = s.replace("&lt;","<");
        s = s.replace("&gt;",">");
        s = s.replace("&auml;","ä");
        s = s.replace("&Auml;","Ä");
        s = s.replace("&uuml;","ü");
        s = s.replace("&Uuml;","Ü");
        s = s.replace("&ouml;","ö");
        s = s.replace("&Ouml;","Ö");
        s = s.replace("&Suml;","ß");
        s = s.replace("&suml;","ß");
        s = s.replace("&szlig;","ß");
        s = s.replace("&nbsp;"," ");
        s = s.replace("&quot;","\"");
        s = s.replace("&euro;","€");
        s = s.replace("&deg;","°");
        s = s.replace("&acute;","´");

        Matcher m;
        Pattern pENT=Pattern.compile("^(?<pre>.*)\\&#(?<n>[0-9]+)\\;(?<suf>.*)$");
        while ((m=pENT.matcher(s)).find()) {
            s  = m.group("pre");
            //int  n = (int)(CalcNumerical.parse("0x"+m.group("n")).toLong());
            int n= Integer.parseInt(m.group("n"));
            char c = (char)n;
            s += c;
            s += m.group("suf");
        }
        s = s.replace("\\%20"," ");   // Leerzeichen
        s = s.replace("&amp;","&");
        return s;
    }

    /**
     * wandelt einen normalen mehrzeiligen String so um, das er als HTML-Code richtig dargestellt wird
     * @param s String
     * @return  HTML-Code
     */
    public static String StringToHTML(String s) {
        s = StringToXML(s);
        s = s.replaceAll("\\n", "<br />");
        return s;
    }

    /**
     * Ersetzt in einem URL-String alle %20 etc durch ASC-II Zeichen!!
     * @param s URL-String
     * @return  normaler Java-String
     */
    public static String URLtoString(String s) {
        Matcher m;

        while ((m=P_URLtoString.matcher(s)).matches()) {
            String z   = "";
            switch (m.group(2).toUpperCase()) {
                case "20": z=" "; break;
                case "21": z="!"; break;
                case "22": z="\"";break;
                case "23": z="#"; break;
                case "24": z="$"; break;
                case "26": z="&"; break;
                case "27": z="'"; break;
                case "28": z="("; break;
                case "29": z=")"; break;
                case "2A": z="*"; break;
                case "2B": z="+"; break;
                case "2C": z=","; break;
                case "2D": z="-"; break;
                case "2E": z="."; break;
                case "2F": z="/"; break;
                case "3A": z=":"; break;
                case "3B": z=";"; break;
                case "3C": z="<"; break;
                case "3D": z="="; break;
                case "3E": z=">"; break;
                case "3F": z="?"; break;
                case "40": z="@"; break;
                case "5B": z="["; break;
                case "5C": z="\\";break;
                case "5D": z="]"; break;
                case "7B": z="{"; break;
                case "7C": z="|"; break;
                case "7D": z="}"; break;
            }
            s = m.group(1)+z+m.group(3);
        }
        s = s.replace("\\%25","%");
        return s;
    }

    /** setzt einen Text als roten Text in HTML und ersetzt dabei alle Umlaute etc. durch Entitäten */
    public static String redText(String s){
        return colorText(toHTML(s),"red");
    }

    /** setzt einen Text als färbigen Text in HTML und ersetzt dabei alle Umlaute etc. durch Entitäten */
    public static String colorText(String s, String color){
        return colorHtml(toHTML(s),color);
    }

    /** setzt einen HTML-Code in ein span mit einer Textfarbe */
    public static String colorHtml(String html,String color){
       return "<span style='color:"+color+"'>"+html+"</span>";
    }

    /** setzt einen HTML-Code in ein span mit einer Textfarbe */
    public static String colorHtml(String html,String color, String bgcolor){
        return "<span style='color:"+color+";background-color:"+bgcolor+"'>"+html+"</span>";
    }

}
