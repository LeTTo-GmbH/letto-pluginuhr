package at.letto.plugins.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import java.util.List;

/**
 * Liefert allgemein Informationen zu einem Plugin, welche ohne Definition einer Plugin-Instanz allgemein gültig sind
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginGeneralInfoList {

    private List<PluginGeneralInfo> pluginInfos;

}
