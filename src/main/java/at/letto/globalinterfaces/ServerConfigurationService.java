package at.letto.globalinterfaces;

import at.letto.globalinterfaces.ImageService;
import at.letto.tools.ServerStatus;

import java.util.Vector;

public interface ServerConfigurationService {
	
	 /**
     * Liefert eine String-Resource aus der Datei resources/StringRes in der aktuell eingestellten Sprache
     * @param Key   Name der Stringresource   
     * @return      gesuchter String aus der Resource
     */
    public String Res(String Key);
    
    /**
     * Liefert das Betriebssystem in einem String
     * @return String des Betriebssystems
     */
    public String getOS();
    
    /**
     * Prüft ob das Betriebssystem Linux ist
     * @return true wenn Linux
     */
    public boolean isLinux();
    
    /**
     * Prüft ob das Betriebssystem Windows ist
     * @return true wenn Windows
     */
    public boolean isWindows();
    
    /**
     * @return True wenn am Glassfish Server
     */
    public boolean isServer();
    
    /**
     * Gibt den entsprechenden XML-Tag aus der Konfig-Datei zurück
     * @param  Key   XML-Tag, der ausgelesen werden soll
     * @return Wert aus XML-Config-Datei 
     */
    public String Get(String Key);
    
    /**
     * Lädt eine Datei entweder aus der Datenbank, aus dem Config-Verzeichnis, oder aus der Resource
     * @param  filename   Dateiname der Config-Datei
     * @return Dateiinhalt
     */
    public Vector<String> loadConfigFile(String filename);
    
    /**
	 * Gibt ein Log-Message aus
	 * @param message Gibt ein Log-Message aus
	 */
	public void Msg1(String message);
	
	/**
	 * @return liefert die aktuelle Zeit als String
	 */
	public String getCurrentTime();
	
    /**
     * Erzeugen einer Webserver - Error-Meldung
     * @param txt1 Text 1
     * @param txt2 Text 2
     */
    public void err(String txt1, String txt2);
    
    /**
     * Erzeugen einer Webserver - Warnung
     * @param txt1 Text 1
     * @param txt2 Text 2
     */
    public void warn(String txt1, String txt2) ;
    
    /**
     * Erzeugen einer Webserver - Info-Message
     * @param txt1 Text 1
     * @param txt2 Text 2
     */
    public void info(String txt1, String txt2);
    
    /**
     * Liest eine Textdatei aus den Resourcen in einen Vector ein
     * @param Resource  Resourcenpfad innerhalb von src
     * @return          Dateiinhalt der Resource als String-Vector
     */
    public Vector<String> readResourceFile(String Resource);
    
	/** @return Referenz auf Image-Service für Plugins */
    public ImageService getPluginImageService();

    /** @return Referenz auf Image-Service für normale Bilder und Dateien */
    public ImageService getImageService();

    public ImageService getFotoImageService();

    /** @return Liefert Informationen wie IP, Servername, Schulname über den Server */
    public String getServerInfo();

}
