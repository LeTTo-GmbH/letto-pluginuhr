package at.letto.plugins.interfaces;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.plugins.dto.*;
import at.letto.plugins.enums.InputElement;
import at.letto.tools.JavascriptLibrary;
import at.letto.tools.dto.ImageBase64Dto;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Interface für die Plugin-Programmierung<br>
 * Alle DTOs sind von LeTTo direkt unabhängig<br>
 */
public interface PluginService {

    /** @return Liefert den Namen der Wiki-Seite wenn eine Doku am LeTTo-Wiki vorliegt */
    String getWikiHelp();

    /** @return Liefert eine Hilfe-URL für die Beschreibung des Plugins */
    String getHelpUrl();

    /** @return Gibt an ob die Standard-Plugin-Configuration verwendet werden soll */
    boolean isDefaultPluginConfig();

    /** @return Gibt an ob das Plugin Ergebnisse und VarHash als JSON-String verarbeiten kann */
    boolean isMath();

    /**
     * Liefert den Typ des Plugins
     * @return Klasse des Plugins
     */
    String getPluginType();

    /** @return Liefert die aktuelle Version eines Plugins, ändert sich diese, so ändert sich auch die Prüfsumme jedes Plugin Bildes */
    String getPluginVersion();

    /** @return Liefert den Konfigurationsstring des Plugins */
    String getConfig();

    /** @return Liefert den String welcher für die Definition des Plugins gespeichert wird [PI name typ "config"] */
    String getTag();

    /** Setzt den Namen des Plugins
     *  @param name Pluginname        */
    void setName(String name);

    /** @return Liefert den Namen des Plugins     */
    String getName();

    /** @return Liefert die Bildbreite in Prozent der Seitenbreite für die Ausgabe */
    int getImageWidthProzent();

    /** @return Liefert eine HTML-Hilfe zu dem Plugin. */
    String getHelp();

    /** Liefert einen Angabestring für die MoodleText Angabe und erzeugt gegebenenfalls fehlende Datasets in der Datasetliste
     * @param params  Parameter für die Einstellungen
     * @return        String für das MoodleText-Feld
     */
    String getAngabe(String params);

    /** @return Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten  */
    List<PluginDatasetDto> generateDatasets();

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     * @return Liste aller Variablen des Plugins
     */
    Vector<String> getVars();

    /** @return liefert die Breite des Plugin-Bildes */
    int getWidth();

    /** @return liefert die Höhe des Plugin-Bildes */
    int getHeight();

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     * @param params  Parameter für die Antworterzeugung
     * @param q       Frage wo das Plugin eingebettet ist
     * @return        HTML Text
     */
    String getHTML(String params, PluginQuestionDto q);

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     * @param params Parameter
     * @param q      Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return       Maxima Berechnungs-String
     */
    String getMaxima(String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode );

    /**
     * Wertet die Parameter für die Bild-Routinen aus
     * @param params Parameter des Bild-Tags
     * @param q      aktuelle Frage
     * @param pluginImageResultDto Nimmt alle Fehlermeldungen auf, welche beim Rendern des Bildes entstehen
     */
    void parseDrawParams(String params,PluginQuestionDto q, PluginImageResultDto pluginImageResultDto);

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     * @param   params   Parameter für die Bilderzeugung
     * @param   q        Frage wo das Plugin eingebettet ist
     * @return           Base64 kodiertes Bild in einem MoodleFile
     */
    ImageBase64Dto getImageDto(String params, PluginQuestionDto q);

    /**
     * Liefert ein FileDTO mit dem Dateinamen des erzeugten Bildes, welches im Filesystem gespeichert wurde
     * @param   params   Parameter für die Bilderzeugung
     * @param   q        Frage wo das Plugin eingebettet ist
     * @param   imageService  ImageService zum Erzeugen der Bilder
     * @return           FileDTO des Bildes welches gefunden oder erzeugt wurde.
     */
    //FileDTO getImage(String params, PluginQuestionDto q, ImageService imageService);

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     * @return  Liefert eine Liste aller möglichen Varianten von Bildern
     */
    Vector<String[]> getImageTemplates();

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     * @param vars   Alle Variablen der Frage
     * @param cp     Berechnungsparameter
     * @param p      Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return       Ergebnis der Funktion
     */
    //public CalcErgebnis parserPlugin(VarHash vars, CalcParams cp, CalcErgebnis ... p);
    CalcErgebnisDto parserPlugin(VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto ... p);

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     * @param p Einheiten der Parameter als Recheneinheiten
     * @return  Recheneinheit des Ergebnisses
     */
    String parserPluginEinheit(String ... p);

//---------------------------------------------------- QuestionPluginJavaScript ---------------

    /**
     * @return Gibt an ob das Plugin eine Java-Script Schnittstelle bei der Beispieldarstellung hat<br>
     * Im alten LeTTo als Interface QuestionPluginJavaScript
     */
    boolean isJavaScript();

    /** @return Alle globalen Informationen des Plugins */
    PluginGeneralInfo getPluginGeneralInfo();

    /**
     * Bestimmt einen eindeutigen String, welcher ein Plugin-Bild beschreibt um daraus den Dateinamen bestimmen zu können.
     * @param imageParams Parameter des PIG-Tags
     * @param q           Frage in der das Plugin eingebettet ist
     * @return            eindeutiger String der das Plugin-Bild eindeutig beschreibt.
     */
    String getPluginImageDescription(String imageParams, PluginQuestionDto q);

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param pluginDef	akt. Plugin-Definition
     * @param jsResult	Rückgabe von Javascript
     * @return	aktualiesierte Plugindefinition
     */
    String updatePluginstringJavascript(String pluginDef, String jsResult);

    /**
     * Liefert eine Liste von Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden!
     * @return	für das Plugin notwendige JS-Libraries
     */
    List<JavascriptLibrary> javascriptLibraries();

    /**
     * Liefert eine Liste von LOKALEN Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     * @return	für das Plugin notwendige JS-Libraries
     */
    List<JavascriptLibrary> javascriptLibrariesLocal();

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param params        Plugin-Parameter
     * @param q             Question, in die das Plugin eingebettet ist
     * @param nr            Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return              PluginDto
     */
    PluginDto loadPluginDto(String params, PluginQuestionDto q, int nr);

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param pluginDto Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return          Latexsourcode und zugehörige Bilder in einer Hashmap
     */
    PluginRenderDto renderLatex(PluginDto pluginDto, String answer, String mode);

    /**
     * Prüft die Eingabe eines Schülers
     * @param pluginDto           PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto           Antwort des Schülers
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    Bewertung
     */
    PluginScoreInfoDto score(PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade);

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Text der innerhalb der PI Tags gestanden ist
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter Text
     */
    String modifyAngabe(String text, PluginQuestionDto q);

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param text  Angabetext der Frage
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter AngabeText
     */
    String modifyAngabeTextkomplett(String text, PluginQuestionDto q);

    /**
     * Methode definiert das Eingabeelement der Subquestion, die das
     * Plugin verwendet
     * @return anzuzeigendes Eingabeelement, default: TextField
     */
    InputElement getInputElement();

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param tex                 true für LaTeX-Code, false für html-Code
     * @param pluginDto           PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto           Antwort des Schülers
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    HTML-Code oder LaTeX-Code mit Bildern
     */
    PluginRenderDto renderPluginResult(boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade);

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @return                   alle notwendigen Konfig
     */
    PluginConfigurationInfoDto configurationInfo(String configurationID);

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     */
    PluginConfigDto setConfigurationData(String configuration, PluginQuestionDto questionDto);

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    String getConfiguration();

}
