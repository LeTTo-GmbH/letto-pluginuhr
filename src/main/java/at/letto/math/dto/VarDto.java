package at.letto.math.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ein Eintrag eines VarHashes für die Übertragung in einem Rest-Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarDto implements Cloneable {

    /** Ausdruck, bzw. Wert der Variablen als String oder Json in einem CalcErgebnisDto */
    private CalcErgebnisDto calcErgebnisDto;
    /** ZielEinheit als String ungeparst*/
    private String          ze;
    /** CalcParams */
    private CalcParamsDto   cp;

    @Override
    public String toString() {
        return calcErgebnisDto.toString();
    }

}
