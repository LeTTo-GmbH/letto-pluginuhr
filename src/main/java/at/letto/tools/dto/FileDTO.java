package at.letto.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Klasse zum Speichern und Weiterreichen von Dateien lokal und über den Webserver
 */
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class FileDTO {

    /** Dateiname über den die Datei im Imageservice verarbeitet werden kann */
    private String filename="";

    /** Web-Pfad über den die Datei im Internet erreichbar ist */
    private String webPath="";

    /** Einheit der Größenangabe des Bildes */
    private IMAGEUNIT unit = IMAGEUNIT.none;

    /** Breite des Bildes in der Einheit unit*/
    private double width;

    /** Breite des Original-Bildes in Pixel */
    private int widthPx;

    /** Höhe des Original-Bildes in Pixel*/
    private int heightPx;

    /** Alternativtext für Barrierefreiheit */
    private String alternate="Ein Bild von Letto";

    /** Style-Kommentare zum Bilder-Tag */
    private String style="";

    /** Ist hier ein Text angegeben, so wird die Datei als Link mit dem angegebenen Text dargestellt */
    private String link="";

    /** Titel des Bildes */
    private String title="";

    /**
     * Erzeugt ein FileDTO Objekt
     * @param filename Dateiname am Image-Service
     * @param webPath  Web-Pfad über den die Datei im Internet erreichbar ist
     * @param alternate Alternativtext für Barrierefreiheit
     */
    public FileDTO(String filename, String webPath, String alternate) {
        this.filename = filename;
        this.webPath  = webPath;
        this.alternate= alternate;
    }

    public FileDTO(ImageBase64Dto imageBase64Dto) {
        filename = imageBase64Dto.getImageInfoDto().getFilename();
        webPath  = imageBase64Dto.getImageInfoDto().getUrl();
        unit     = imageBase64Dto.getImageInfoDto().getUnit();
        width    = imageBase64Dto.getImageInfoDto().getImageWidth();
        widthPx  = imageBase64Dto.getImageInfoDto().getWidth();
        heightPx = imageBase64Dto.getImageInfoDto().getHeight();
        alternate= imageBase64Dto.getImageInfoDto().getAlternate();
        style    = imageBase64Dto.getImageInfoDto().getStyle();
        title    = imageBase64Dto.getImageInfoDto().getTitle();
    }

    public FileDTO(ImageUrlDto imageUrlDto) {
        filename = imageUrlDto.getImageInfoDto().getFilename();
        webPath  = imageUrlDto.getImageInfoDto().getUrl();
        unit     = imageUrlDto.getImageInfoDto().getUnit();
        width    = imageUrlDto.getImageInfoDto().getImageWidth();
        widthPx  = imageUrlDto.getImageInfoDto().getWidth();
        heightPx = imageUrlDto.getImageInfoDto().getHeight();
        alternate= imageUrlDto.getImageInfoDto().getAlternate();
        style    = imageUrlDto.getImageInfoDto().getStyle();
        title    = imageUrlDto.getImageInfoDto().getTitle();
    }


    /**
     * Liefert die Attribute, welche im Image-Tag eines Bildes verwendet werden sollen.<br>
     * @return Attribute des Image-Tags eines Bildes
     */
    public String imageAttributes() {
        String ret = "";
        if (width>0)  			    ret += " width=\""+width+unit+"\"";
        if (alternate.length()>0) 	ret += " alt=\""  +alternate+"\"";
        if (title.length()>0) 	    ret += " title=\""+title+"\"";
        if (style.length()>0) 	    ret += " style=\""+style+"\"";
        return ret;
    }

    public String htmlImageWidth() {
        switch(getUnit()) {
            default:
            case none:return "";
            case px:  return ((int)getWidth())+"px";
            case pt:  return ((int)getWidth())+"pt";
            case cm:  return getWidth()+"cm";
            case percent: return ((int)getWidth())+"%";
            case em:  return ((int)getWidth())+"em";
        }
    }

}
