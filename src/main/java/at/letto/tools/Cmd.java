package at.letto.tools;

import static java.nio.file.StandardCopyOption.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.*;
import org.apache.batik.transcoder.image.*;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import at.letto.ServerConfiguration;
import at.letto.globalinterfaces.LettoFile;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.xmlgraphics.image.loader.impl.imageio.ImageIOUtil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Funktionen für Dateiverarbeitung und Systemaufrufe
 * @author damboeck/mayt
 *
 */
public class Cmd {

	public static String inkscape="";

	public static void main(String[] a) {
		String str1 = "gimp abcdef";
		String str2 = "gimp abcxcsdef";
		System.out.println( difference(str1, str2) );
		// systemcall(cmd);
	}


	public static String difference(String str1, String str2) {
		if (str1 == null) {
			return str2;
		}
		if (str2 == null) {
			return str1;
		}
		int at = indexOfDifference(str1, str2);
		if (at == -1) {
			return "";
		}
		return str2.substring(at);
	}
	public static int indexOfDifference(String str1, String str2) {
		if (str1 == str2) {
			return -1;
		}
		if (str1 == null || str2 == null) {
			return 0;
		}
		int i;
		for (i = 0; i < str1.length() && i < str2.length(); ++i) {
			if (str1.charAt(i) != str2.charAt(i)) {
				break;
			}
		}
		if (i < str2.length() || i < str1.length()) {
			return i;
		}
		return -1;
	}
	public static boolean isEmpty(String s) {
		if (s == null) return true;
		if (s.trim().length() == 0) return true;
		return false;
	}


	/**
	 * Führt einen Befehl in der Commandline aus<br>
	 * Nach timeout bricht die Ausführung mit einer TimeoutException ab
	 * @param commandLine     Befehl
	 * @param timeout         Maximale Laufzeit in Millisekunden
	 * @return Exitcode
	 * @throws IOException      Fehler
	 * @throws InterruptedException  Fehler
	 * @throws TimeoutException Fehler
	 */
	public static int executeCommandLine(final String commandLine,
										 final long timeout)
			throws IOException, InterruptedException, TimeoutException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandLine);
		// IO Streams definieren!
		BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error =new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((reader.readLine())!=null) {

		}
		while ((error.readLine())!=null) {

		}
		// Worker für den Prozess definieren
		Worker worker = new Worker(process);
		worker.start();
		try {
			worker.join(timeout);
			if (worker.exit != null)
				return worker.exit;
			else
				throw new TimeoutException();
		} catch(InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally {
			process.destroy();
		}
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;
		private Worker(Process process) {
			this.process = process;
		}
		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	/**
	 * Führt das Kommando cmd im Betriebssystem aus,
	 * @param cmd     Kommando
	 * @param timeout Zeit in ms nachdem das Programm sicher beendet wird!
	 * @return        stdout des Programmes
	 */
	public static String systemcall(String cmd,int timeout) {
		try {
			executeCommandLine(cmd,10000L);
		} catch (IOException | InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Führt mehrere Kommandos als bash-Script aus
	 * @param cmd   Ein oder mehrere Kommandos welche durch Zeilenvorschub getrennt vorliegen
	 * @param path  Verzeichnis in dem das Kommando als Bash-Script gespeichert und ausgeführt wird
	 * @return      Ausgabe von Standard-Output-Kanal
	 */
	public static String systemcallbatch(String cmd, String path) {
		File f=null;
		try {
			cmd = cmd.trim();
			path = path.trim();
			if (ServerConfiguration.service.isLinux()) {
				cmd = cmd.replaceAll("<br\\s*/?>", "\n");
				File pfad = new File(path);
				if (!pfad.exists()) pfad.mkdirs();
				if (!pfad.exists()) pfad.mkdir();
				if (pfad.exists() && pfad.isDirectory()) {
					if (!path.endsWith("/")) path = path + "/";
					String script = path + "callscript.sh";
					f = new File(script);
					int i=0;
					while (f.exists()) {
						i++;
						script = path + "callscript"+i+".sh";
						f = new File(script);
						if (i>1000) throw new RuntimeException("Zu viele callscripts sind vorhanden!!");
					}
					FileWriter fw = new FileWriter(f);
					fw.write("#!/bin/bash\n");
					fw.write("cd "+path.replaceAll("\\\\","/")+"\n");
					fw.write("# Script wurde automatisch erstellt von Letto!\n");
					fw.write(cmd);
					fw.close();
					f.setExecutable(true);
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command(f.getAbsolutePath());
					processBuilder.directory(pfad);
					Process process = processBuilder.start();
					InputStream stdout = process.getInputStream();
					InputStream stderr = process.getErrorStream();
					process.waitFor();
					byte stdoutContent[] = new byte[stdout.available()];
					int stdoutBytes = stdout.read(stdoutContent);
					byte stderrContent[] = new byte[stderr.available()];
					int stderrBytes = stdout.read(stderrContent);
					process.destroyForcibly();
					String ret = Charset.forName("UTF-8").newDecoder().decode(ByteBuffer.wrap(stdoutContent)).toString();
					f.delete();
					return ret;
				} else return "Pfad " + path + " existiert nicht! Kommando konnte nicht ausgeführt werden!";
			} else {
				cmd = cmd.replaceAll("<br\\s*/?>", "\n");
				File pfad = new File(path);
				if (pfad.exists() && pfad.isDirectory()) {
					if (!(path.endsWith("/") || path.endsWith("\\\\"))) path = path + "/";
					String script = path + "callscript.bat";
					f = new File(script);
					int i=0;
					while (f.exists()) {
						i++;
						script = path + "callscript"+i+".bat";
						f = new File(script);
						if (i>1000) throw new RuntimeException("Zu viele callscripts sind vorhanden!!");
					}
					FileWriter fw = new FileWriter(f);
					fw.write("@echo off\nchcp 1252 >null\ndel null /q /f\ncd "+path.replaceAll("/","\\\\")+"\nrem Script wurde automatisch erstellt von Letto!\n");
					fw.write(cmd);
					fw.close();
					f.setExecutable(true);
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command(f.getAbsolutePath());
					processBuilder.directory(pfad);
					Process process = processBuilder.start();
					InputStream stdout = process.getInputStream();
					InputStream stderr = process.getErrorStream();
					process.waitFor();
					byte stdoutContent[] = new byte[stdout.available()];
					int stdoutBytes = stdout.read(stdoutContent);
					byte stderrContent[] = new byte[stderr.available()];
					int stderrBytes = stdout.read(stderrContent);
					process.destroyForcibly();
					String ret = Charset.forName("windows-1252").newDecoder().decode(ByteBuffer.wrap(stdoutContent)).toString();
					f.delete();
					return ret;
				} else return "Pfad " + path + " existiert nicht! Kommando konnte nicht ausgeführt werden!";
			}
		} catch (Exception exception) { }
		try {
			if (f!=null && f.exists()) f.delete();
		} catch (Exception exception) {}
		return "Kommando konnte nicht ausgeführt werden!";
	}

	/**
	 * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
	 * es wieder beendet wird!
	 * @param cmd     Kommando
	 * @return        stdout des Programmes
	 */
	public static String systemcall(String cmd) {
		String charset = Charset.defaultCharset().toString();
		return systemcall(cmd, charset);
	}


	/**
	 * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
	 * es wieder beendet wird!
	 * @param cmd     Kommando
	 * @param charset Character-Set
	 * @return        stdout des Programmes
	 */
	public static String systemcall(String cmd, String charset) {
		String ret="";
		Process p=null;
		try {
			String acmd = "";
			String h    = cmd;
			int    mode=0;      // 0 normal 1..innerhalb von Hochkomma
			int    f;

			/*
			 * Zerlegen des Kommandos in die Parameterliste und Entfernen der Doppelhochkomma
			 */
			Vector<String> cv = new Vector<String>();
			do {
				// Durchuche den String nach dem nächsten Vorkommen von " oder blank
				int pos      = h.length();
				char c       = 'a';
				f = h.indexOf("\""); if ((f>-1) && (f<pos)) { pos=f;c='"'; }
				f = h.indexOf(" "); if ((f>-1) && (f<pos)) { pos=f;c=' '; }
				if (c!='a') {
					acmd += h.substring(0,pos);
					h = h.substring(pos);
				}
				if (c=='"') {
					if (mode==1) mode=0;
					else         mode=1;
					h = h.substring(1);
				} else if (c==' ') {
					if (mode==1) {
						acmd += " ";
					} else {
						if (acmd.length()>0) cv.add(acmd);
						acmd="";
					}
					h = h.substring(1);
				}  else {
					acmd += h;
					h="";
				}
			} while (h.length()>0);
			if (acmd.length()>0) cv.add(acmd);
			String cmdlst[] = new String[cv.size()];
			for (int j=0;j<cv.size();j++) cmdlst[j] = cv.get(j);
			/**
			 * Starten den Prozesses
			 */
			p = new ProcessBuilder(cmdlst).start();
			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream(),charset));
			BufferedReader error =new BufferedReader(new InputStreamReader(p.getErrorStream(),charset));

			StringBuilder sbstdout = new StringBuilder();
			StringBuilder sberr    = new StringBuilder();
			String line;
			while ((line=reader.readLine())!=null) {
				sbstdout.append(line+"\n");
			}
			while ((line=error.readLine())!=null) {
				sberr.append(line+"\n");
			}
			p.waitFor();
			p.destroyForcibly();
			String err = sberr.toString();
			ret = sbstdout.toString();
			if (err.length()>0)
				ret += err;
		} catch(IOException e1) {
			return (ret.length()>0?"\n":"")+"Error: "+cmd+" kann nicht gestartet werden!";
		} catch(InterruptedException e2) {
			return (ret.length()>0?"\n":"")+"Error: "+cmd+" wurde unerwartet unterbrochen!";
		}
		return ret;
	}

	/**
	 * Führt das Kommando cmd im Betriebssystem aus, und wartet bis
	 * es wieder beendet wird!
	 * @param cmdlst  String[]     Kommandoliste von Befehl und Parametern
	 * @return        stdout des Programmes
	 */
	public static String systemcall(String cmdlst[]) {
		String ret="";
		Process p=null;
		try {
			/**
			 * Starten den Prozesses
			 */
			p = new ProcessBuilder(cmdlst).start();    // funktioniert unter Linux

			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error =new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String line;
			while ((line=reader.readLine())!=null) {
				ret += line+"\n";
			}
			while ((line=error.readLine())!=null) {

			}
			p.waitFor();
		} catch(IOException e1) {} catch(InterruptedException e2) {
			System.out.println("cmd-IO-Fehler");
		}
		return ret;
	}

	/**
	 * Führt das Kommando cmd in einem parallel Thread im Betriebssystem aus,
	 * und wartet nicht bis das Commando fertig ist!
	 * @param cmd     Kommando
	 * @return        stdout des Programmes
	 */
	public static String threadcall(String cmd) {
		String ret="";
		try {
			//Vector<String> v = new Vector<String>();
			//String []x = cmd.split("\"");
			String args[] = cmd.split(" ");

			Runtime.getRuntime().exec(args);

			//Runtime.getRuntime().exec(cmd);
		} catch(IOException e1) {}
		return ret;
	}

	public static String threadcall(String[] cmd) {
		String ret="";
		try {
			Runtime.getRuntime().exec(cmd);
		} catch(IOException e1) {}
		return ret;

	}


	/**
	 * Erzeugt eine leere Datei und alle Ordner im vollständigen Dateipfad
	 * @param DateiName   Name der zu erstellenden Datei als vollständiger Pfad
	 * @return true wenn erfolgreich
	 */
	public static boolean GenerateFile(String DateiName) {
		return GenerateFile(DateiName, null);
	}

	/**
	 * Erzeugt eine mit Daten (outBytes) gefüllte Datei und alle Ordner im Dateipfad
	 * @param DateiName  	Name der zu erstellenden Datei als vollständiger Pfad
	 * @param outBytes		Daten, die in Datei ausgegeben werden
	 * @return true        wenn erfolgreich
	 */
	public static boolean GenerateFile(String DateiName, byte[] outBytes) {
		File f = new File(DateiName);

		try {
			File ImageDir = new File(f.getParent());
			ImageDir.mkdirs();
		} catch (Exception ex) {}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(DateiName);
			if (outBytes!=null) fos.write(outBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Wandelt ein Image in einen Base64-Encoded String um
	 * dieser kann dann in MoodleXML gespeichert werden
	 * @param DateiName : Datei, die eingelesen werden soll
	 * @return : UEncoded String mit gewähltem Bildinhalt
	 */
	public static String LoadImage(String DateiName) {
		String ImgString = null;

		File f = new File(DateiName);
		if (f.exists() == false) return ImgString;

		FileInputStream fis;
		try {
			fis = new FileInputStream(DateiName);
			byte fileContent[] = new byte[(int)f.length()];
			fis.read(fileContent);

			byte[] ret = Base64.getEncoder().encode(fileContent);
			ImgString = new String(ret);
			fis.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return ImgString;
	}

	/**
	 * Lädt ein Image in ein byte-Array
	 * @param DateiName : Datei, die eingelesen werden soll
	 * @return : byte-Array mit Dateiinhalt
	 */
	public static byte[] LoadImageByte(String DateiName) {
		File f = new File(DateiName);
		if (f.exists() == false) return null;

		FileInputStream fis;
		try {
			fis = new FileInputStream(DateiName);
			byte fileContent[] = new byte[(int)f.length()];
			fis.read(fileContent);
			fis.close();
			return fileContent;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}


	public static BufferedImage resizeImage(BufferedImage originalImage, int type, Dimension d){
		int IMG_WIDTH;
		int IMG_HEIGHT;

		float yscale = (float)d.height/(float)originalImage.getHeight();
		float xscale= (float)d.width/(float)originalImage.getWidth();

		if (xscale < yscale) {
			IMG_WIDTH = d.width;
			IMG_HEIGHT = (int) (xscale*originalImage.getHeight());
		}
		else {
			IMG_WIDTH = (int) (yscale*originalImage.getWidth());
			IMG_HEIGHT = d.height;

		}

		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Speichert ein Image (byte-Array) an die angegebene Stelle
	 * @param file Image-Daten als yyte-Array
	 * @param StorePath Dateiname als vollständiger Dateipfad
	 */
	public static void SaveImage(byte[] file, String StorePath) {
		try {
			//TODO Werner: Base64 kann von HTML-Dateien nicht geladen werden!!!!
			GenerateFile(StorePath, file);
		} catch (IllegalArgumentException e) {
			System.out.println("Datei "+StorePath+" konnte nicht geschrieben werden, da Base64-kodiertes Bild eine falsche Endung hat!");
		}
	}

	/**
	 * Speichert ein UEncoded-Image an die angegebene Stelle
	 * @param UEncodedFile Image-Daten
	 * @param StorePath Dateiname als vollständiger Dateipfad
	 */
	public static void SaveImage(String UEncodedFile, String StorePath) {
		byte image[];
		try {
			image = Base64.getMimeDecoder().decode(UEncodedFile.getBytes());
			GenerateFile(StorePath, image);
		} catch (IllegalArgumentException e) {
			System.out.println("Datei "+StorePath+" konnte nicht geschrieben werden, da Base64-kodiertes Bild eine falsche Endung hat!");
		}
	}

	/**
	 * Liest die angegebene Datei ein und gibt sie als Zeichenkette zurück
	 * @param filename    Dateiname
	 * @return            Dateiinhalt als String
	 */
	public static Vector<String> readfile(String filename) {
		try {
			return FileMethods.readFileInList(filename);
		} catch (Exception e) {
			System.out.println("Die Datei "+filename+" konnte nicht gelesen werden!");
		}
		return new Vector<String>();
//		Vector<String> ret = new Vector<String>();
//		String s;
//		BufferedReader datei;
//		try {
//			datei = new BufferedReader(new FileReader(filename));
//            while ((s = datei.readLine()) != null) {
//            	ret.add(s+"\n");
//            }
//            datei.close();
//		} catch (IOException e)
//        { System.out.println("Die Datei "+filename+" konnte nicht gelesen werden!");}
//		// finally { try { datei.close(); } catch (Exception e) {}; }
//		return ret;
	}

	/**
	 * Liest die angegebene Datei ein und gibt sie als Zeichenkette zurück<br>
	 * Jede Zeile wird als ein Vektorelement eingelesen und am Zeilenende wird der Zeilenvorschub entfernt!!<br>
	 * Soll der gelesene Vektor wieder korrekt in die Datei geschrieben werden, so muss writelnfile(data,file) verwendet werden!!
	 * @param file        Datei
	 * @return            Dateiinhalt als String
	 */
	public static Vector<String> readfile(File file) {
		try {
			return FileMethods.readFileInList(file);
		} catch (Exception e) {
			System.out.println("Die Datei "+file.getAbsolutePath()+" konnte nicht gelesen werden!");
		}
		return new Vector<String>();
	}

	/**
	 * Liest die angegebene Datei ein und gibt sie als Zeichenkette zurück<br>
	 * Jede Zeile wird als ein Vektorelement eingelesen und am Zeilenende wird der Zeilenvorschub entfernt!!<br>
	 * Soll der gelesene Vektor wieder korrekt in die Datei geschrieben werden, so muss writelnfile(data,file) verwendet werden!!
	 * @param file        Datei
	 * @return            Dateiinhalt als String
	 */
	public static String readfileInString(File file) {
		Vector<String> data = readfile(file);
		StringBuilder sb = new StringBuilder();
		for (String s:data) sb.append(s);
		return sb.toString();
	}

	/**
	 * Liest die angegebene Datei ein und gibt sie als Zeichenkette zurück
	 * @param datei       Dateistream von dem gelesen werden soll
	 * @return            Dateiinhalt als Vector von String
	 */
	@Deprecated
	public static Vector<String> readFile(BufferedReader datei) {
		// FIXME Werner: BufferedReaders enthalten beriets die Encodierung, können also nicht
		// in der Encodierung geändert werden. => Verwendung der obigen Methoden
		Vector<String> ret = new Vector<String>();
		String s;
		try {
			while ((s = datei.readLine()) != null) {
				ret.add(s+"\n");
			}
			datei.close();
		} catch (IOException e)
		{ System.out.println("Die Datei konnte nicht gelesen werden!");}
		return ret;
	}


	/**
	 * Erzeugt die Datei neu und schreibt den Inhalt in die angegebene Datei
	 * @param inhalt        String für den Inhalt der Datei
	 * @param filename      Dateiname
	 * @return              True wenn der Schreibvorgang erfolgreich war
	 */
	public static boolean writefile(String inhalt, String filename) {
		Vector<String> v = new Vector<String>();
		v.add(inhalt);
		return writefile(v,filename);
	}

	/**
	 * Erzeugt die Datei neu und schreibt den Inhalt in die angegebene Datei
	 * @param inhalt        String für den Inhalt der Datei
	 * @param file          Datei
	 * @return              True wenn der Schreibvorgang erfolgreich war
	 */
	public static boolean writefile(String inhalt, File file) {
		Vector<String> v = new Vector<String>();
		v.add(inhalt);
		return writefile(v,file);
	}

	/**
	 * Erzeugt die Datei neu und schreibt den Inhalt in die angegebene Datei
	 * @param inhalt        Vector&lt;String&gt; für den Inhalt der Datei
	 * @param filename      Dateiname
	 * @return              True wenn der Schreibvorgang erfolgreich war
	 */
	public static boolean writefile(Vector<String> inhalt, String filename) {
		if (!GenerateFile(filename)) return false;
		BufferedWriter out=null;
		try
		{	out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename), "UTF-8"));
			for (String s:inhalt) out.write(s);
			out.close();
		} catch (IOException e)
		{   System.out.println("Die Datei konnte nicht geschrieben werden!");
			return false;
		}
		return true;
	}

	/**
	 * Erzeugt die Datei neu und schreibt den Inhalt in die angegebene Datei
	 * @param inhalt        Vector&lt;String&gt; für den Inhalt der Datei
	 * @param file          Datei
	 * @return              True wenn der Schreibvorgang erfolgreich war
	 */
	public static boolean writefile(Vector<String> inhalt, File file) {
		if (!GenerateFile(file.getAbsolutePath())) return false;
		BufferedWriter out=null;
		try
		{	out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
			for (String s:inhalt) out.write(s);
			out.close();
		} catch (IOException e)
		{   System.out.println("Die Datei konnte nicht geschrieben werden!");
			return false;
		}
		return true;
	}

	/**
	 * Erzeugt die Datei neu und schreibt den Inhalt in die angegebene Datei<br>
	 * An jedes Vektorelement wird am Ende ein Zeilenvorschub angehängt
	 * @param inhalt        Vector&lt;String&gt; für den Inhalt der Datei
	 * @param file          Datei
	 * @return              True wenn der Schreibvorgang erfolgreich war
	 */
	public static boolean writelnfile(Vector<String> inhalt, File file) {
		if (!GenerateFile(file.getAbsolutePath())) return false;
		BufferedWriter out=null;
		try
		{	out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
			for (String s:inhalt) out.write(s+"\n");
			out.close();
		} catch (IOException e)
		{   System.out.println("Die Datei konnte nicht geschrieben werden!");
			return false;
		}
		return true;
	}


	/**
	 * konvertiert eine Standard Moodle XML Datei in eine gültige XML-Datei
	 * @param source   Quelldatei
	 * @param dest     Zieldatei
	 * @return         true wenn die Konvertierung erfolgreich war
	 */
	public static boolean convertMoodleToXML(String source, String dest) {
		String s;
		BufferedReader datei;
		try {
			datei = new BufferedReader(new FileReader(source));
			FileWriter fw = new FileWriter(dest, false);
			while ((s = datei.readLine()) != null) {
				s = RegExp.replaceAll("<text>[^<]*(\\&)[^alg][^mt][^\\;p][^;]",s,"&amp;");
				fw.write(s);
			}
			datei.close();
			fw.close();
		} catch (IOException e)
		{ 	System.out.println("Die Datei "+dest+" konnte nicht gelesen werden!");
			return false;
		}
		return true;
	}

	/**
	 * konvertiert die angegebene Datei vom Moodle-XML-Format in eine gültige XML-Datei
	 * @param filename Dateiname
	 * @return         true wenn die Konvertierung erfolgreich war
	 */
	public static boolean convertMoodleToXML(String filename) {
		return convertMoodleToXML(filename,filename);
	}

	/**
	 * Ermittelt die Extention eines Files
	 * @param file : Datei, von dem der Dateityp ermittelt werden soll
	 * @return Ermittelt die Extention eines Files ohne des Punktes
	 */
	public static String getFileTyp(File file) {
		return getFileTyp(file.getAbsolutePath());
	}

	/**
	 * Ermittelt die Extention eines Files vom Dateinamen
	 * @param fileName : Datei, von dem der Dateityp ermittelt werden soll
	 * @return ermittelte File-Extention eines Files ohne des Punktes
	 */
	public static String getFileTyp(String fileName) {
		String ext = "";
		// Extention ermitteln
		Matcher m = Pattern.compile(".*\\.([^\\.]+)$").matcher(fileName);
		if (m.find()) {
			ext = m.toMatchResult().group(1).toLowerCase();
		}
		return ext;
	}

	private static Pattern P_PfadFileExt = Pattern.compile("^(?<pfad>.*[\\\\\\/])(?<name>(?<noe>[^\\\\\\/]+)(?<ext>\\.[^\\.]+))$");
	private static Pattern P_FileExt     = Pattern.compile("^(?<name>(?<noe>[^\\\\\\/]+)(?<ext>\\.[^\\.]+))$");
	private static Pattern P_PfadFile    = Pattern.compile("^(?<pfad>.*[\\\\\\/])(?<name>[^\\\\\\/]+)$");

	/**
	 * Liefert den Pfad einer Datei
	 * @param file   Datei die untersucht werden soll
	 * @return       Pfad der Datei in der Form C:/opt/ mit normalem Schrägstrich als Trennzeichen inklusive einem vorhandenen abschließenden Schrägstrich
	 */
	public static String getFileDirectory(File file) {
		String pfad = file.getAbsolutePath();
		Matcher m;
		// Datei c:/opt/abc.img
		if ((m = P_PfadFileExt.matcher(pfad)).find()) return m.group("pfad").replaceAll("\\\\","\\/");
		// Datei abc.img
		if ((m = P_FileExt.matcher(pfad)).find())     return m.group("pfad").replaceAll("\\\\","\\/");
		// Datei c:/opt/abc
		if ((m = P_PfadFile.matcher(pfad)).find())    return "";
		// Datei abc
		return "";
	}

	/**
	 * Liefert den Dateinamen einer Datei
	 * @param file   Datei die untersucht werden soll
	 * @return       Dateiname ohne Pfad
	 */
	public static String getFileName(File file) {
		String pfad = file.getAbsolutePath();
		Matcher m;
		// Datei c:/opt/abc.img
		if ((m = P_PfadFileExt.matcher(pfad)).find()) return m.group("name");
		// Datei abc.img
		if ((m = P_FileExt.matcher(pfad)).find())     return m.group("name");
		// Datei c:/opt/abc
		if ((m = P_PfadFile.matcher(pfad)).find())    return m.group("name");
		// Datei abc
		return pfad;
	}

	/**
	 * Liefert den Dateinamen einer Datei
	 * @param file   Datei die untersucht werden soll zB.: c:/opt/abc.img
	 * @return       Dateiname ohne Pfad und ohne Extension - abc
	 */
	public static String getFileNameWithoutExtension(File file) {
		String pfad = file.getAbsolutePath();
		Matcher m;
		// Datei c:/opt/abc.img
		if ((m = P_PfadFileExt.matcher(pfad)).find()) return m.group("noe");
		// Datei abc.img
		if ((m = P_FileExt.matcher(pfad)).find())     return m.group("noe");
		// Datei c:/opt/abc
		if ((m = P_PfadFile.matcher(pfad)).find())    return m.group("name");
		// Datei abc
		return pfad;
	}

	/**
	 * Liefert die Extension einer Datei
	 * @param file   Datei die untersucht werden soll zB.: c:/opt/abc.img
	 * @return       Extension mit Punkt - .img
	 */
	public static String getFileExtension(File file) {
		String pfad = file.getAbsolutePath();
		Matcher m;
		// Datei c:/opt/abc.img
		if ((m = P_PfadFileExt.matcher(pfad)).find()) return m.group("ext");
		// Datei abc.img
		if ((m = P_FileExt.matcher(pfad)).find())     return m.group("ext");
		// Datei c:/opt/abc
		if ((m = P_PfadFile.matcher(pfad)).find())    return "";
		// Datei abc
		return "";
	}

	/**
	 * Prüft, ob der Dateityp von file in der übergebenen Liste vorkommt
	 * @param file : Datei, von dem der Dateityp verglichen werden soll
	 * @param types : Liste der Typen
	 * @return : true, wenn Dateityp passt
	 */
	public static boolean checkFileTyp(File file, String[] types) {

		String ext = "";
		// Extention ermitteln
		Matcher m = Pattern.compile(".*\\.([^\\.]+)$").matcher(file.getName());
		if (m.find()) {
			ext = m.toMatchResult().group(1).toLowerCase();
		}

		// Überprüfung, ob Extention in der Liste enthalten ist
		Vector <String> fileTypes = new Vector <String>(Arrays.asList(types));
		if ( fileTypes.contains(ext) ) return true;

		return false;
	}
	/**
	 * Konvertiert eine Bilddatei
	 * @param cmd
	 * @param src
	 * @param dst
	 */
/*	private static String convertImg(String cmd, String src, String dst) {
		File f = new File(dst);
		dst = f.getAbsolutePath();
		dst = dst.replace("/./","/");
		dst = dst.replace("\\.\\","\\");
		cmd = cmd.replace("%s","\""+src+"\"");
		cmd = cmd.replace("%d","\""+dst+"\"");
		//System.out.println(cmd);
		Cmd.systemcall(cmd);
		return dst;
	} */

	/**
	 * Speichert das Bild im String image in eine temporäre Datei
	 * jpg,pdf oder png werden in eine Datei gespeichert
	 * svg und png wird in eine pdf-Datei konvertiert!
	 * andere Datentypen bleiben erhalten.
	 * @param image    kodierte Datei
	 * @param imgVerz  Verzeichnis wo die Datei gespeichert werden soll
	 * @param nr      Nummer der Datei
	 * @param mode    0..tex 1..gui
	 * @return        [0] absoluter Dateipfad wo die Datei gespeichert wurde
	 *                [1] absoluter Dateipfad der konvertierten Datei
	 */
	public static String[] saveTmpImg(LettoFile image,String imgVerz,int nr,int mode){
		String ext="";
		String ret[] = new String[2];
		// Extension bestimmen
		Matcher m = Pattern.compile(".*(\\.[^\\.]+)$").matcher(image.getFilename());
		if (m.find()) {
			ext = m.toMatchResult().group(1).toLowerCase();
		}
		String fn = "img"+nr;
		// if (nr==-1) fn="Vorschau";
		String src = imgVerz+"/"+fn+ext;
		File f = new File(src);
		src = f.getAbsolutePath();
		src = src.replace("/./","/");
		src = src.replace("\\.\\","\\");
		Cmd.SaveImage(image.getInhalt(), src);
		//System.out.println("src:"+src);
		ret[0] = src;
		ret[1] = src;
		if (ext.toLowerCase().equals(".svg")) {
			ret[1] = (new File(svgtojpg(src,imgVerz+"/"+fn+".jpg"))).getAbsolutePath();
		} else if (ext.toLowerCase().equals(".png"))  {
			if (mode == 1) ret[1] =  (new File(pngtojpg(src,imgVerz+"/"+fn+".jpg"))).getAbsolutePath();
		} else if (ext.toLowerCase().equals(".pdf"))  {
			if (mode == 1) ret[1] =  (new File(pdftojpg(1,src,imgVerz+"/"+fn+".jpg"))).getAbsolutePath();
		}
		return ret;
	}

	/**
	 * Speichert das UEncoded-Image im String image in eine temporäre Datei als jpg-Datei.
	 * @param UEncodedFile Image-Daten
	 * @param imgVerz  Verzeichnis wo die Datei gespeichert werden soll
	 * @param nr      Nummer der Datei
	 * @param mode    0..tex 1..gui
	 * @param extension File-Extension
	 * @return        absoluter Dateipfad wo die Datei gespeichert wurde
	 */
	public static String saveTmpImgJpg(String UEncodedFile,String imgVerz,int nr,int mode, String extension){
		String fn = "img"+nr;
		// Extension setzen
		if (extension==null) extension=".jpg";
		extension = extension.trim();
		if (extension.length()==0) extension=".jpg";
		if (!extension.startsWith(".")) extension = "."+extension;
		if (extension.length()<2) extension=".jpg";
		extension = extension.toLowerCase();
		// Datei speichern
		String src = imgVerz+"/"+fn+extension;
		File f = new File(src);
		src = f.getAbsolutePath();
		src = src.replace("/./","/");
		src = src.replace("\\.\\","\\");
		Cmd.SaveImage(UEncodedFile, src);
		return src;
	}

	/**
	 * Speichert alle Dateien des Vektors "files" in eine Zip-komprimierte Datei
	 * @param files    Dateien die gespeichert werden
	 * @param zipfile  Ziel-zip-Datei
	 * @return         Handle auf die Ziel-Zip-Datei oder null wenn etwas nicht funktioniert hat
	 */
	public File saveAsZip(Vector<File> files, File zipfile) {
		ZipFileWriter zf = null;
		try {
			zf = new ZipFileWriter(zipfile);
			for (File f:files)
				zf.addFile(f);
			zf.close();
			return zipfile;
		} catch (IOException e) {
			if (zf!=null) zf.close();
		}
		return null;
	}

	/**
	 * Wandelt eine svg-Datei in eine jpg-Datei um
	 * @param src Quelldatei
	 * @return    Name der Zieldatei
	 */
	public static String svgtojpg(String src) {
		String dst = src;
		if (dst.matches(".*\\.[sS][vV][gG]")) dst = dst.substring(0,src.length()-4);
		return svgtojpg(src,dst+".jpg");
	}
	/**
	 * Wandelt eine svg-Datei in eine jpg-Datei um
	 * @param src Quelldatei
	 * @param dst Zieldatei
	 * @return    Name der Zieldatei
	 */
	@SuppressWarnings("deprecation")
	public static String svgtojpg(String src, String dst) {
		// Create a JPEG transcoder
		JPEGTranscoder t = new JPEGTranscoder();

		// Set the transcoding hints.
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, Float.valueOf(.8f));
		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, Float.valueOf(600));

		String svgURI;
		try {
			svgURI = new File(src).toURL().toString();
			//svgURI=src;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			System.out.println("URL konnte nicht erstellt werden!");
			return "";
		}

		// Create the transcoder input.
		TranscoderInput input = new TranscoderInput(svgURI);

		// Create the transcoder output.
		OutputStream ostream;
		try {
			ostream = new FileOutputStream(dst);
		} catch (FileNotFoundException e) {
			System.out.println("Datei nicht gefunden!");
			return "";
		}
		TranscoderOutput output = new TranscoderOutput(ostream);

		// Save the image.
		try {
			t.transcode(input, output);
		} catch (TranscoderException e) {
			e.printStackTrace();
			System.out.println("Transcodefehler");
			return "";
		}

		// Flush and close the stream.
		try {
			ostream.flush();
			ostream.close();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("File-write-Fehler");
			return "";
		}

		return dst;
	}

	/**
	 * Wandelt eine png-Datei in eine jpg-Datei um
	 * @param src Quelldatei
	 * @return    Name der Zieldatei
	 */
	public static String pngtojpg(String src) {
		String dst = src;
		if (dst.matches(".*\\.[pP][nN][gG]")) dst = dst.substring(0,src.length()-4);
		return pngtojpg(src,dst+".jpg");
	}
	/**
	 * Wandelt eine png-Datei in eine jpg-Datei um
	 * @param src Quelldatei
	 * @param dst Zieldatei
	 * @return    Name der Zieldatei
	 */
	public static String pngtojpg(String src, String dst) {
		BufferedImage bufferedImage;

		try {
			//read image file
			bufferedImage = ImageIO.read(new File(src));

			// create a blank, RGB, same width and height, and a white background
			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
					bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", new File(dst));

			return dst;

		} catch (IOException e) {

			e.printStackTrace();

		}
		return "";
	}

	/**
	 * Wandelt die erste Seite einer pdf-Datei in eine jpg-Datei um
	 * @param src Quelldatei
	 * @return    Name der Zieldatei
	 */
	public static String pdftojpg(String src) {
		String dst = src;
		if (dst.matches(".*\\.[pP][dD][fF]")) dst = dst.substring(0,src.length()-4);
		return pdftojpg(1,src,dst+".jpg");
	}
	/**
	 * Wandelt eine Seite einer PDF-Datei in eine jpg-Datei um
	 * @param pagenr Seitennummer, wenn größer als Seitenanzahl wird die letzte Seite konvertiert
	 * @param src Quelldatei
	 * @param dst Zieldatei
	 * @return    Name der Zieldatei
	 */
	public static String pdftojpg(int pagenr, String src, String dst) {
		try {
			PDDocument document = PDDocument.load(new File(src));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int pageCounter = 0;
			for (PDPage page : document.getPages())
			{
				if (pageCounter == pagenr) {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
					// suffix in filename will be used as the file format
					ImageIO.write(bim, "jpg", new File(dst));
				}
				pageCounter++;
			}
			document.close();

//			ALTE VERSION von 2.0
//			PDDocument document = PDDocument.load(new File(src));
//			if (pagenr > document.getNumberOfPages()) pagenr = document.getNumberOfPages();
//			List<PDPage> pages = new ArrayList<>();
//			document.getDocumentCatalog().getPages().forEach(pages::add);
//			page = pages.get(pagenr-1);
//
//			BufferedImage buffImage =    page.convertToImage();
//			ImageIO.write(buffImage, "jpg", new File(dst));
			return dst;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * Wandelt ein base64-kodiertes PDF in eine svg-Datei um
	 * Zum Wandeln wird inkscape verwendet!! Falls Inkscape nicht gefunden werden kann wird null retourniert!
	 * @param base64    base64 kodiertes PDF
	 * @return          base64 kodiertes SVG
	 */
	public static String convertPDFtoSVG(String base64) {
		if (inkscape.length()<2);
		inkscape = Install.findInkscape(true);
		if (inkscape.length()<2) return null;
		String p = ServerConfiguration.service.Get("TempPath");
		if (p==null) p=".";
		if (p.length()<1) p=".";
		File fpdf = new File(p+"/bild.pdf");
		File fsvg = new File(p+"/bild.svg");
		// Konvertiere mit Inkscape wenn inkscape gefunden wurde
		SaveImage(base64,fpdf.getAbsolutePath());
		if (inkscape.length()>1) {
			if (fsvg.exists())
				fsvg.delete();
			Cmd.systemcall(inkscape+" \""+fpdf.getAbsolutePath()+"\" -l \""+
					fsvg.getAbsolutePath()+"\"");
			if (!fsvg.isFile()) return null;
			return LoadImage(fsvg.getAbsolutePath());
		}
		return "Bild:"+fpdf.getAbsolutePath();
		// Falls die Konvertierung nicht erfolgreich wird null retourniert
		//return null;
	}
	/**
	 * Wandelt ein base64-kodiertes PDF in eine svg-Datei um
	 * Zum Wandeln wird inkscape verwendet!! Falls Inkscape nicht gefunden werden kann wird null retourniert!
	 * @param    file   PDF-Datei
	 * @return          base64 kodiertes SVG
	 */
	public static byte[] convertPDFtoSVG(byte[] file) {
		if (inkscape.length()<2)
			inkscape = Install.findInkscape(true);
		String p = ServerConfiguration.service.Get("TempPath");
		if (p==null) p=".";
		if (p.length()<1) p=".";
		File fpdf = new File(p+"/bild.pdf");
		File fsvg = new File(p+"/bild.svg");
		// Konvertiere mit Inkscape wenn inkscape gefunden wurde
		SaveImage(file, fpdf.getAbsolutePath());
		if (inkscape.length()>1) {
			if (fsvg.exists())
				fsvg.delete();
			Cmd.systemcall(inkscape+" \""+fpdf.getAbsolutePath()+"\" -l \""+
					fsvg.getAbsolutePath()+"\"");
			if (!fsvg.isFile()) return null;
			return LoadImageByte(fsvg.getAbsolutePath());
		}
		return new byte[0];
		// Falls die Konvertierung nicht erfolgreich wird null retourniert
		//return null;
	}

	/**
	 * Wandelt ein base64-kodiertes PDF in eine jpg-Datei um
	 * @param bild   	 base64 kodiertes PDF
	 * @param pagenr    Seitennummer
	 * @return          base64 kodiertes JPG
	 */
	public static String pdftojpgB64(String bild, int pagenr) {
		try {
			// Base64 kodierten String in einem InputStream umwandeln
			InputStream is = new ByteArrayInputStream(bild.getBytes());
			// PDF Dokument aus dem InputStream laden
			PDDocument document = PDDocument.load(is);
			if (pagenr > document.getNumberOfPages()) pagenr = document.getNumberOfPages();

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int pageCounter = 0;
			for (PDPage page : document.getPages()) {
				if (pageCounter == pagenr) {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
					OutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bim, "jpg", os);
					// jpg Base64 encodieren
					byte[] ret = Base64.getEncoder().encode(os.toString().getBytes());
					// Bytearray als String zurückgeben
					document.close();
					return new String(ret);
				}
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

//		PDPage page;
//		if (pagenr<0) pagenr=0;
//		try {
//			// Base64 kodierten String in einem InputStream umwandeln
//			InputStream is = new ByteArrayInputStream(bild.getBytes());
//			// PDF Dokument aus dem InputStream laden
//			PDDocument document = PDDocument.load(is);
//			// Seite des Dokuments laden
//			if (pagenr > document.getNumberOfPages()) pagenr = document.getNumberOfPages();
//			page = (PDPage) document.getDocumentCatalog().getAllPages().get(pagenr-1);
//			// PDF Dokument schließen
//			document.close();
//			// Seite in BufferedImage konvertieren
//			BufferedImage buffImage = page.convertToImage();
//			// BufferedImage in jpg konvertieren
//			OutputStream os = new ByteArrayOutputStream();
//			ImageIO.write(buffImage, "jpg", os);
//			// jpg Base64 encodieren
//			byte[] ret = Base64.getEncoder().encode(os.toString().getBytes());
//			// Bytearray als String zurückgeben
//			return new String(ret);
//		} catch (Exception|Error e) {
//		}
//		return "";
	}
	/**
	 * Wandelt ein base64-kodiertes svg in eine jpg-Datei um
	 * @param bild    base64 kodiertes SVG
	 * @return          base64 kodiertes JPG
	 */
	public static String svgtojpgB64(String bild) {
		try {
			// Bild aus svg laden und Transcodieren
			byte img[] = Base64.getMimeDecoder().decode(bild.getBytes());
			InputStream is  = new ByteArrayInputStream(img);
			JPEGTranscoder t = new JPEGTranscoder();
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, Float.valueOf(.8f));
			t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, Float.valueOf(600));
			// Create the transcoder input.
			TranscoderInput input = new TranscoderInput(is);
			// Create the transcoder output.
			ByteArrayOutputStream ostream ;
			ostream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(ostream);
			// Save the image.
			try {
				t.transcode(input, output);
			} catch (TranscoderException e) {
				e.printStackTrace();
				System.out.println("Transcodefehler");
			}
			// Flush and close the stream.
			is  = new ByteArrayInputStream(ostream.toByteArray());
			ostream.close();
			// Bild in Buffer laden
			BufferedImage image = ImageIO.read(is);
			// BufferedImage in jpg konvertieren
			OutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", os);
			// jpg Base64 encodieren
			byte[] ret = Base64.getEncoder().encode(os.toString().getBytes());
			// Bytearray als String zurückgeben
			return new String(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * löscht das Verzeichnis oder die Datei rekursiv inklusive aller Unterverzeichnisse
	 * @param f Datei
	 * @return  true wenn die Datei gelöscht wurde oder nicht existiert hatte
	 */
	public static boolean removeFile(File f) {
		boolean ret=true;
		if (f==null) return true;
		if (!f.exists()) return true;
		if (f.isFile()) f.delete();
		else if (f.isDirectory()) {
			for (File sf : f.listFiles())
				if (!removeFile(sf)) ret=false;
			f.delete();
		}
		return ret;
	}
	/**
	 * Durchsucht das angegebene Verzeichnis rekursiv nach Dateien welche dem
	 * Regexp entsprechen
	 * @param regexp       Regexp für das Suchen der Datein
	 * @param pfad         Startverzeichnis
	 * @param files        Liste aller gefundenen Dateien
	 */
	public static void findFiles(String regexp, File pfad, Vector<File> files) {
		if (pfad.isFile() && pfad.getAbsolutePath().matches(regexp))
			files.add(pfad);
		if (pfad.isDirectory()) {
			for (File sf : pfad.listFiles())
				findFiles(regexp,sf,files);
		}
	}
	/**
	 * Kopieren einer Datei
	 * @param source    Quelle
	 * @param dest      Ziel
	 * @return true wenn ok
	 */
	public static boolean copyFile(String source, String dest) {
		// kopieren der richtigen Datei
		try {
			Cmd.GenerateFile(dest);
			Files.copy((new File(source)).toPath(),(new File(dest)).toPath(),REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Entfernt aus einem Dateinamen alle Umlaute und Sonderzeichen.<br>
	 * Es bleiben nur mehr Buchstaben, Zahlen, Minus und Unterstrich erhalten<br>
	 * @param name alter Dateiname
	 * @return     neuer Dateiname
	 */
	public static String renameFile(String name) {
		name =  name.replaceAll("ö","oe").replaceAll("ü","ue").replaceAll("ä","ae").replaceAll("Ü","Ue").replaceAll("Ö","Oe").replaceAll("Ä","ae").replaceAll("ß","ss");
		name =  name.replaceAll("\\\\","-").replaceAll("/","-").replaceAll(" ","_").replaceAll("[^a-zA-Z0-9\\-_\\.]","");
		return name;
	}

	/**
	 * Entfernt aus einem Dateinamen alle Umlaute und Sonderzeichen.<br>
	 * Es bleiben nur mehr Buchstaben, Zahlen, Minus,Unterstrich und die Pfadtrenner / \ erhalten<br>
	 * @param name alter Pfadname
	 * @return     neuer Pfadname
	 */
	public static String renamePath(String name) {
		name =  name.replaceAll("ö","oe").replaceAll("ü","ue").replaceAll("ä","ae").replaceAll("Ü","Ue").replaceAll("Ö","Oe").replaceAll("Ä","ae").replaceAll("ß","ss");
		name =  name.replaceAll(" ","_").replaceAll("[^a-zA-Z0-9\\\\/\\-_\\.]","");
		return name;
	}

	public static String removeSonderzeichen(String Variable) {
		if (Variable == null) return "";
		Variable = Variable.replaceAll("á", "a");
		Variable = Variable.replaceAll("ć", "c");
		Variable = Variable.replaceAll("č", "c");
		Variable = Variable.replaceAll("ċ", "c");
		Variable = Variable.replaceAll("Ç", "C");
		Variable = Variable.replaceAll("é", "e");
		Variable = Variable.replaceAll("è", "e");
		Variable = Variable.replaceAll("š", "s");
		Variable = Variable.replaceAll("Š", "S");
		Variable = Variable.replaceAll("ž", "z");

		return Variable;
	}

	/**
	 * Ersetzt alle Umlaute in normale Zeichen
	 * @param Variable : Text mit möglichen Umlauten
	 * @return Ersetzt alle Umlaute in normale Zeichen
	 */
	public static String removeUmlaute(String Variable) {
		return removeUmlaute(Variable, true);
	}
	public static String removeUmlaute(String Variable, boolean removeUnderscore) {

		if (Variable == null) return "";
		if (removeUnderscore)
			Variable = Variable.replaceAll("[_{},\\\\]", "");
		Variable = Variable.replaceAll("\\s+", "");

		Variable = Variable.replaceAll("ä", "ae");
		Variable = Variable.replaceAll("ö", "oe");
		Variable = Variable.replaceAll("ü", "ue");
		Variable = Variable.replaceAll("Ä", "Ae");
		Variable = Variable.replaceAll("Ö", "Oe");
		Variable = Variable.replaceAll("Ü", "Ue");
		Variable = Variable.replaceAll("ß", "sz");

		return removeSonderzeichen(Variable);
	}

	public static String loadFileAsBase64(File file) {
		if (file!=null && file.exists()) {
			String ImgString = "";
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				byte fileContent[] = new byte[(int)file.length()];
				fis.read(fileContent);
				byte[] ret = Base64.getEncoder().encode(fileContent);
				ImgString = new String(ret);
				fis.close();
				return ImgString;
			} catch (Exception ignored) {}
		}
		return "";
	}

}