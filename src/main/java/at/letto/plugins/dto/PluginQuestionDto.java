package at.letto.plugins.dto;

import at.letto.math.dto.VarHashDto;
import lombok.*;

import java.util.List;

/**
 * Daten die von einer Question an ein Plugin übergeben werden sollten
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginQuestionDto implements Cloneable {

    public PluginQuestionDto clone() {
        try {
            return (PluginQuestionDto)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /** id der Frage */
    private long                       id;
    /** Name der Frage */
    private String                     name;
    /** Standarddefinitionen welche vor dem Durchlauf von Maxima ausgeführt werden */
    private String                     maximaDefs;
    /** Inhalt der Moodle.mac */
    private String                     moodlemac;
    /** erreichbare Punkteanzahl einer Frage */
    private double                     points;
    /** Liste aller Teilfragen der Frage */
    private List<PluginSubQuestionDto> subQuestions;
    /** Haupt-Maxima-Feld der Frage */
    private String                     maxima;
    /** Liste aller Dateinamen (md5-Prüfsumme.extension) aller Bilder */
    private List<String>               images;
    /** Liste aller Bilder entweder Base64 codiert (mit base64:beginnend) oder als externe URL(mit http:// oder https:// beginnend!!)
     *  oder als reiner Text (mit text:beginnend wenn die Datei eine reine Textdatei ist)
     *  vorzugsweise wird die externe URL verwendet, nur wenn dem Server bekannt ist, dass das Plugin
     *  nicht selbst auf die externe Url der Images zugreifen kann UND die Dateien vom Plugin
     *  nicht direkt über das Dateisystem erreichbar sind (externe Plugins) wird base64: oder text:
     *  verwendet */
    private List<String>               imagesContent;
    /** Nummer des aktuellen Datensatzes */
    private int                        dsNr;
    /** Werte alle gesetzten Datensatz-Variablen */
    private VarHashDto                 vars;
    /** Konstante und Variablen welche als Datensatz definiert sind kombiniert in einem Hash */
    private VarHashDto                 cvars;
    /** Ergebnisse aller Maxima-Berechnungen ohne eingesetzte Datensatz-Variable */
    private VarHashDto                 varsMaxima;
    /** Konstante, Variablen welche als Datensatz definiert und Maxima-Ergebnisse mit eingesetzten Werten kombiniert in einem Hash */
    private VarHashDto                 mvars;

}
