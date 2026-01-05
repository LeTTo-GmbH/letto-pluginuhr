package at.letto.plugins.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Daten die von einer SubQuestion an ein Plugin übergeben werden sollten
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class PluginSubQuestionDto implements Cloneable {
    /** Name der SubQuestion */
    private String name;
    /** die erreichbare Punkteanzahl einer Teilfrage */
    private double points;
}
