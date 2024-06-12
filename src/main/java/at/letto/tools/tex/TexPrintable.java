package at.letto.tools.tex;

import java.io.File;

/**
 * Interface für Klassen, welche als Tex-Dokument mit PDF-Latex gedruckt werden können
 * @author Werner Damböck
 *
 */
public interface TexPrintable {
	
	/**
	 * Erzeugt mittel Latex eine PDF-Datei oder mehrere PDF-Dateien als Zip und gibt ein File-Objekt auf die 
	 * erzeugte Datei zurück.
	 * @param context Print-Context für den Ausdruck
	 * @return  erzeugte Datei oder null wenn nichts erzeugt werden konnte.
	 */
	public File generateFile(TexPrintContext context);
	
	/**
	 * Erzeugt eine kompilierbare Tex-Datei mit den Voreinstellungen des Context
	 * @param context Voreinstellungen, Dateipfad etc.
	 */
	public void generateTEX(TexPrintContext context); 
	
}
