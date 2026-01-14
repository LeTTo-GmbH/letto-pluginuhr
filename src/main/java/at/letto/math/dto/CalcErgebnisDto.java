package at.letto.math.dto;

import at.letto.math.enums.CALCERGEBNISTYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Speichert ein CalcErgebnis als Dto um über eine Rest- oder Service-Schittstelle zu übertragen.<br>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcErgebnisDto implements Cloneable {

    /** CalcErgebnis als normaler lesbarer String, welcher noch nicht geparst ist */
    private String       string;

    /** JSON-String des CalcErgebnis-Ausdrucks */
    private String       json;

    /** Typ, wenn nicht über json übertragen werden kann */
    private CALCERGEBNISTYPE type;

    @Override
    public String toString() {
        switch (type) {
            case CALCULATE -> {
               return json;
            }
            case STRING -> {
                return string;
            }
            case ERROR -> {
                return "error";
            }
        }
        return "null";
    }

}
