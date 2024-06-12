package at.letto.tools.tex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;

import at.letto.globalinterfaces.LettoUser;
import at.letto.tools.LettoConfigDto;
import at.letto.tools.LicenseKey;
import at.letto.tools.Cmd;
import at.letto.tools.ZipFileWriter;
import at.letto.tools.tex.Tex.PRINTMODE;
import at.letto.tools.tex.Tex.PrintParameter;
import at.letto.tools.threads.LettoTimer;
import lombok.Getter;
import lombok.Setter;

/**
 * Der Tex-Printcontext wird für den Ausdruck von beliebigen Elementen mit Latex verwendet.<br>
 * Er enthält die notwendigen Print-Parameter, die temporären Dateien, alle notwendigen Verzeichnisse und Dateinamen.<br>
 * Am Ende eines Ausdruckes wird der Print-Context wieder gelöscht.<br>
 * 
 * Der normale Workflow: 
 * @author Werner Damböck
 *
 */
public class TexPrintContext {
	
	/** Dokument das gedruckt werden soll */
	@Getter private final  TexPrintable document;
	/** Liste aller Dateien, die temporär angelegt wurden */
	@Getter private  final Vector<File> images;
	/** Mode des Ausdruckes */
	@Getter private final TexPrintMode mode;
	/** Parameter für den Ausdruck */
	@Getter private final PrintParameter pp;
	/** Dateiname der tex-Datei */
	@Getter @Setter private String filename;
	/** Inhalt der compilierbaren Tex-Datei */
	@Getter private Vector<String> texCode;
	/** Ziel - PDF-Datei */
	@Getter @Setter private String pdfziel;
	/** Ziel - Zip-Datei */
	@Getter @Setter private String zipziel;
	/** Ausgabe als Zip Datei eingeschaltet */
	@Getter @Setter private boolean zip=false;
	/** Nummer des Datensatzes wenn Fragen gedruckt werden */
	@Getter @Setter private int datasetNr=0;
	/** Datensatzwerte des Schülers bei einem Test */
	@Getter @Setter private String testDataset="";
	/** Lizenz die für den Druck verwendet wird */
	// @Getter @Setter private LicenseKey license=null;
	/** Lizenz die für den Druck verwendet wird */
	@Getter @Setter private LettoConfigDto lettoConfigDto=null;

	/** User der den Ausdruck macht */
	@Getter @Setter private LettoUser user=null;
	@Getter @Setter private boolean questionService=false;
	@Getter @Setter private int spalte=0;
	
	/**
	 * Erzeugt einen neuen Print-Context für den Ausdruck mit Latex
	 * @param document     Dokument, dass gedruckt werden soll
	 * @param filename     Dateiname der tex-Datei und der zukünftigen PDF-Datei
	 * @param mode         Modus des Ausdruckes
	 * @param lettoConfigDto Config
	 * @param user		   Benutzer der gerade druckt
	 */
	public TexPrintContext(TexPrintable document, String filename, TexPrintMode mode, LettoConfigDto lettoConfigDto, LettoUser user) {
		this.document = document;
		this.images   = new Vector<File>();
		clearTexCode();
		this.mode     = mode;
		this.pp       = new PrintParameter();
		//filename      = Cmd.getFileName(new File(filename));  
		if (filename==null || filename.length()==0) filename="bsp";
		filename      = Cmd.renameFile(filename);
		this.filename = pp.TEX_Docs+"/"+filename;
		this.lettoConfigDto  = lettoConfigDto;
		this.user     = user;
		setZielDateiname(filename);
	}

	/**
	 * Erzeugt einen neuen Print-Context für den Ausdruck mit Latex
	 * @param mode         Modus des Ausdruckes
	 * @param lettoConfigDto Config
	 * @param user		   Benutzer der gerade druckt
	 */
	public TexPrintContext(TexPrintMode mode, LettoConfigDto lettoConfigDto, LettoUser user) {
		this.document = null;
		this.images   = new Vector<File>();
		clearTexCode();
		this.mode     = mode;
		this.pp       = new PrintParameter();
		this.lettoConfigDto  = lettoConfigDto;
		this.user     = user;
	}
	
	/**
	 * Erzeugt mittels pdflatex eine PDF-Datei oder mehrere PDF-Dateien als Zip und gibt einen Handle 
	 * darauf zurück
	 * @return PDF-Datei oder null wenn etwas nicht funktioniert hat.
	 */
	public File generateFile() {
		
		// jetzt den Tex-Code erzeugen
		File f = document.generateFile(this);

		// wenn notwendig zippen und zurückgeben der Datei
		if (f!=null) {
			if (zip && !(f.getAbsolutePath().endsWith(".zip"))) {
				ZipFileWriter zf = null;
				try {
					zf = new ZipFileWriter(zipziel);
					zf.addFile(pdfziel);
					zf.close();
					f = new File(zipziel);
				} catch (IOException e) {
					if (zf!=null) zf.close();
				}
			} 
			if (f.exists()) 
				return f;
		}
		return null;
	}
		
	/**
	 * Setzt den Ziel-Dateinamen bestehend aus dem richten Verzeichnis, dem Namen (name) und dem richtigen Suffix
	 * @param bezeichner Haupt-Bezeichner der Datei welcher als Basis für den Dateinamen verwendet wird.
	 */
	public void setZielDateiname(String bezeichner) {
		bezeichner = Cmd.renameFile(bezeichner);		
		if (mode.getPrintmode()==PRINTMODE.PRINTERGEBNIS) bezeichner += "-korrekt";
		if (mode.getPrintmode()==PRINTMODE.PRINTANTWORT) bezeichner += "-schuelerantwort";
		if (mode.getPrintmode()==PRINTMODE.PRINTANTWORTERGEBNIS) bezeichner += "-schuelerantwort-korrekt";
		if (mode.getPrintmode()==PRINTMODE.PRINTEINSICHTNAHME) bezeichner += "-einsichtnahme";
		if (mode.getPrintmode()==PRINTMODE.PRINTFORMEL) bezeichner += "-loesung";
		if (mode.getPrintmode()==PRINTMODE.PRINTINFO) bezeichner += "-info";
		String pdfpfad = (pp.PDFPfad==null||pp.PDFPfad.length()==0)?".":pp.PDFPfad;
		pdfziel = pdfpfad+"/"+bezeichner+".pdf";
		zipziel = pdfpfad+"/"+bezeichner+".zip";
		File fp = new File(pdfpfad);
		if (!fp.exists()) {
			try {
				Files.createDirectory(fp.toPath());
			} catch (IOException e) {
				throw new RuntimeException( "Verzeichnis ist nicht erstellbar! : "+fp.getAbsolutePath());
			}
		}		
	}
	
	/**
	 * compiliert den aktuellen Tex-Source-Code in eine PDF-Datei
	 * @return Datei
	 */
	public File compileTex() {
		LettoTimer.checkInterrupt();
		Vector<String> out = new Vector<String>();
		// Dreimal den Compiler starten wegen des Inhaltsverzeichnisses und der Index-Files
		try {
			Tex.callPDFTex(filename,out,pp);
			Tex.callPDFTex(filename,out,pp);
			Tex.callPDFTex(filename,out,pp);
		} catch (Exception e1) {
			return null;
		}
		
		File f = new File(filename+".pdf");
		File z = new File(pdfziel);
		if (!f.exists()) return null;
		
		if (f.getAbsolutePath().equals(z.getAbsolutePath())) 
			return f;
		if (z.exists()) z.delete();
		if (f.exists() && !z.exists()) {
			if (f.renameTo(z))
			{
				if ((new File(pdfziel)).exists()) {
					return z;
				}
			}					
		}
		return null;
	}
	
	/** löscht den kompletten TexCode */
	public void clearTexCode() {
		this.texCode = new Vector<String>();
	}
	
	/**
	 * Löscht alle temporären Dateien des Tex-Ausdrucks und die .tex-Datei
	 */
	public void removeTempFiles() {
		for (File img:images) {
			if (img.exists() && img.isFile()) 
				img.delete();			
		}
	}
	
}
