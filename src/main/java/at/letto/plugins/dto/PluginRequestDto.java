package at.letto.plugins.dto;

import at.letto.plugins.enums.InputElement;
import at.letto.tools.JavascriptLibrary;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginRequestDto {

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

    /** Art der Berechnung */
    private PluginMaximaCalcModeDto pluginMaximaCalcMode;

}
