package at.letto.plugins.restclient;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.CalcParamsDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.plugins.dto.*;
import at.letto.tools.dto.ImageBase64Dto;
import at.letto.tools.dto.ImageUrlDto;

import java.util.List;
import java.util.Vector;

/**
 * verwaltet mehrere Plugins eines Servers<br>
 * Alle Plugins
 */
public interface PluginConnectionService {

    /** @return liefert eine Liste aller Plugins (Pluginnamen) , welche mit diesem Service verwaltet werden */
    List<String> getPluginList();

    /** @return liefert eine Liste aller globalen Informationen über alle Plugins des verwalteten Services */
    List<PluginGeneralInfo> getPluginGeneralInfoList();

    /** @param typ Plugin Typ
     *  @return liefert die allgemeinen Konfigurationsinformationen zu einem Plugin */
    PluginGeneralInfo getPluginGeneralInfo(String typ);

    /**
     * Berechnet den Fragetext für das Fragefeld des Webservers für die angegebenen Parameter für die Verwendung in einem PIT Tag
     * @param typ     Typ des Plugins
     * @param name    Name des Plugins in der Frage
     * @param config  Konfigurationsstring des Plugins
     * @param params  Parameter für die Antworterzeugung
     * @param q       Frage wo das Plugin eingebettet ist
     * @return        HTML Text
     */
    String getHTML(String typ, String name, String config, String params, PluginQuestionDto q);

    /** Liefert einen Angabestring für die MoodleText Angabe
     * @param typ     Typ des Plugins
     * @param name    Name des Plugins in der Frage
     * @param config  Konfigurationsstring des Plugins
     * @param params  Parameter für die Einstellungen
     * @return        String für das MoodleText-Feld
     */
    String getAngabe(String typ, String name, String config, String params);

    /** Liefert alle Datensätze, welche für das Plugin in der Frage vorhanden sein sollten
     * @param typ     Typ des Plugins
     * @param name    Name des Plugins in der Frage
     * @param config  Konfigurationsstring des Plugins
     * @return        Liste der Datensatzdefinitionen welche vom Plugin in der Frage angefordert werden */
    List<PluginDatasetDto> generateDatasets(String typ, String name, String config);

    /**
     * Liefert einen Maxima-Berechnungsstring für die Berechnung des Plugins
     * @param typ     Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config  Konfigurationsstring des Plugins
     * @param params Parameter
     * @param q      Frage wo das Plugin eingebettet ist
     * @param pluginMaximaCalcMode Art der Berechnung
     * @return       Maxima Berechnungs-String
     */
    String getMaxima(String typ, String name, String config, String params, PluginQuestionDto q, PluginMaximaCalcModeDto pluginMaximaCalcMode );

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern
     * @param   typ      Typ des Plugins
     * @param   name     Name des Plugins in der Frage
     * @param   config   Konfigurationsstring des Plugins
     * @param   params   Parameter für die Bilderzeugung
     * @param   q        Frage wo das Plugin eingebettet ist
     * @return           Base64 kodiertes Bild
     */
    ImageBase64Dto getImage(String typ, String name, String config, String params, PluginQuestionDto q);

    /**
     * Liefert eine Url auf ein Bild mit den angegebenen Parametern
     * @param   typ      Typ des Plugins
     * @param   name     Name des Plugins in der Frage
     * @param   config   Konfigurationsstring des Plugins
     * @param   params   Parameter für die Bilderzeugung
     * @param   q        Frage wo das Plugin eingebettet ist
     * @return           Url auf das erstellte Bild
     */
    ImageUrlDto getImageUrl(String typ, String name, String config, String params, PluginQuestionDto q);

    /**
     * Liefert eine Liste aller möglichen Varianten von Bildern
     * Element 0 : beschreibender Text
     * Element 1 : PIG Tag
     * Element 2 : Hilfetext
     * @param   typ      Typ des Plugins
     * @param   name     Name des Plugins in der Frage
     * @param   config   Konfigurationsstring des Plugins
     * @return  Liefert eine Liste aller möglichen Varianten von Bildern
     */
    Vector<String[]> getImageTemplates(String typ, String name, String config);

    /**
     * Wird verwendet wenn im Lösungsfeld die Funktion plugin("pluginname",p1,p2,p3) verwendet wird
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param vars   Alle Variablen der Frage
     * @param cp     Berechnungsparameter
     * @param p      Liste von CalcErgebnis-Werten, welche an das Plugin von der Question aus übergeben werden können
     * @return       Ergebnis der Funktion
     */
    //public CalcErgebnis parserPlugin(VarHash vars, CalcParams cp, CalcErgebnis ... p);
    CalcErgebnisDto parserPlugin(String typ, String name, String config, VarHashDto vars, CalcParamsDto cp, CalcErgebnisDto ... p);

    /**
     * Bestimmt die Recheneinheit, welche bei der Methode parserPlugin als Ergebnis herauskomment wenn die Parameter die Einheiten wie in der Liste p haben
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param p Einheiten der Parameter als Recheneinheiten
     * @return  Recheneinheit des Ergebnisses
     */
    String parserPluginEinheit(String typ, String name, String config, String ... p);

    /**
     * Prüft die Eingabe eines Schülers
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param pluginDto    PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto           Antwort des Schülers
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    Bewertung
     */
    PluginScoreInfoDto score(String typ, String name, String config, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade);

    /**
     * Liefert eine Liste aller Variablen welche als Dataset benötigt werden.
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @return Liste aller Variablen des Plugins
     */
    Vector<String> getVars(String typ, String name, String config);

    /**
     * verändert einen Angabetext, der in der Angabe in PI Tags eingeschlossen wurde<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text  Text der innerhalb der PI Tags gestanden ist
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter Text
     */
    String modifyAngabe(String typ, String name, String config, String text, PluginQuestionDto q);

    /**
     * verändert den kompletten Angabetext der Frage. Dieser muss als Parameter übergeben werden!<br>
     * Die Funktion wird vor dem Darstellen der Frage ausgeführt.
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param text  Angabetext der Frage
     * @param q     Frage innerhalb der, der Text sich befindet, die Frage sollte vom Plugin nicht verändert werden!!
     * @return      veränderter AngabeText
     */
    String modifyAngabeTextkomplett(String typ, String name, String config, String text, PluginQuestionDto q);

    /**
     * Passt die Plugindefinition an die Eingabe aus dem Javascipt-Result an. zB: Interaktive Karte
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param pluginDef	akt. Plugin-Definition
     * @param jsResult	Rückgabe von Javascript
     * @return	aktualiesierte Plugindefinition
     */
    String updatePluginstringJavascript(String typ, String name, String config, String pluginDef, String jsResult);

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params        Plugin-Parameter
     * @param q             Question, in die das Plugin eingebettet ist
     * @param nr            Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return              PluginDto
     */
    PluginDto loadPluginDto(String typ, String name, String config, String params, PluginQuestionDto q, int nr);

    /**
     * Rendert ein Plugins für den Fragedruck als Latex-Sourcode
     * @param typ       Typ des Plugins
     * @param name      Name des Plugins in der Frage
     * @param config    Konfigurationsstring des Plugins
     * @param pluginDto Das PluginDto welches am Webserver an das Java-Script des Plugins zum Rendern gesandt wird
     * @param answer    Inhalt des Antwortfeldes welches der Schüler eingegeben hat
     * @param mode      Druckmode
     * @return          Latexsourcode und zugehörige Bilder in einer Hashmap
     */
    PluginRenderDto renderLatex(String typ, String name, String config, PluginDto pluginDto, String answer, String mode);

    /**
     * Rendert das Plugin inklusive der Schülereingabe und korrekter Lösung<br>
     * Es wird dabei entweder direkt ein HTML-Code oder LaTeX-Code erzeugt<br>
     * @param typ                 Typ des Plugins
     * @param name                Name des Plugins in der Frage
     * @param config              Konfigurationsstring des Plugins
     * @param tex                 true für LaTeX-Code, false für html-Code
     * @param pluginDto           PluginDto welches für die Java-Script aktive Eingabe aufbereitet wurde
     * @param antwort             Antwort die der Schüler eingegeben hat
     * @param toleranz            Toleranz für die Lösung
     * @param varsQuestion        Referenz auf VarHash, wird dynamisch nachgeladen
     * @param answerDto           Antwort des Schülers
     * @param grade               Maximale Punktanzahl für die richtige Antwort
     * @return                    HTML-Code oder LaTeX-Code mit Bildern
     */
    PluginRenderDto renderPluginResult(String typ, String name, String config, boolean tex, PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto answerDto, double grade);

    /**
     * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
     * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
     * Konfiguration aktiviert.
     * @param typ                Typ des Plugins
     * @param name               Name des Plugins in der Frage
     * @param config             Konfigurationsstring des Plugins
     * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
     * @param timeout            maximale Gültigkeit der Konfigurations-Verbindung in Sekunden ohne Verbindungsanfragen, Notwendig um bei Verbindungsabbruch die Daten am Plugin-Service auch wieder zu löschen
     * @return                   alle notwendigen Konfig
     */
    PluginConfigurationInfoDto configurationInfo(String typ, String name, String config, String configurationID, long timeout);

    /**
     * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
     * @param typ                Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID (muss am Plugin-Service zuvor angelegt worden sein  mit configurationInfo)
     * @param configuration   aktueller Konfigurations-String des Plugins
     * @param questionDto     Question-DTO mit Varhashes
     * @return                Liefert die Daten welche an JS weitergeleitet werden.
     */
    PluginConfigDto setConfigurationData(String typ, String configurationID, String configuration, PluginQuestionDto questionDto);

    /**
     * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
     * @param typ                Typ des Plugins
     * @param configurationID zu verwendende Konfigurations-ID
     * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
     */
    String getConfiguration(String typ, String configurationID);

}
