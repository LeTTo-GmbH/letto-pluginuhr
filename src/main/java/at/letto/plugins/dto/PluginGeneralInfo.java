package at.letto.plugins.dto;

import at.letto.plugins.enums.InputElement;
import at.letto.tools.JavascriptLibrary;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Liefert allgemein Informationen zu einem Plugin, welche ohne Definition einer Plugin-Instanz allgemein gültig sind
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginGeneralInfo {

    /** Typ des Plugins */
    private String typ="";

    /** Version des Plugins */
    private String version="";

    /** Liefert den Namen der Wiki-Seite wenn eine Doku am LeTTo-Wiki vorliegt */
    private String wikiHelp="";

    /** Liefert eine Hilfe-URL für die Beschreibung des Plugins */
    private String helpUrl="";

    /** Hilfetext für das Plugin */
    private String help="";

    /** Gibt an ob die Standard-Plugin-Configuration verwendet werden soll */
    private boolean defaultPluginConfig=true;

    /** Gibt an ob das Plugin Ergebnisse und VarHash als JSON-String verarbeiten kann */
    private boolean math=false;

    /** Klasse des Plugins */
    private String pluginType="";

    /** Name der JAVA-Script Methode zur Plugin-Initialisierung für die interaktive Ergebniseingabe */
    private String initPluginJS="";

    /** Gibt an ob das Plugin eine Java-Script Schnittstelle bei der Beispieldarstellung hat */
    private boolean javaScript=false;

    /** Liste von Javascript-Libraries, die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden.
     * für das Plugin notwendige JS-Libraries
     */
    private List<JavascriptLibrary> javascriptLibraries=new ArrayList<>();

    /** Liste von LOKALEN Javascript-Libraries, die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     * für das Plugin notwendige JS-Libraries
     */
    private List<JavascriptLibrary> javascriptLibrariesLocal=new ArrayList<>();

    /** anzuzeigendes Eingabeelement als String */
    private String inputElement = InputElement.TextField.toString();

    /** Plugin ist stateless und liefert bei gleicher Angabe immer das gleiche Verhalten */
    private boolean cacheable = true;

    /** Plugin benötigt zur Berechnung und Darstellung den VarHash der Datensätze */
    private boolean useVars = true;

    /** Plugin benötigt zur Berechnung und Darstellung den VarHash der cvars - Konstante und Datensätze */
    private boolean useCVars= true;

    /** Plugin benötigt zur Berechnung und Darstellung den Varhash aller Maxima-Berechnungen ohne eingesetzte Datensatz-Variable */
    private boolean useVarsMaxima=true;

    /** Plugin benötigt zur Berechnung und Darstellung den Varhash aller Datensätze und Maxima-Ergebnisse mit eingesetzten Werten kombiniert in einem Hash */
    private boolean useMVars=true;

    /** URL des Plugin-Services für die direkte Kommunikation*/
    private String pluginServiceURL="";

}
