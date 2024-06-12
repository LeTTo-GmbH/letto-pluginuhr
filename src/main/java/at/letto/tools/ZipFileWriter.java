package at.letto.tools;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Klasse für die Erzeugung von Zip-Dateien
 * @author Werner
 *
 */
public class ZipFileWriter {

	private FileOutputStream fos = null;
	private ZipOutputStream zipOut = null;
	
	/**
	 * Erzeugt einen Zip-Datei-Writer auf die Datei mit dem Dateinamen filename<br>
	 * Die Datei wird dabei neu erzeugt!
	 * @param filename     Dateiname
	 * @throws IOException Fehler wenn die Datei nicht erzeugt werden kann
	 */
	public ZipFileWriter(String filename) throws IOException {
		this(new File(filename));
	}
	
	/**
	 * Erzeugt einen Zip-Datei-Writer auf die Datei file<br>
	 * Die Datei wird dabei neu erzeugt!
	 * @param file         Datei
	 * @throws IOException Fehler wenn die Datei nicht erzeugt werden kann
	 */
	public ZipFileWriter(File file) throws IOException {
		fos = null;
        zipOut = null;
        fos = new FileOutputStream(file);
        zipOut = new ZipOutputStream(new BufferedOutputStream(fos));   
	}
		
	/**
	 * Dateiverbinden beenden und Datei schließen.
	 */
	public void close() {
        try {
			zipOut.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Datei zum Zip hinzufügen 
	 * @param filename Dateiname der hinzugefügt werden soll
	 * @throws IOException Fehler wenn die Datei nicht hinzugefügt werden kann
	 */
	public void addFile(String filename)  throws IOException  { 
		addFile(new File(filename));
	}

	private String normalizePath(String path) {
		path = path.replaceAll("\\\\","\\/").trim();
		while (path.startsWith("/")) path = path.substring(1);
		while (path.endsWith("/"))   path = path.substring(0,path.length()-1);
		return path;
	}

	/**
	 * Fügt ein neues Verzeichnis hinzu.
	 * @param path Pfad des Verzeichnisses wie verz/ oder verz oder /verz oder /verz/ oder /verz/unterverz
	 * @throws IOException Fehlermeldung
	 */
	public void addDirectory(String path) throws IOException {
		path = normalizePath(path);
		zipOut.putNextEntry(new ZipEntry(path+"/"));
	}

	/**
	 * Datei zum Zip hinzufügen
	 * @param file Datei die hinzugefügt werden soll
	 * @param name Name unter dem die Datei hinzugefügt werden soll
	 * @throws IOException Fehler wenn die Datei nicht hinzugefügt werden kann
	 */
	public void addFile(File file, String name) throws IOException {
		addFile("",file,name);
	}

	/**
	 * Datei zum Zip hinzufügen
	 * @param file Datei die hinzugefügt werden soll
	 * @throws IOException Fehler wenn die Datei nicht hinzugefügt werden kann
	 */
	public void addFile(File file) throws IOException {
		addFile("",file,file.getName());
	}

	/**
	 * Datei zum Zip hinzufügen
	 * @param path Verzeichnis innerhalb der Zip-Datei in dass die Datei gelegt werden soll
	 * @param file Datei die hinzugefügt werden soll
	 * @param name Dateiname unter dem die Datei hinzugefügt werden soll
	 * @throws IOException Fehler wenn die Datei nicht hinzugefügt werden kann
	 */
	public void addFile(String path, File file, String name) throws IOException  {
		path = normalizePath(path).trim();
		if (path.length()>0) path = path+"/";
		FileInputStream fis = null;
        fis = new FileInputStream(file);
        ZipEntry ze = new ZipEntry(path+name);
        zipOut.putNextEntry(ze);
        byte[] tmp = new byte[4*1024];
        int size = 0;
        while((size = fis.read(tmp)) != -1){
            zipOut.write(tmp, 0, size);
        }
        zipOut.flush();
        fis.close();
	}
	
	public static void main(String[] args) {
		ZipFileWriter z = null;
		try {
			z = new ZipFileWriter("tex/a.zip");
			z.addFile("tex/bsp.pdf");
			z.close();
		} catch (IOException e) {
			if (z!=null) z.close();
		}
	}

}
