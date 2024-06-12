package at.letto.plugins.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRenderLatexRequestDto {

    /** Typ des Plugins */
    private String typ="";

    /** Name des Plugins in der Frage */
    private String name="";

    /** Konfigurationsstring des Plugins */
    private String config="";

    /** Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird */
    private PluginDto pluginDto;

    /** Inhalt des Antwortfeldes welches der Schüler eingegeben hat */
    private String answer;

    /** Druckmode */
    private String mode;

}
