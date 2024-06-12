package at.letto.plugins.service;

import at.letto.tools.FileMethods;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

/**
 * statische Methoden um aus den Systemresourcen der Plugins Dateien zu laden
 */
public class PluginResource {

    /**
     * Gibt einen InputStream auf die gewünschte Resource zurück
     * @param  resource  Pfad der Resource (innerhalb von src/resources)
     * @return InputStream auf die Resource
     */
    public static InputStream getResourceAsStream(String resource) {
        return PluginResource.class.getResourceAsStream("/"+resource);
    }

    /**
     * Gibt eine URL auf die gewünschte Resource zurück
     * @param resource  Pfad der Resource (innerhalb von src/resources)
     * @return URL auf die Resource
     */
    private static URL getResource(String resource) {
        return PluginResource.class.getResource("/"+resource);
    }

    /**
     * Liest eine Textdatei aus den Resourcen in einen Vector ein
     * @param resource  Resourcenpfad innerhalb von src
     * @return          Dateiinhalt der Resource als String-Vector
     */
    public static Vector<String> readResourceFile(String resource){
        try {
            InputStream res = getResourceAsStream(resource);
            Vector<String> ret = FileMethods.readFileInList(res,0);
            res.close();
            return ret;
            /*if (res!=null) {
                BufferedReader br=new BufferedReader(new InputStreamReader(res));
                while ((s = br.readLine()) != null) {
                    ret.add(s);
                }
                br.close();
            } else {
                System.out.println("Resource "+Resource+" not found!");
            }*/
        } catch (Exception e) {
            System.out.println("Resource "+resource+" not found!");
        }
        return new Vector<String>();
    }

    public static String readResourceFileString(String resource) {
        StringBuilder sb = new StringBuilder();
        for (String s:readResourceFile(resource)) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Vector<String> replaceUmlautHTML(Vector<String> data) {
        Vector<String> ret = new Vector<String>();
        for (String line:data)
            ret.add(replaceUmlautHTML(line));
        return ret;
    }

    public static String replaceUmlautHTML(String data) {
        data = data.replaceAll("ä","&auml").replaceAll("ü","&uuml;").replaceAll("ö","&ouml;");
        data = data.replaceAll("Ä","&Auml").replaceAll("Ü","&Uuml;").replaceAll("Ö","&Ouml;");
        data = data.replaceAll("ß","&suml");
        data = data.replaceAll("ä","&auml").replaceAll("ü","&uuml;").replaceAll("ö","&ouml;");
        data = data.replaceAll("Ä","&Auml").replaceAll("Ü","&Uuml;").replaceAll("Ö","&Ouml;");
        data = data.replaceAll("ß","&suml");
        return data;
    }

}
