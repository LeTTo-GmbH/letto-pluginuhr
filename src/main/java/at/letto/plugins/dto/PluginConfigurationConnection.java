package at.letto.plugins.dto;

import at.letto.plugins.interfaces.PluginService;
import at.letto.tools.Datum;
import at.letto.tools.RandomString;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class PluginConfigurationConnection {
    /** Konfigurations ID */
    public final String configurationID;
    /** Typ des Plugins */
    public final String typ;
    /** Name des Plugins */
    public final String name;
    /** Configurationsstring des Plugins */
    public String config;
    /** Plugin das gerade bearbeitet wird */
    public PluginService pluginService;
    /** Plugin Configurations-Information */
    public PluginConfigurationInfoDto pluginConfigurationInfoDto;
    /** PluginConfigDto welches aktuell gültig ist */
    public PluginConfigDto pluginConfigDto;
    /** PluginQuestionDto der Frage welche zu dem Plugin gehört */
    public PluginQuestionDto pluginQuestionDto;
    /** Gültigkeitsdauer in Sekunden */
    public long timeout;
    /** Zugriffszeitstempel des letzten Zugriffes als DateInteger in Sekunden */
    public long lastTime = Datum.nowDateInteger();

    public PluginConfigurationConnection(String typ, String name, String config, String configurationID, PluginService pluginService, long timeout) {
        this.typ    = typ;
        this.name   = name;
        if (configurationID!=null && configurationID.trim().length()>0) {
            this.configurationID = configurationID;
        } else {
            this.configurationID = RandomString.generateRandomString(30);
        }
        this.timeout=timeout;
        this.pluginConfigDto = new PluginConfigDto(
                typ,
                name,
                config,
                "plugintag",
                800,600,  // Größe des Editier-Fensters
                configurationID,
                "",// error-Message
                new PluginDto(),
                "",
                "",
                new HashMap<>(),      // Daten welche für LeTTo sichtbar sind
                ""                    // json mit Daten welche von LeTTo nicht ausgewertet werden sollen
        );
        changeConfig(config, pluginService);
    }

    public void changeConfig(String config, PluginService pluginService) {
        this.config                     = config;
        this.pluginService              = pluginService;
        this.pluginConfigurationInfoDto = pluginService.configurationInfo(configurationID);
    }
}
