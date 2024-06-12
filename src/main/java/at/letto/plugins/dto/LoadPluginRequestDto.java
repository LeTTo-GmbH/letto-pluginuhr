package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadPluginRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Parameter für die Antworterzeugung */
    private String params="";

    /** Frage wo das Plugin eingebettet ist */
    private PluginQuestionDto q;

    /** Laufende Nummer für alle PIG-Tags und Question-Plugins */
    private int nr;

    /** Configuration-ID, wenn das PluginDto über AJAX bei der Konfiguration angefordert wird */
    private String configurationID;

}
