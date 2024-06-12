package at.letto.tools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.*;

import at.letto.ServerConfiguration;

/**
 * Routinen für die Suche und Installation von externen Programmen
 * @author damboeck
 *
 */
public class Install {
		
	/**
	 * @param args  Kommandozeilenparameter
	 */
	public static void main(String[] args) {
		String value="";		
		value = getWindowsVersion();
		
		System.out.println("Programme:"+getProgramPath());
		
		if (ServerConfiguration.service.isWindows()) System.out.println("Windows Distribution = " + value);  
		System.out.println("Maxima:"+findMaxima(false));
		System.out.println("Inkscape:"+findInkscape(false));
		System.out.println("PDFView:"+findPDFViewer(false));
		System.out.println("Bitmaps:"+findBitmapViewer(false));
		System.out.println("TeX:"+findTex(false));
		System.out.println("TeXIDE:"+findTexIDE(false));
		System.out.println("TextEdit:"+findTextEditor(false));
		System.out.println("Calc:"+findCalc(false));
		System.out.println("Program:"+getProgramPath());
		/*
		System.out.println("Geany:"+findWindows("geany.exe"));
		System.out.println("MAxima:"+findWindows("maxima.bat"));
		System.out.println("Inkscape:"+findWindows("inkscape.exe"));
		System.out.println("Acrobat:"+findWindows("acrord.exe"));
		System.out.println("Foxit Reader:"+findWindows("FOXIT READER.EXE"));
		System.out.println("Gimp:"+findWindows("gimp-2.8.exe"));
		System.out.println("Latex:"+findWindows("pdflatex.exe"));
		System.out.println("Excel:"+findWindows("excel.exe"));
		*/
		System.out.println("fertig!!");
	}
	
	public static MatchResult result;
	
	public static boolean match(String pattern, CharSequence s) {
		boolean gefunden = false;
		Matcher m = Pattern.compile(pattern).matcher(s);
		if (m.find()) {
			gefunden = true;
			result = m.toMatchResult();
		}
		return gefunden;		
	}
	
	/**
	 * Sucht ob ein Commando unter Linux existiert und gibt den Pfad des Programmes zurück
	 * @param cmd     Kommando
	 * @param install Installation durchführen
	 * @return        Pfad des Programms in Linux
	 */
	private static String findLinux(String cmd, boolean install) {
		if (ServerConfiguration.service.isLinux()) {
			String s = Cmd.systemcall("which "+cmd).replace("\n","");
			if (s.length()>0) {  // Cmd wurde gefunden
				return s;
			} else {
				if (install) {
					if (!ServerConfiguration.service.isServer()) {
						Cmd.systemcall("gksudo apt-get install -y "+cmd);
						s = Cmd.systemcall("which "+cmd).replace("\n","");
						return s;
					}
				}
			}
		}
		return "";
	}
	/**
	 * Sucht im Verzeichnis verz nach dem angegebenen Programm
	 * @param verz  Verzeichnis
	 * @param cmd  Programmname der Startdatei
	 * @return     Gefundener Pfad des Programmes
	 */
	public static String findWindows(String verz, String cmd) {
		if (ServerConfiguration.service.isWindows()) {
			verz = verz+"\\";
			verz = verz.replaceAll("\\\\\\\\","\\");
			String cmdlst[] = new String[3];
			cmdlst[0] = "cmd";
			cmdlst[1] = "/C";
			cmdlst[2] = "dir /s /W \""+verz+cmd+"\"";
			String ret[] = Cmd.systemcall(cmdlst).split("\n");
			String path="";
			for (String s:ret) {
				if (match("\\s+Verzeichnis von\\s(.+)", s)) path=result.group(1);
				else if (match(".*"+cmd.toLowerCase(), s.toLowerCase())) return path+"\\"+result.group(0);
			}
		}
		return "";
	}
	/**
	 * Sucht im Programmpfad nach dem angegebenen Programm
	 * @param cmd      Kommando
	 * @return String Ergebnis
	 */
	public static String findWindows(String cmd) {
		String ret="";
		if ((ret=findWindows(getProgramPath()         ,cmd)).length()>0) return ret;
		if ((ret=findWindows("C:\\Programme"          ,cmd)).length()>0) return ret;
		if ((ret=findWindows("C:\\Programme (x86)"    ,cmd)).length()>0) return ret;
		if ((ret=findWindows("C:\\Program Files"      ,cmd)).length()>0) return ret;
		if ((ret=findWindows("C:\\Program Files (x86)",cmd)).length()>0) return ret;
		return ret;
	}
	/**
	 * Installiert die angegebene Anwendung  "cmd*"
	 * @param cmd Kommando
	 */
	public static void installWindows(String cmd) {
		File download=new File(ServerConfiguration.service.Get("Pfad")+"\\download");
		if (download.isDirectory()) {
			for (File f:download.listFiles()) {
				if (f.isFile() && f.getName().matches(cmd)) {
					//String path = elevate.getAbsolutePath()+" -wait \""+f.getAbsolutePath()+"\"";
					String path = "cmd /C \""+f.getAbsolutePath()+"\"";
					path = path.replace("\\.\\","\\");
					System.out.println("Install:"+path);
					Cmd.systemcall(path);
				}
			}
		} else {
			System.out.println("Download-Verzeichnis fehlt");
		}
	}
	/**
	 * Liefert das System-Wurzelverzeichnis von Windows
	 * @return  System Wurzelverzeichnis
	 */
	public static String getSystemRoot() {
		return readHKLM("SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion","SystemRoot");
	}
	/**
	 * Gibt den Suchpfad des Systems zurück
	 * @return  Suchpfad des Systems
	 */
	public static String getPath() {
		if (ServerConfiguration.service.isWindows()) {
			return readHKLM("SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment","Path");
		}
		return "";
	}
	/**
	 * Liefert den Pfad der Windows-Programme
	 * @return String
	 */
	public static String getProgramPath() {
		if (ServerConfiguration.service.isWindows()) {
			return readHKLM("SOFTWARE\\Microsoft\\Windows\\CurrentVersion","ProgramFilesDir");
		}
		return "";
	}
	/**
	 * Liest einen Wert des Feldes "name" aus einem Key "key" aus
	 * @param key     Schlüssel in der Registry
	 * @param name    Feldname in der Registry
	 * @return      Wert des Feldes
	 */
	private static String readHKLM(String key,String name) {
		String value="";
		try {
			value = WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE,key,name);
		} catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException | NullPointerException e) {			
		}
		if (value==null) value="";
		return value;
	}
	/**
	 * Liefert die Bezeichnung der verwendeten Windows-Version aus der Registry zurück
	 * @return  Windows Version
	 */
	public static String getWindowsVersion() {
		return readHKLM("SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion","ProductName");
	}
	
	/**
	 * Sucht nach Maxima und installiert es bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findMaxima(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("maxima",install);
		if (ServerConfiguration.service.isWindows()) {
			File f;
			String path = readHKLM("SOFTWARE\\Classes\\Maxima.wxMaxima\\shell\\open\\command","");
			if (match(".*\"([^\"]+)\\\\wxMaxima\\\\wxMaxima.exe\".*",path)) {
				path = result.group(1)+"\\bin\\maxima.bat";
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			f = new File(findWindows("maxima.bat"));
			if (f.exists()) return f.getAbsolutePath();
			if (install) {
				installWindows("maxima.*");
				return findMaxima(false);
			}
		}
		return "";
	}
	
	/**
	 * Sucht nach Inkscape und installiert es bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findInkscape(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("inkscape",install);
		if (ServerConfiguration.service.isWindows()) {
			if (true) return "";
			@SuppressWarnings("unused")
			File f = new File(findWindows("inkscape.exe"));
			if (f.exists()) return f.getAbsolutePath();
			String path = readHKLM("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\inkscape.exe","");
			if (path.length()>1) {
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			path = readHKLM("SOFTWARE\\Classes\\svgfile\\shell\\Inkscape\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			if (install) {
				installWindows("inkscape.*");
				return findInkscape(false);
			}
		}
		return "";
	}
	
	/**
	 * Sucht nach einem PDF Viewer und installiert ihn bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findPDFViewer(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("evince",install);
		if (ServerConfiguration.service.isWindows()) { 
			File f = new File(findWindows("AcroRd32.exe"));
			if (f.exists()) return f.getAbsolutePath();
			f = new File(findWindows("AcroRd.exe"));
			if (f.exists()) return f.getAbsolutePath();	
			f = new File(findWindows("foxit reader.exe"));
			if (f.exists()) return f.getAbsolutePath();			
			String path = readHKLM("SOFTWARE\\Classes\\Applications\\Foxit Reader.exe\\shell\\open\\command","").toUpperCase();
			if (match(".*\"([^\"]+READER.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			path = readHKLM("SOFTWARE\\Classes\\FoxitReader.Document\\shell\\open\\command","").toUpperCase();
			if (match(".*\"([^\"]+READER.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			path = readHKLM("SOFTWARE\\Classes\\acrobat\\shell\\open\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
			if (install) {
				installWindows("FoxitReader.*");
				return findPDFViewer(false);
			}
		}
		return "";
	}
	
	/**
	 * Sucht nach einem Bitmap-Editor und installiert ihn bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findBitmapViewer(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("gimp",install);
		if (ServerConfiguration.service.isWindows()) {
			File f = new File(findWindows("gimp-2.8.exe"));
			if (f.exists()) return f.getAbsolutePath();
			String path=getSystemRoot()+"\\system32\\mspaint.exe";
			f = new File(path);
			if (f.exists()) {
				return f.getAbsolutePath();
			}		
			path = readHKLM("SOFTWARE\\Classes\\icofile\\shell\\Mit GIMP öffnen\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
			if (install) {
				installWindows("gimp.*");
				return findBitmapViewer(false);
			}
		}		
		return "";
	}
	
	/**
	 * Sucht nach TeX und installiert es bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findTex(boolean install) {
		if (ServerConfiguration.service.isLinux()) {
			String s = Cmd.systemcall("which pdflatex").replace("\n","");
			if (s.length()>0) {  // Cmd wurde gefunden
				return s;
			} else {
				if (install) {
					Cmd.systemcall("gksudo apt-get install -y auctex tex-common texinfo texlive-latex-extra texlive-full texlive-plain-extra texmacs ttm ");
					s = Cmd.systemcall("which pdflatex").replace("\n","");
					return s;
				}
			}
		}
		if (ServerConfiguration.service.isWindows()) {
			File f = new File(findWindows("pdflatex.exe"));
			if (f.exists()) return f.getAbsolutePath();
			String path = getPath();  // PFad durchsuchen
			if (match(".*\\;([^\\;]+\\\\miktex\\\\bin\\\\).*", path)){
				f = new File(result.group(1)+"pdflatex.exe");
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
			if (install) {
				installWindows("basic-miktex.*");
				return findTex(false);
			}
		}		
		return "";
	}
	
	/**
	 * Sucht nach einem TeX-Editor und installiert ihn bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findTexIDE(boolean install) {
		if (ServerConfiguration.service.isLinux()) {
			String s = Cmd.systemcall("which kile").replace("\n","");
			if (s.length()>0) {
				return s;
			}
		}		
		return findGeany(install);
	}
	
	/**
	 * Sucht nach einem Text-Editor und installiert ihn bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findTextEditor(boolean install) {
		return findGeany(install);
	}
	
	/**
	 * Sucht nach einem Text-Editor und installiert ihn bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findGeany(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("geany",install);
		if (ServerConfiguration.service.isWindows()) {
			File f = new File(findWindows("geany.exe"));
			if (f.exists()) return f.getAbsolutePath();
			f=new File(getProgramPath()+"\\Geany\\bin\\Geany.exe");
			if (f.exists()) {
				return f.getAbsolutePath();
			}			
			if (install) {
				installWindows("geany.*");
				return findGeany(false);
			}
		}		
		return "";
	}
	
	/**
	 * Sucht nach Excel oder Calc und installiert es bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findCalc(boolean install) {
		if (ServerConfiguration.service.isLinux()) {
			String path = findLinux("libreoffice",install);
			if (path.length()>0) path+=" --calc";
			return path;
		}
		if (ServerConfiguration.service.isWindows()) {
			File f = new File(findWindows("excel.exe"));
			if (f.exists()) return f.getAbsolutePath();
			f = new File(findWindows("soffice.exe"));
			if (f.exists()) return f.getAbsolutePath()+" --calc";
			// HKEY_LOCAL_MACHINE\SOFTWARE\Classes\Excel.Addin\shell\Open\command
			String path = readHKLM("SOFTWARE\\Classes\\Excel.Addin\\shell\\Open\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
			// HKEY_LOCAL_MACHINE\SOFTWARE\Classes\Excel.CSV\shell\Open\command
			path = readHKLM("SOFTWARE\\Classes\\Excel.CSV\\shell\\Open\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
			// HKEY_LOCAL_MACHINE\SOFTWARE\Classes\SOFTWARE\LibreOffice\LibreOffice Path
			path = readHKLM("SOFTWARE\\Classes\\SOFTWARE\\LibreOffice\\LibreOffice","Path");
			if (path.length()>1) {
				f = new File(path+"\\program\\soffice.exe");
				if (f.exists()) {
					return f.getAbsolutePath()+" --calc";
				}
			}
		
			if (install) {
				installWindows("LibO.*");
				return findCalc(false);
			}
		}
		return "";
	}
	
	/**
	 * Sucht nach Java und installiert es bei Bedarf
	 * @param install   true wenn installiert werden soll
	 * @return          Pfad zum Programm
	 */
	public static String findJava(boolean install) {
		if (ServerConfiguration.service.isLinux()) return findLinux("java",install);
		if (ServerConfiguration.service.isWindows()) {
			File f;
			String path = readHKLM("SOFTWARE\\Classes\\jarfile\\shell\\open\\command","").toUpperCase();
			if (match(".*\"([^\"]+.EXE)\".*",path)) {
				path = result.group(1);				
				path = path.replace("JAVAW","JAVA");
				f = new File(path);
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}	
			f = new File(findWindows("java.exe"));
			if (f.exists()) return f.getAbsolutePath();
			if (install) {
				installWindows("jre.*exe");
				return findJava(false);
			}
		}
		return "";
	}
}
