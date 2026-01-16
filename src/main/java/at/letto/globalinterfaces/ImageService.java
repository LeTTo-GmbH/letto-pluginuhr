package at.letto.globalinterfaces;

import at.letto.tools.Cmd;
import at.letto.tools.ENCRYPT;
import at.letto.tools.WebGet;
import at.letto.tools.dto.FileBase64Dto;
import at.letto.tools.dto.FileDTO;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.tex.Tex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service für die Bildverwaltung am Letto-Server<br>
 * Der Dateiname der Bilder ergibt sich aus der Prüfsumme der Datei bei normalen Bildern oder aus der Prüfsumme
 * eines Identifikationsstrings bei Plugin-Bildern.<br>
 */
public interface ImageService {

    /**
     * Prüft ob das Service korrekt Daten im Filesystem speichern kann und gibt eine Fehlermeldung zurück wenn etwas nicht funktioniert oder
     * einen Leerstring wenn Dateien korrekt gespeichert und gelesen werden können.
     * @return Fehlermeldung oder Leerstring
     */
    String checkFilesystem();

    /**
     * Prüft die Funktionalität des Services und gibt eine Fehlermeldung zurück wenn etwas nicht funktioniert oder
     * einen Leerstring wenn der Cache problemlos funktioniert.
     * @return Fehlermeldung oder Leerstring
     */
    String checkService();

    /**
     * Prüft ob ein Bild mit dem angegebenen Dateinamen schon existiert
     * @param filename    Dateiname
     * @return true wenn der Dateiname existiert
     */
    boolean existImage(String filename);

    /**
     * Liefert das Alter eines Bildes in Millisekunden
     * @param filename    Dateiname
     * @return            Alter des Bildes in Millisekunden, -1 wenn das Bild nicht existiert, -2 wenn der filename ungültig ist
     */
    long getImageAge(String filename);

    /**
     * Liefert die Größe eines Bildes in Byte
     * @param filename    Dateiname
     * @return            Größe des Bildes, -1 wenn das Bild nicht existiert, -2 wenn der filename ungültig ist
     */
    long getImageSize(String filename);

    /**
     * Löscht das Bild von der Festplatte
     * @param filename    Dateiname
     * @return            true wenn das Bild gelöscht werden konnte
     */
    boolean delImage(String filename);

    /**
     * Liefert die URL, mit der auf die Datei zugegriffen werden kann.
     * @param filename Dateiname
     * @return        URL mit der auf die Datei zugegriffen werden kann, Leerstring wenn der filename nicht vorhanden ist
     */
    String getURL(String filename);

    /**
     * Liefert die URL, mit der auf die Datei zugegriffen werden kann.
     * @param filename Dateiname
     * @return        URL mit der auf die Datei zugegriffen werden kann, Leerstring wenn der filename nicht vorhanden ist
     */
    String getAbsURL(String filename);

    /**
     * Erzeugt eine Datei mit dem angegebenen Dateinamen mit einer Dateigröße von null
     * @param filename Dateiname
     * @return         true wenn die Datei erzeugt werden konnte
     */
    boolean createFile(String filename);

    /**
     * prüft ob ein Dateiname den Bedingungen für die Dateinamen entspricht
     * @param filename Dateiname
     * @return         true wenn der Dateiname gültig ist
     */
    boolean isFilenameOK(String filename);

    /**
     * Liefert die Extension eines Dateinamens
     * @param filename Dateiname
     * @return         Extension
     */
    String getExtension(String filename);

    /**
     * Speichert eine Base-64-kodierte Datei und sein zugehörigen Bild-Informationen
     * @param imageBase64Dto Bild und Bild-Informationen
     * @return               Leer wenn ok, oder eine Fehlermeldung
     */
    String saveImage(ImageBase64Dto imageBase64Dto);

    /**
     * Speichert eine Base-64-kodierte Datei
     * @param base64File   Dateiinhalt
     * @param filename     Dateiname
     * @return             Leer wenn ok, oder eine Fehlermeldung
     */
    String saveImage(String base64File, String filename);

    /**
     * Speichert eine mit der AWT erzeugte Datei
     * @param image        Dateiinhalt
     * @param filename     Dateiname unter dem die Datei gespeichert wird
     * @return             Leer wenn ok, oder eine Fehlermeldung
     */
    String saveImage(BufferedImage image, String filename);

    /**
     * Speichert eine als Byte-Array vorliegende Datei
     * @param byteArray    Dateiinhalt
     * @param extension    Extension der Datei für den Dateityp
     * @return             Dateiname der gespeicherten Datei oder Leerstring bei einem Fehler
     */
    String saveByteArrayImage(byte[] byteArray, String extension);

    /**
     * Speichert eine Base-64-kodierte Datei
     * @param base64encodedString   Dateiinhalt
     * @param extension    Extension der Datei für den Dateityp
     * @return             Dateiname der gespeicherten Datei oder Leerstring bei einem Fehler
     */
    String saveBase64Image(String base64encodedString, String extension);

    /**
     * Lädt eine Datei von einer URL und speichert sie im Image-Service unter der md5-Summe der Datei ab
     * @param webPath  URI der Datei welche gespeichert werden soll
     * @return         Dateiname der gespeicherten Datei oder Leerstring bei einem Fehler
     */
    String saveURLImage(String webPath);

    /**
     * Speichert eine lokale Datei im Image-Service
     * @param file         Datei
     * @return             Dateiname der gespeicherten Datei oder Leerstring bei einem Fehler
     */
    String saveLocalImage(File file);

    /**
     * Lädt eine Datei als Base64 Codierte Datei aus dem Filesystem in eine Base64-codierten String
     * @param filename     Dateiname der Datei die geladen werden soll
     * @return             Base64-codierter String des Dateiinhaltes
     */
    String loadImageBase64(String filename);

    /**
     * Lädt eine Datei als Base64 Codierte Datei aus dem Filesystem in eine Base64-codierten String
     * @param filename     Dateiname der Datei die geladen werden soll
     * @return             Base64-codierter String des Dateiinhaltes
     */
    ImageBase64Dto loadImageBase64Dto(String filename);

    /**
     * Lädt eine Datei als Base64 codierte Datei aus dem Filesystem in eine Base64-codierten String
     * @param fileDTO      Dateiname/URL der Datei die geladen werden soll
     * @return             Base64-codierter String des Dateiinhaltes
     */
    String loadImageBase64(FileDTO fileDTO);

    /**
     * Liefert eine Liste aller Dateien, die im Image Cache gespeichert sind
     * @return Liste aller Dateinamen
     */
    Vector<String> getImages();

    /**
     * Löscht alle Dateien welche im Vektor angegeben sind
     * @param images Liste aller Dateinamen die gelöscht werden sollen
     * @return       Liste der gelöschten Dateinamen
     * */
    Vector<String> delImages(Vector<String> images);

    /**
     * Liefert eine Liste aller Dateinamen der Dateien die älter als age ms sind
     * @param age  Alter in ms
     * @return     Liste aller Dateinamen der Dateien die älter als age ms sind
     */
    Vector<String> getImagesOlderThan(long age);

    /**
     * liefert ein File-Objekt, welches auf eine lokale Datei zeigt. Ist die Datei nicht lokal vorhanden wird sie lokal
     * heruntergeladen und dann das File-Objekt zurückgegeben. Ist die Datei lokal vorhanden wird ein File-Objekt auf
     * diese Datei zurückgegeben.
     * @param filename Dateiname
     * @return         File-Objekt welches auf eine lokale Datei zeigt. Ist die Datei nicht vorhanden wird null zurückgegeben.
     */
    File getLocalFile(String filename);

    /**
     * Anpassen des Image-Services an ein lokales Service:
     * Die URL wird nicht mehr vollständig zurückgeben, sondern nur mehr
     * ausgehend vom akt. Server beginnend mit zB. /images/....<br>
     *     Diese Anpassung wird nur durchgeführt, wenn das Image-Service und der Server
     *     die gleiche IP-Adresse und das gleiche port haben.
     *     Notwendig, um das Download-Attirbute im <a href=...></a>-Tag nutzen zu können
     * @param serverpath   Servlet-Path
     */
    void adaptUrlToRelative(String serverpath);

    /* ---------------------------------------------------------------------------------------------------
          statische Methoden
       --------------------------------------------------------------------------------------------------- */
    /**
     * Prüft einen Dateinamen ob er gültig ist und ändert ihn gegebenenfalls auf einen gültigen Namen
     * @param filename Dateiname
     * @return         gültiger Dateiname
     */
    public static String checkFilename(String filename) {
        Pattern p = Pattern.compile("^(.+)\\.([^\\.]*)$");
        Matcher m = p.matcher(filename);
        String name = filename;
        String ext = "ext";
        if (m.find()) {
            name = m.group(1);
            ext = m.group(2);
        }
        name = name.toLowerCase();
        ext = ext.toLowerCase().replaceAll("\\.", "");
        name = Cmd.renameFile(name);
        ext = Cmd.renameFile(ext);
        if (name.length() < 3) name = "file" + name;
        if (ext.length() < 1) ext = "ext";
        return name + "." + ext;
    }

    /**
     * Erzeugt aus einem String eine md5-Prüfsumme für den Dateinamen<br>
     * Mit diesem Dateinamen sollte das Bild dann im Imageservice gespeichert und geladen werden können
     * @param base       Basis-String, der entweder der Dateiinhalt, oder eine eindeutige Beschreibung des Bildes darstellt.
            * @param extension  Extension der Datei
     * @return           Dateiname aus md5-Prüfsumme und Extension
     */
    public static String generateFilename(String base, String extension) {
        String md5 = ENCRYPT.md5falsch(base);
        String filename = checkFilename(md5 + "." + extension);
        return filename;
    }

    /**
     * Erzeugt aus einem byteArray des Dateiinhaltes eine md5-Prüfsumme für den Dateinamen<br>
     * Mit diesem Dateinamen sollte das Bild dann im Imageservice gespeichert und geladen werden können
     * @param base       Dateiinhalt
     * @param extension  Extension der Datei
     * @return           Dateiname aus md5-Prüfsumme und Extension
     */
    public static String generateFilename(byte[] base, String extension) {
        String md5 = ENCRYPT.md5falsch(base);
        String filename = checkFilename(md5 + "." + extension);
        return filename;
    }

    public static String imgToBase64String(final BufferedImage img) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", Base64.getEncoder().wrap(os));
            return os.toString("UTF-8");
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * Liefert den fertigen HTML-Tag zur Darstellung eines Bild. Inkludiert snd JS-Libs zum Öffnen des Bildes in
     * einem eigenen Fenster
     * @param fileDTO   FileDTO vom Image-Service
     * @param dblClick	Bei Doppelclick wird das Bild groß aufgeklappt, sonst bei Einfachclick
     * @return	img-Tag
     */
    static String getImageWeb(FileDTO fileDTO, boolean dblClick) {
        if (fileDTO!=null) {
            return String.format(
                    "<img on%sclick='openImg(\"%s\")' src=\"%s\" alt=\"%s\" style=\"width:%s;\">" ,
                    dblClick ? "dbl":"" , fileDTO.getWebPath(),
                    fileDTO.getWebPath(), fileDTO.getAlternate(), fileDTO.htmlImageWidth());
        }
        else return "";
    }

    /**
     * Lädt ein Bild von einer HTML img-src als Base64 codierten String
     * @param imgSrc Inhalt des src-Attributes eines HTML-img-Tags
     * @return       Inhalt Base64 codiert oder null wenn Datei nicht ladbar ist
     */
    static FileBase64Dto loadImageSource(String imgSrc){
        FileBase64Dto fileBase64Dto=null;
        Matcher m= Pattern.compile("^data:image/(?<ext>[^;\\+]+).*base64,(?<image>.*)$").matcher(imgSrc);
        if (m.find()) try {
            String extension = m.group("ext").toLowerCase();
            String base64     = m.group("image");
            byte[] image      = ENCRYPT.base64Decode(base64);
            String md5        = ENCRYPT.md5falsch(image);
            String filename   = checkFilename(md5 + "." + extension);
            if (image.length>0 && base64.length()>0) {
                fileBase64Dto = new FileBase64Dto(filename, extension, md5, base64);
                return fileBase64Dto;
            }
        } catch (Exception e) {}
        m= Pattern.compile("(^|[^?]*/)(?<name>[^./?]+)(\\.(?<ext>[^./?]+))?(\\?.*)?$").matcher(imgSrc);
        if (m.find()) try {
            byte[] inhalt = WebGet.getUrlByteArray(imgSrc);
            String extension = m.group("ext").toLowerCase();
            if (extension==null) extension="jpg";
            String filename  = checkFilename(m.group("name")+"."+extension);
            String md5       = ENCRYPT.md5falsch(inhalt);
            String base64 = ENCRYPT.base64Encode(inhalt);
            if (inhalt.length>0 && base64.length()>0) {
                fileBase64Dto = new FileBase64Dto(filename,extension,md5,base64);
                return fileBase64Dto;
            }
        } catch (Exception e) {}
        return null;
    }

}