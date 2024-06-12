package at.letto.plugins.dto;

import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRenderResultRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** true für LaTeX-Code, false für html-Code */
    boolean tex;

    /** Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird */
    private PluginDto pluginDto;

    /** Antwort die der Schüler eingegeben hat */
    private String antwort;

    /** Toleranz für die Lösung */
    private ToleranzDto toleranz;

    /** Referenz auf VarHash, wird dynamisch nachgeladen */
    private VarHashDto varsQuestion;

    /** Antwort des Schülers */
    private PluginAnswerDto answerDto;

    /** Maximale Punktanzahl für die richtige Antwort */
    private double grade;

}
