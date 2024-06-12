package at.letto.plugins.restclient;

import at.letto.globalinterfaces.ImageService;
import at.letto.plugins.dto.PluginDto;
import at.letto.plugins.dto.PluginQuestionDto;
import at.letto.tools.JavascriptLibrary;
import at.letto.tools.dto.ImageBase64Dto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Verwaltet alle möglichen Plugins des Servers <br>
 * Kann in TomEE-war und Spring-Boot Projekten verwendet werden<br>
 *
 * Verwendung in at.letto.service.MicroServiceConfiguration
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public interface PluginManagerService extends PluginConnectionService  {

    /** @return liefert eine Liste aller Plugins (Pluginnamen), welche mit diesem Service verwaltet werden */
    List<String> getPluginList();

    /** @return liefert eine Information, ob die JavaScript- und Css Dateien in ein lokales Verzeichnis gespeichert werden */
    boolean isPublicJs();

    /** @return liefert das Verzeichnis wo die JavaScript-Dateien gespeichert werden */
    String getPublicJs();


    /** @param typ Plugin Typ
     *  @return liefert das zugehörige Connectionsservice zu einem Plugin-Typ */
    PluginConnectionService getPluginConnectionService(String typ);

    /**
     * Registriert eine PluginConnectionService im PluginManagerService
     * @param connection PluginConnection zu einem Plugin-Service
     */
    void registerPluginConnectionService(PluginConnectionService connection);

    /**
     * Liefert einen String welcher die Parameter eines Plugins exakt beschreibt um eine Prüfsumme davon zu bilden
     * @param typ     Typ des Plugins
     * @param name    Name des Plugins
     * @param config  Konfigurationsparameter-String
     * @param params  Parameter des PIG-Tags
     * @param q       Frage in der das Plugin eingebettet ist
     * @return        String als Kombination aus alle den Parametern
     */
    String getPluginImageDescription(String typ, String name, String config, String params, PluginQuestionDto q);

    /**
     * Liefert ein Base64 codiertes Bild mit den angegebenen Parametern und speichert es in einem ImageService
     * @param imageService ImageService mit dem das Bild gespeichert wird
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params Parameter für die Bilderzeugung
     * @param q      Frage wo das Plugin eingebettet ist
     * @return Base64 kodiertes Bild
     */
    ImageBase64Dto getImage(ImageService imageService, String typ, String name, String config, String params, PluginQuestionDto q);

    /**
     * Liefert eine Liste von Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Es muss die vollständige URL angegeben werden!
     * @return	für alle Plugin notwendige JS-Libraries
     */
    List<JavascriptLibrary> getJavascriptLibraries();

    /**
     * Liefert eine Liste von LOKALEN Javascript-Libraries,
     * die im Header der HTML-Seite eingebunden werden müssen.
     * Pfade werden relativ zum akt. Servernamen übergeben
     * @return	für alle Plugin notwendige JS-Libraries
     */
    List<JavascriptLibrary> getJavascriptLibrariesLocal();

    /**
     * Rendern des Plugin-Images, Aufbau eines DTOs zur späteren Javascript - Bearbeitung
     * @param imageService  Image Service
     * @param typ    Typ des Plugins
     * @param name   Name des Plugins in der Frage
     * @param config Konfigurationsstring des Plugins
     * @param params        Plugin-Parameter
     * @param q             Question, in die das Plugin eingebettet ist
     * @param nr            Laufende Nummer für alle PIG-Tags und Question-Plugins
     * @return              PluginDto
     */
    PluginDto loadPluginDto(ImageService imageService, String typ, String name, String config, String params, PluginQuestionDto q, int nr);

}
