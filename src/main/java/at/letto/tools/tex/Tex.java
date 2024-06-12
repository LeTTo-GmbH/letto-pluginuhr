package at.letto.tools.tex;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import at.letto.ServerConfiguration;
import at.letto.globalinterfaces.ImageService;
import at.letto.globalinterfaces.LettoFile;
import at.letto.globalinterfaces.LettoUser;
import at.letto.tools.*;
import at.letto.tools.dto.FileBase64Dto;
import at.letto.tools.html.HTMLMODE;
import at.letto.tools.threads.CallAdapter;
import at.letto.tools.threads.LettoTimer;
import at.letto.tools.threads.TimerCall;
import at.letto.tools.threads.TimerCall.CallResult;
import at.letto.tools.threads.TimerCall.RESULT;
import javax.imageio.ImageIO;
import java.io.*;

/**
 * Nützliche Methoden für den Export nach tex
 * @author damboeck
 *
 */
public abstract class Tex {
	
	/** runde Klammer auf im Formelsatz */
	public static final String KLAMMERAUF = "\\left( ";
	
	/** runde Klammer zu im Formelsatz */
	public static final String KLAMMERZU  = "\\right) ";
	
	/** Winkelangaben im Formelsatz */
	public static final String WINKEL     = "\\angle ";
	
	/** Minus-Vorzeichen im Formelsatz */
	public static final String MINUSVORZEICHEN = "{-}";
	
	/** Mal-Punkt im Formelsatz */
	public static final String MAL = "\\cdot ";
	
	/** Grad-zeichen im Formelsatz */
	public static final String GRADCOMPLEX = "^{\\,\\circ}";
	public static final String GRADSHORT   = "^{\\circ}";
	
	/** Euro-Symbol */
	public static final String EURO       = "€";
	
	/** neue Zeile */
	public static final String LF         = "\n\n";
	
	/** neuer Absatz */
	public static final String PAR        = "\\par\n";
	
	/** neue Seite */
	public static final String FF         = "\\newpage\n";
	
	/** Farbdefinitionen für Testdruck */
	public static final String ColorDef  = 
			"\\definecolor{colorangabefehler}{cmyk}{0.3,0.3,0.0,0}\n" +
			"\\definecolor{colorok}{cmyk}{0.3,0.0,0.3,0}\n" +
			"\\definecolor{coloroktext}{cmyk}{0.7,0,0.7,0.3}\n" +
			"\\definecolor{colorteilrichtig}{cmyk}{0.0,0.0,0.3,0}\n" +
			"\\definecolor{colortextteilrichtig}{cmyk}{0.0,0.12,1,0.5}\n" +
			"\\definecolor{colorinfo}{cmyk}{0.8,0.2,1,0.2}\n" +
			"\\definecolor{colorfalsch}{cmyk}{0.0,0.3,0.3,0}\n";
			
	/** Legend im Textkopf */
	public static final String ColorTestLegende = "{\\small\\color{lightgray}";
	
	/** Farbe der Lösung beim Fragedruck */
	public static final String ColorLoesung = "{\\color{blue}";
	
	/** Farbe der Schülerantwort beim Fragedruck */
	public static final String ColorAntwort = "{\\color{cyan}";
	
	/** Farbe des Feedback beim Fragedruck */
	public static final String ColorFeedback = "{\\color{blue}";
	
	/** Farbe der Question Info beim Fragedruck */
	public static final String ColorQuestionInfo = "{\\small\\color{colorinfo}";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortRichtig = "\\colorbox{colorok}{";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortFalsch = "\\colorbox{colorfalsch}{";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortTextRichtig = "{\\color{coloroktext}";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortTextFalsch = "{\\color{colorfalsch}";

	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortTextTeilRichtig = "{\\color{colortextteilrichtig}";

	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortTeilrichtig = "\\colorbox{colorteilrichtig}{";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortAngabeFehler = "\\colorbox{colorangabefehler}{";
	
	/** Farbe einer richtigen Antwort beim Fragedruck */
	public static final String ColorAntwortNichtentschieden = "\\colorbox{lightgray}{";

	public static final String FormatH1 = "{\\bfseries\\huge ";
	public static final String FormatH2 = "{\\bfseries\\LARGE ";
	public static final String FormatH3 = "{\\Large ";
	public static final String FormatH4 = "{\\large ";
	public static final String FormatH5 = "{\\small ";
	public static final String FormatH6 = "{\\tiny ";
	public static final String FormatAddress = "{\\itshape ";
	public static final String FormatPre     = "\\texttt{";

	private static HashMap<String,String> FUNCTIONS = null;
	
	/**
	 * Functions wo der Texname nicht durch voranstellen von einem Backslash möglich ist, da der Funktionsname anders ist als in Letto
	 */
	private static final String[][] FUNC  = {{"asin","\\arcsin"},{"acos","\\arccos"},{"atan","\\arctan"}};
	/**
	 * Functions wo der Texname durch voranstellen eines Backslash erfolgt
	 */
	private static final String[]   BFUNC = {"sin","cos","tan","cot","arccos","arcsin","arctan","sinh","cosh","tanh","coth"};
	
	/**
	 * MODUS wie der Tex-Ausdruck erfolgen soll:<br>
	 * PRINTFRAGE     : Ausgabe des reinen Fragetextes<br>
	 * PRINTERGEBNIS  : Ausgabe von Frage und Antwort<br>
	 * PRINTFORMEL    : Ausgabe von Frage, Antwort und Berechnungsformel<br>
	 * PRINTINFO      : Ausgabe von Frage, Antwort, Berechnungsformel und Info-Elementen<br>
	 * PRINTHTML      : direkte Druckausgabe auf dem Browser als HTML-Ansicht, Tex wird dabei nicht verwendet<br>
	 * PRINTANTWORT   : Ausgabe von Frage und Schülerantwort<br>
	 * PRINTANTWORTERGEBNIS : Ausgabe von Frage und Schülerantwort und Ergebnis<br>
	 * PRINTEINSICHTNAHME : Einsichtnahme durch den Studenten mit Schülerergebnissen jedoch ohne Multiple-Choice und Zuordnungsfragen<br>
	 * @author damboeck
	 *
	 */
	public static enum PRINTMODE{ PRINTFRAGE, PRINTERGEBNIS , PRINTFORMEL , PRINTINFO , PRINTHTML, PRINTANTWORT, PRINTANTWORTERGEBNIS, PRINTEINSICHTNAHME;
		public HTMLMODE getHTMLMODE() {
			switch (this) {
			case PRINTFRAGE    : return HTMLMODE.TEXN;
			case PRINTERGEBNIS : return HTMLMODE.TEXA;
			case PRINTFORMEL   : return HTMLMODE.TEXF;
			case PRINTINFO     : return HTMLMODE.TEXI;
			case PRINTHTML     : return HTMLMODE.HTML;	
			case PRINTANTWORT  : return HTMLMODE.TEXS;
			case PRINTANTWORTERGEBNIS  : return HTMLMODE.TEXSE;
			case PRINTEINSICHTNAHME: return HTMLMODE.TEXEN;
			}
			return HTMLMODE.HTML;
		}
		/**
		 * @return true wenn die Schülerantwort gedruckt wird
		 */
		public boolean antwort() {
			switch (this) {
				case PRINTANTWORT:
				case PRINTEINSICHTNAHME:
				case PRINTANTWORTERGEBNIS  : return true;
				default:
					break;
			}
			return false;
		}
		/**
		 * @return true wenn das Ergebnis gedruckt wird
		 */
		public boolean ergebnis() {
			switch (this) {
			case PRINTANTWORTERGEBNIS  :
			case PRINTERGEBNIS : 
			case PRINTFORMEL   : 
			case PRINTINFO     : return true;
			default:
				break;
			}
			return false;
		}
		/**
		 * @return true wenn die Formel gedruckt wird
		 */
		public boolean formel() {
			switch (this) {
			case PRINTFORMEL   : 
			case PRINTINFO     : return true;
			default:
				break;
			}
			return false;
		}
	} 
		
	/**
	 * fortlaufende Nummer für die temporären Bilder für den Tex Export 
	 */
	private static int tmpImgNr=1;
			
	/**
	 * Startet PDFTex mit der Datei texdatei 
	 * @param texdatei  ohne Extension
	 * @param out       Ausgabe der Commandline
	 * @param pp        Print Parameter
	 */
	public static void callPDFTex(String texdatei,Vector<String> out, PrintParameter pp) {
		File f = new File(texdatei+".tex");
		if (!f.exists()) throw new RuntimeException(texdatei+".tex nicht gefunden");
		texdatei = f.getAbsolutePath();
		
		Matcher m = Pattern.compile("^(?<pre>.*)(\\\\|/)\\.(?<suf>(\\\\|/).*)$").matcher(texdatei);
		if (m.find()) texdatei = m.group("pre")+m.group("suf");
		
		texdatei = texdatei.substring(0,texdatei.length()-4);
		f = new File(texdatei+".pdf");
		if (f.exists()) f.delete();
		if (!f.exists()) {
			String cmd = "";
			if (pp.isWindows) cmd = "\""+pp.TEX_Compiler+"\" "+pp.TEX_Parameter;
			else cmd = pp.TEX_Compiler+" "+pp.TEX_Parameter;
			cmd = cmd.replace("%d","\""+pp.TEX_Docs+"\"");
			if (cmd.contains("%f")) cmd = cmd.replace("%f","\""+texdatei+"\"");
			else                    cmd += " \""+texdatei+"\"";			
			out.add(Cmd.systemcall(cmd));
			if (!f.exists()) 
				throw new RuntimeException(texdatei+".pdf konnte nicht erstellt werden! cmd:"+cmd+" out:"+out);			
		} else { 
			throw new RuntimeException(texdatei+".pdf konnte nicht gelöscht werden");
		}
	}
		
	/**
	 * Klasse welche alle Config-Parameter welche aus der globalen Bean geholt werden speichert.<br>
	 * Ist notwendig, um den Ausdruck in einer Timerroutine starten zu können.
	 * @author Werner Damböck
	 *
	 */
	public static class PrintParameter{
		public boolean isWindows;
		public String  TEX_Compiler;
		public String  TEX_Parameter;
		public String  TEX_Docs;
		public String  PDFPfad;
		public String  DocInitTest;
		public String  DocStartTest;
		public String  DocInit;
		public String  DocInitQuestion;
		public String  DocStart;
		public String  DocStartQuestion;
		public Vector<String> moodle_sty;
		public Vector<String> rdp_sty;
		public PrintParameter() {
			isWindows    = ServerConfiguration.service.isWindows();
			TEX_Compiler = ServerConfiguration.service.Get("TEX_Compiler");
			TEX_Parameter= ServerConfiguration.service.Get("TEX_Parameter");
			TEX_Docs     = ServerConfiguration.service.Get("TexDocs");
			PDFPfad      = ServerConfiguration.service.Get("PDFPfad");
			DocInitTest  = ServerConfiguration.service.Get("DocInitTest");
			DocStartTest = ServerConfiguration.service.Get("DocStartTest");			
			DocInit      = ServerConfiguration.service.Get("DocInit");
			DocInitQuestion=ServerConfiguration.service.Get("DocInitQuestion");
			DocStart     = ServerConfiguration.service.Get("DocStart");
			DocStartQuestion = ServerConfiguration.service.Get("DocStartQuestion");
			moodle_sty   = ServerConfiguration.service.loadConfigFile("moodle.sty");
			rdp_sty      = ServerConfiguration.service.loadConfigFile("rdpfragen.sty");
		}		
	}

	/**
	 * Erzeugt ein PDF eine Kategorie, Test oder Question//
	 * 
	 * Leider funktioniert der Timer nicht, wenn darin ein Commandozeilenprogramm ausgeführt wird. Woran das liegt weiß ich nicht!!
	 * Wahrscheinlich, weil der Zugriff auf die Conf-Variablen  in der Timer-Routine nicht richtig funktioniert, wenn sie von einem Benutzer aus der Datenbank überschrieben sind!!
	 * 
	 * @param document    Objekt, dass gedruckt werden soll
	 * @param name        Dateiname ohne Pfadangabe
	 * @param mode        Modus des Ausdruckes
	 * @param lettoConfigDto  Config
	 * @param user        Benutzer
	 * @return 		      Dateiname der erzeugten PDF-Datei als gesamter Pfad, Leerstring wenn keine Datei erzeugt wurde
	 */
	public static String generatePDF(TexPrintable document, String name, TexPrintMode mode, LettoConfigDto lettoConfigDto, LettoUser user) {
		TexPrintContext context = new TexPrintContext(document, name, mode, lettoConfigDto, user);
		return generatePDF(context,document, name, mode, lettoConfigDto, user);
	}


	/**
	 * Erzeugt ein PDF eine Kategorie, Test oder Question//
	 *
	 * Leider funktioniert der Timer nicht, wenn darin ein Commandozeilenprogramm ausgeführt wird. Woran das liegt weiß ich nicht!!
	 * Wahrscheinlich, weil der Zugriff auf die Conf-Variablen  in der Timer-Routine nicht richtig funktioniert, wenn sie von einem Benutzer aus der Datenbank überschrieben sind!!
	 * @param context 	  Tex-Context, der von aussen mit userspezifischen Daten gesetzt wird
	 * @param document    Objekt, dass gedruckt werden soll
	 * @param name        Dateiname ohne Pfadangabe
	 * @param mode        Modus des Ausdruckes
	 * @param lettoConfigDto  Config
	 * @param user        Benutzer
	 * @return 		      Dateiname der erzeugten PDF-Datei als gesamter Pfad, Leerstring wenn keine Datei erzeugt wurde
	 */
	public static String generatePDF(TexPrintContext context, TexPrintable document, String name, TexPrintMode mode, LettoConfigDto lettoConfigDto, LettoUser user) {
		CallResult ret = TimerCall.callMethode(new CallAdapter() {
			TexPrintContext c=null;
			@Override
			public Object callMethode(Object ... objects) {
				String filename;
				c       = (TexPrintContext)objects[0];
				File            pdfFile = c.generateFile();
				if (pdfFile!=null && pdfFile.exists() && pdfFile.isFile()) {
					filename = pdfFile.getAbsolutePath();
				} else {
					throw new RuntimeException("Tex konnte keine korrekte Datei erzeugen!");
				}
				return filename;
			}
			@Override public String getMethodeName() { return "generatePDF"; }
			@Override public String getMethodeInfo() { return  c!=null?c.getFilename():"null"; }
		},LettoTimer.getPrintTimer(),context);
		LettoTimer.checkInterrupt();
		context.removeTempFiles();
		if (ret.status==RESULT.OK) {
			if (ret.getResult() instanceof String) return (String)ret.getResult();
			throw new RuntimeException(name+" kann nicht erstellt werden!");
		} else if (ret.status==RESULT.TIMEOUT) {
			throw new RuntimeException("Timeout beim Druck!");
		} else if (ret.status==RESULT.TIMEOUTKILLED) {
			throw new RuntimeException("Timeout beim Druck, der Task wurde gekilled!");
		} else if (ret.status== TimerCall.RESULT.ZOMBIE) {
			return "Timeout beim Druck bei dem der Task als Zombie hängenblieb!";
		} else {
			throw new RuntimeException(name+" kann nicht erstellt werden!");
		} 	
		
	}
	
	/**
	 * Erzeugt einen Tex-Sourcecode für den Ausdruck //
	 * 
	 * Leider funktioniert der Timer nicht, wenn darin ein Commandozeilenprogramm ausgeführt wird. Woran das liegt weiß ich nicht!!
	 * Wahrscheinlich, weil der Zugriff auf die Conf-Variablen  in der Timer-Routine nicht richtig funktioniert, 
	 * wenn sie von einem Benutzer aus der Datenbank überschrieben sind!!
	 * 
	 * @param document    Objekt, dass gedruckt werden soll
	 * @param name        Dateiname ohne Pfadangabe
	 * @param mode        Modus des Ausdruckes
	 * @param lettoConfigDto  Config
	 * @param user        Benutzer
	 * @return 		      Dateiname der erzeugten PDF-Datei als gesamter Pfad, Leerstring wenn keine Datei erzeugt wurde
	 */
	public static String generateTex(TexPrintable document, String name, TexPrintMode mode, LettoConfigDto lettoConfigDto, LettoUser user) {
		TexPrintContext context = new TexPrintContext(document, name, mode, lettoConfigDto, user);
		try {
			document.generateTEX(context);			
			File texFile = new File(context.getFilename()+".tex");
			if (texFile!=null && texFile.exists() && texFile.isFile()) {
				// Bilder mitspeichern
				if (context.getImages().size()>0) {
					// Ersetze alle absoluten Pfade in der Tex-Datei durch relative Pfade
					Vector<String> tex    = Cmd.readfile(texFile);
					Vector<String> texNeu = new Vector<String>();
					String imagePath = Cmd.getFileDirectory(texFile)+"images/";
					imagePath = RegExp.verblockeString(imagePath);
					for (String line:tex) {
						line = line.replaceAll(imagePath,"");
						texNeu.add(line);
					}
					Cmd.writelnfile(texNeu,texFile);
					// Zip erzeugen
					File zipfile = new File(context.getFilename()+".zip");
					ZipFileWriter zf = null;
					try {
						zf = new ZipFileWriter(zipfile);
						zf.addFile(texFile.getAbsolutePath());
						for (File img:context.getImages())
							zf.addFile(img);
						zf.close();
						context.removeTempFiles();
						return zipfile.getAbsolutePath();
					} catch (IOException e) {
						if (zf!=null) zf.close();
					}
				}
				context.removeTempFiles();
				return texFile.getAbsolutePath();
			}			
		} catch (Exception ex) { }
		context.removeTempFiles();
		throw new RuntimeException("Es konnte keine korrekte Tex-Datei erzeugt werden!");		 	
	}

	/**
	 * Erzeugt einen Tex-Sourcecode für den Ausdruck //
	 *
	 * Leider funktioniert der Timer nicht, wenn darin ein Commandozeilenprogramm ausgeführt wird. Woran das liegt weiß ich nicht!!
	 * Wahrscheinlich, weil der Zugriff auf die Conf-Variablen  in der Timer-Routine nicht richtig funktioniert,
	 * wenn sie von einem Benutzer aus der Datenbank überschrieben sind!!
	 *
	 * @param document      Objekt, dass gedruckt werden soll
	 * @param name          Dateiname ohne Pfadangabe
	 * @param mode          Modus des Ausdruckes
	 * @param lettoConfigDto  Config
	 * @param user        Benutzer
	 * @param zipFileWriter Zip-Datei in die geschrieben werden soll
	 * @param path	        Pfad innerhalb der Zip-Datei
	 */
	public static void generateTexToZip(TexPrintable document, String name,
										TexPrintMode mode,
										LettoConfigDto lettoConfigDto, LettoUser user,
										ZipFileWriter zipFileWriter, String path) {
		TexPrintContext context = new TexPrintContext(document, name, mode, lettoConfigDto, user);
		try {
			document.generateTEX(context);
			File texFile = new File(context.getFilename()+".tex");
			if (texFile!=null && texFile.exists() && texFile.isFile()) {
				// Bilder mitspeichern
				if (context.getImages().size()>0) {
					// Ersetze alle absoluten Pfade in der Tex-Datei durch relative Pfade
					Vector<String> tex    = Cmd.readfile(texFile);
					Vector<String> texNeu = new Vector<String>();
					String imagePath = Cmd.getFileDirectory(texFile)+"images/";
					imagePath = RegExp.verblockeString(imagePath);
					for (String line:tex) {
						line = line.replaceAll(imagePath,"");
						texNeu.add(line);
					}
					Cmd.writelnfile(texNeu,texFile);
					// Zip anfügen
					try {
						zipFileWriter.addDirectory(path);
						zipFileWriter.addFile(path,texFile,texFile.getName());
						for (File img:context.getImages())
							zipFileWriter.addFile(path,img,img.getName());
						context.removeTempFiles();
						return;
					} catch (IOException e) {
					}
				}
				context.removeTempFiles();
				return;
			}
		} catch (Exception ex) { }
		context.removeTempFiles();
	}
	
	/**
	 * Wandelt einen String um, so dass er in Tex compilierbar ist
	 * @param s        String
	 * @return         Tex-String
	 */
	public static String stringToTex(String s) {
		if (s==null) return "";
		return stringToTex(s,false);
	}
	
	/**
	 * Wandelt einen String um, so dass er in Tex compilierbar ist
	 * @param s        String
	 * @param listing  gibt an ob der Text innerhalb eines Listings steht
	 * @return         Tex-String
	 */
	public static String stringToTex(String s,boolean listing) {
		String ret=s;
		String htmllist[][] = {					
				// --------------- html tags ersetzen ----------------	
				{"\\&lt;","<"},
				{"\\&gt;",">"},
				{"\\&ouml;","\"o"},
				{"\\&uuml;","\"u"},
				{"\\&auml;","\"a"},
				{"\\&Ouml;","\"O"},
				{"\\&Uuml;","\"U"},
				{"\\&Auml;","\"A"},
				{"\\&[sS]uml;","\"s"},
				{"\\&szlig;","\"s"},
				{"\\&#39;","'"},
				{"\\&amp;","\\\\&"},	        
		        };         
		
		String texlist[][] = {    
				{"\\&{1,4}\\;",""},					   // nicht bekannte Entitäten löschen
		        {"\\$","\\\\dollar "},				   // $
		        {"\\&","\\\\\\&"},                     // & 
		        {"\\|","\\$\\\\vert\\$"},                // |
		        // --------------- Sonderzeichen für tex ---------------------
				{"\\<[uo]l\\s*\\>","\\\\begin\\{itemize\\}"},   // <ul><ol>
		        {"\\<\\/[uo]l\\s*\\>","\\\\end\\{itemize\\}"},
		        {"\\<li\\s*\\>","\\\\item "},                   // <li>
		        {"\\<\\/li\\s*\\>",""},
		        {"\\<\\/*span[^\\>]*\\>",""},                   // <span>
		        {"\\<\\/*strong[^\\>]*\\>",""},                 // <strong>
		        {"\\<\\/*font[^\\>]*\\>",""},                   // font
				{"\\~","\\\\tild "},				 // ~	
				{"\\'","\\\\hk "},					 // '
				{"\\`","\\\\hkb "},					 // `
				{"\\%","\\\\prozent "},				 // %				        
				{"\\^","\\\\dach "},  				 // ^
		        {"\\#","\\\\raute "},				 // #		        
		        {"\\<\\-\\>","\\\\pfeillr "},        // <->
		        {"\\<\\=\\>","\\\\pfeillr "},        // <=>
		        {"\\-\\>","\\\\pfeil "},			 // ->
		        {"\\=\\>","\\\\pfeil "},			 // =>		        
		        {"\\>","\\$\\>\\$"},          		 // >  
		        {"\\<","\\$\\<\\$"},     		     // < 
		        {"_","-"},
				// Umlaute
				{"ö","\"o"},
				{"ä","\"a"},
				{"ü","\"u"},
				{"Ö","\"O"},
				{"Ä","\"A"},
				{"Ü","\"U"},
				{"ß","\"s"},
		        // --------------- Alles entfernen was nicht erlaubtes UTF8 ist --------------------
				{"[^\\\\\\_a-zA-Z0-9öäüÖÄÜß\\(\\)\\[\\]\\{\\}\\,\\.\\-\\;\\:\\#\\'\\+\\*\\!\\§\\s\\$\\&\\<\\>\\/\\=]\\|",""}};
				
		if (!listing) {
			ret = ret.replaceAll("\\\\","\\\\bs");
			ret = ret.replaceAll("\"","\\\\grqq ");
		}
		for (String rep[]:htmllist) ret = ret.replaceAll(rep[0],rep[1]);
        if (!listing) {
        	ret = ret.replaceAll("\\{","\\\\lgk ");
    		ret = ret.replaceAll("\\}","\\\\rgk ");
        	for (String rep[]:texlist) ret = ret.replaceAll(rep[0],rep[1]);
        }
		return ret;
	} 
		
	/**
	 * Wandelt einen Maxima-String in eine für Tex verwendbare Form
	 * @param s  Maxima String
	 * @return  Tex verwendbarer String
	 */
	public static String maximaToTex(String s) {
		s = s.replaceAll("[\\r\\n]+","<br>");
		return "\\mp{\\color{blue}"+stringToTex(s,false)+"}"+LF;
	}
		
	public static String bruch(String Z, String N) {
		return "\\dfrac{"+Z+"}{"+N+"}";
	}
	
	public static String bruchklein(String Z, String N) {
		return "\\tfrac{"+Z+"}{"+N+"}";
	}
	
	public static String wurzel(String s) {
		return wurzel("",s);
	}
	
	public static String wurzel(String n, String s) {
		if (n==null || n.length()==0 || n.equals("2")) 
			return "\\sqrt{"+s+"}";
		return  "\\sqrt["+n+"]{"+s+"}";
	}	
	
	public static String clozeBox() {
		return clozeBox(30,1);
	}
	
	public static String clozeBox(int width) {
		return clozeBox(width,1);
	}
	
	/**
	 * Antwort-Box mit definierter Breite
	 * @param width   Breite in mm
	 * @param height  Höhe in Zeilen
	 * @return        Tex Code für hellgelbe Antwortbox
	 */
	public static String clozeBox(int width, int height) {
		String ret = "\\colorbox{lightyellow}{\\fbox{\\raisebox{"+(height*25)+"pt}{\\hbox to "+width+"mm{}}}}";
		return ret;
	}
	
	/**
	 * Speichert das Bild im String image in eine temporäre Datei
	 * jpg,pdf oder png werden in eine Datei gespeichert
	 * svg und png wird in eine pdf-Datei konvertiert!
	 * andere Datentypen bleiben erhalten.
	 * @param image    kodierte Datei
	 * @param context  Tex-Printcontext
	 * @return         absoluter Dateipfad wo die Datei gespeichert wurde
	 */
	public static String saveTmpImg(LettoFile image,TexPrintContext context){
		String filename[] = Cmd.saveTmpImg(image, context.getPp().TEX_Docs+"/images", tmpImgNr++, 0);
		context.getImages().add(new File(filename[1]));
		return filename[1];
	}

	/**
	 * Speichert das UEncoded-Image im String image in eine temporäre Datei
	 * jpg,pdf oder png werden in eine Datei gespeichert
	 * svg und png wird in eine pdf-Datei konvertiert!
	 * andere Datentypen bleiben erhalten.
	 * @param UEncodedFile Image-Daten
	 * @param context   Tex-Printcontext
	 * @param extension File-Extension
	 * @return          absoluter Dateipfad wo die Datei gespeichert wurde
	 */
	public static String saveTmpImg(String UEncodedFile, TexPrintContext context, String extension){
		String filename = Cmd.saveTmpImgJpg(UEncodedFile, context.getPp().TEX_Docs+"/images", tmpImgNr++, 0, extension);
		context.getImages().add(new File(filename));
		return filename;
	}

	public static String getImageString(String filename) {
		filename = filename.trim().replaceAll("\\\\", "\\/");
		String fn[]      = filename.split("\\.");
		if (fn.length<2) return "";
		String extension = fn[fn.length-1];
		switch (extension.toLowerCase()) {
			case "png": case "jpg": case "jpeg": {
				int width = 10000;
				int height= 10000;
				try {
					BufferedImage bufferedImage = ImageIO.read(new File(filename));
					width  = bufferedImage.getWidth();
					height = bufferedImage.getHeight();
				} catch (Exception ex) {
				}
				if (width > 500) {
					if (height>width) {
						return "\\includegraphics[width="+((double)width/height)+"\\textwidth]{" + filename + "}";
					}
					return "\\includegraphics[width=\\textwidth]{" + filename + "}";
				}else
					return "\\includegraphics{" + filename + "}";
			}
			case "pdf": {
				return "\\includepdf[pages=1-]{" + filename + "}";
			}
			default:
				return filename+" kann nicht eingebunden werden!\n\n";
		}
	}

	public static String getImageString(FileBase64Dto fileBase64Dto) {
		String filename = fileBase64Dto.getName();
		switch (fileBase64Dto.getExtension()) {
			case "png": case "jpg": case "jpeg": {
				int width = 10000;
				int height= 10000;
				try {
					BufferedImage bufferedImage = ImageService.base64StringToImg(fileBase64Dto.getBase64());
					width  = bufferedImage.getWidth();
					height = bufferedImage.getHeight();
				} catch (Exception ex) {
				}
				if (width > 500) {
					if (height>width) {
						return "\\includegraphics[width="+((double)width/height)+"\\textwidth]{img/" + filename + "}";
					}
					return "\\includegraphics[width=\\textwidth]{img/" + filename + "}";
				}else
					return "\\includegraphics{img/" + filename + "}";
			}
			case "pdf": {
				return "\\includepdf[pages=1-]{img/" + filename + "}";
			}
			default:
				return filename+" kann nicht eingebunden werden!\n\n";
		}
	}

	/**
	 * erzeugt den Eintrag für eine Minipage
	 * @param s    Inhalt der Minipage
	 * @param l    Breite der Minipage
	 * @return     erzeugte Minipage
	 */
	public static String minipage(String s,String l) {
		return "\\begin{minipage}{"+l+"}"+s+"\\end{minipage}";
	}
	
	/**
	 * erzeugt mit einer Minipage einen horizontalen Abstand 
	 * @param x    Abstand in mm
	 * @return     Tex-Code
	 */
	public static String skip(int x){
		return "\\hskip "+x+"mm ";
	}
	/**
	 * Liefert die Tex-Schreibweise einer Funktion
	 * @param name Name der Funktion
	 * @return     Tex-Schreibweise
	 */
	public static String getFunction(String name) {
		if (FUNCTIONS==null) {
			FUNCTIONS = new HashMap<String,String>();
			for (String f[]:FUNC) 
				try {
					FUNCTIONS.put(f[0],f[1]);
				} catch (Exception e) {
					//Log.Msg("Tex Funktion kann nicht geladen werden!"+e.getMessage());
				};	
			for (String f:BFUNC) 
				if (f.length()>0) FUNCTIONS.put(f, "\\"+f);
		}
		if (FUNCTIONS.containsKey(name))
			return FUNCTIONS.get(name)+" ";
		name = "\\mathrm{"+name+"}";
		return name;
	}
	
	public static String generateTestKopf(String geg, String klasse, String gruppe, 
			                              String testNr, String name,String datum,
			                              String punkte, String prozent, String note) {
		String ret;
		String v = "\\vspace{2mm}";
		ret = "\\begin{tabular}{|p{10mm}p{50mm}|p{18mm}p{47mm}|p{11mm}p{30mm}|}\\hline\n";
		geg    = geg;
		klasse = klasse;
		gruppe = gruppe;
		testNr = testNr;
		name   = name;
		datum  = datum;
		punkte = punkte;
		prozent= prozent;
		note   = note;
		ret +=Tex.ColorTestLegende+" Geg.}   &"+v+geg+"&"+Tex.ColorTestLegende+" Bezeichnung}&"+v+testNr+"&"+Tex.ColorTestLegende+" Punkte}&"+v+punkte+"\\\\\\hline\n";
		ret +=Tex.ColorTestLegende+" Klasse} &"+v+klasse+"&"+Tex.ColorTestLegende+" Name}&"+v+name+"&"+Tex.ColorTestLegende+" Prozent}&"+v+prozent+"\\\\\\hline\n";
		ret +=Tex.ColorTestLegende+" Gruppe}&"+v+gruppe+"&"+Tex.ColorTestLegende+" Datum}&"+v+datum+"&"+Tex.ColorTestLegende+" Note}&"+v+note+"\\\\\\hline\n";
		ret +="\\end{tabular}\n\n";
		return ret;
	}
	
	public static String generateBspKopf(int nr, String name, String pointscolor, String pointsSoll, String pointsIst, boolean line) {
		String ret = "";
		if (line) {			
		    ret = "\\begin{tabular}{|p{130mm}|p{25mm}|p{25mm}}\n" +
		    	  "\\hline "+nr+". "+name+"&Soll:"+pointsSoll+"& Ist:"+pointscolor+pointsIst+"}\\\\\\end{tabular} \\\\";
		} else {
			if (pointsIst.length()>0) pointsIst += "/";
			ret = FormatH2+nr+". "+name+pointscolor+"("+pointsIst+pointsSoll+")}}\n\n";
		}
		return ret;
	}
		
}
