package at.letto.plugins.dto;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Information vom Plugin an LeTTo welche Informationen für die Konfiguration des Plugins benötigt werden und
 * wie die Konfiguration des Plugins erfolgen soll.<br>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginConfigurationInfoDto {

    /** Es gibt keinen expliziten Konfigurations-Dialog, sondern nur den Konfigurations-String */
    public static final int CONFIGMODE_STRING=0;

    /** Das Plugin ist im Edit-Service direkt eingebunden und wird über eine JSF-Seite direkt im Edit-Service konfiguriert */
    public static final int CONFIGMODE_JSF=1;

    /** Das Plugin wird über eine Javascript - Funktion konfiguriert */
    public static final int CONFIGMODE_JAVASCRIPT=2;

    /** Das Plugin wird über einen Dialog in einem iframe oder in einem eigenen Browser-TAB welches vom Plugin-Service über eine URL zur Verfügung gestellt wird konfiguriert */
    public static final int CONFIGMODE_URL=3;

    //--------------------------------------------------------------------------------------

    /** Konfigurations ID */
    private String configurationID="";

    /** Konfigurations-Mode für die Konfiguration des Plugins */
    private int configurationMode = CONFIGMODE_STRING;

    /** Gibt an ob im Plugin für die Konfiguration die Frage benötigt wird */
    private boolean useQuestion=true;

    /** Gibt an ob im Plugin für die Konfiguration der Vars Varhash benötigt wird */
    private boolean useVars=false;

    /** Gibt an ob im Plugin für die Konfiguration der cVars Varhash benötigt wird */
    private boolean useCVars=false;

    /** Gibt an ob im Plugin für die Konfiguration der MaximaVars Varhash benötigt wird */
    private boolean useMaximaVars=false;

    /** Gibt an ob im Plugin für die Konfiguration der MVars Varhash benötigt wird */
    private boolean useMVars=false;

    /** Gibt an, ob im Plugin-Konfig-Dialog Datensätze hinzugefügt werden können.
     * => Button AddDataset in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    private boolean addDataSet=false;

    /** Gibt an ob im Plugin bei der Konfiguration die Maxima-Berechnung durchlaufen werden kann.
     * => Button Maxima in Fußzeile des umgebenden Dialogs, (nicht vom Plugin) */
    private boolean calcMaxima=false;

    /** Gibt an, ob das Plugin über den Browser direkt erreichbar ist */
    private boolean externUrl=false;

    /** Java-Script-Methode, die beim Konfigurieren des Plugins aufgerufen wird. <br>
     * Als Parameter wird ein PluginConfigDto übergeben (Konfigurationsstring des Plugins an diese Methode übergeben. <br>
     * JS-Code ist in den javascriptLibraries enthalten. */
    private String javaScriptMethode=null;

    /** Konfigurations-URL für den Konfigurationsdialog im Mode CONFIGMODE_URL */
    private String configurationUrl="";

}
