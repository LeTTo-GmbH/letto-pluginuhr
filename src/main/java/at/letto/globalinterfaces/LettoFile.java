package at.letto.globalinterfaces;

/**
 * Klasse für Dateien welche am Letto-Server gespeichert und verarbeitet werden
 * @author Werner
 *
 */
public interface LettoFile {

	public String getFilename();
	
	/** @return   Größe für das Bild als Tex Größenangabe */
	public String getImgSizeTex();
	
	/** @return Dateiname für die temporäre Datei */
	public String getTmpFile();
	
	/** @return    erzeugt die zusätzlichen Attribute für einen gültigen HTML img Tag */
	public String getImgTag();
	
	/**
	 * Liefert die http(s)-Adresse, unter der die Datei am WEB-Server 
	 * abrufbar ist. 
	 * @return WEB-Adresse der Datei
	 */
	public String getWebPath();
	
	/**
	 * Liefert den Inhalt des Bildes:
	 * Die Daten wurden aus der Datenbank in das Filesystem verschoben:
	 * Diese Methode sollte also nur mehr temporär verwendet werden, da die Bilder 
	 * direkt in HTML eingebunden werden.<br>
	 * Wenn die Datei nicht vom Filesystem gelesen werden kann, dann wird 
	 * der Inhalt der Datenbank zurückgegeben
	 * @return	Base64encoded String der Datei
	 */
	public String getInhalt();

	/**
	 * Liefert den vollständigen HTML-Link zu einem Dokument aus dem Image-Servie
	 * @param nr		ImageNr, im Fall, dass Image nicht am Service verfügbar
	 * @param attribs	Attibute
	 * @return			Link oder JS-Methode zum Download des Files
	 */
    String getLinkWeb(int nr, String attribs);

    /**
	 * Liefert ein Bild welches im Web dargestellt werden kann. Nicht darstellbare Bilder werden konvertiert.
	 * @param dblClick	Bei Doppelclick wird das Bild groß aufgeklappt, sonst bei Einfachclick
	 * @return	img-Tag
	 */
	public String getImageWeb(boolean dblClick);
	
}
