package at.letto.plugins.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informationen über einen Dataset welcher vom Plugin angeforder werden kann
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginDatasetDto {

    /** Variablenname */
    private String name;

    /** Datensatzdefinition mit Zahlenbereichsdefinition */
    private String bereich;

    /** Zieleinheit des Datensatzes */
    private String einheit;

    /** Bei true werden die Standardwerte der übergeordneten Kategorie verwendet und nicht der angegebene Bereich */
    private boolean useTemplate;

}
