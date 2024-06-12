package at.letto.plugins.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bild-Dateien die in einem Plugin erstellt wurden.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginFileDto {

    /** Bild-Informationen */
    private String base64Image="";

    /** Bildbreite in Prozent */
    private int 	tagWidth=50;

    /** Bildhöhe wird aktuell nicht verwendet */
    private int 	tagHeight=50;

    /** alternativer Beschriftungstext wenn das Bild nicht geladen werden kann */
    private String  tagAlt="";

    /** Titel des Bildes */
    private String  tagTitle="";

    /** Style Tag für die Formatierung des Bildes */
    private String  tagStyle="";

    /** Name des Bildes welcher im IMG-Tag des Bildes verwendet wird */
    private String  filename="";

    /** Fehlermeldung wenn das Bild nicht erzeugt werden konnte */
    private String  error="";

}
