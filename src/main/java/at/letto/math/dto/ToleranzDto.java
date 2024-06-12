package at.letto.math.dto;

import at.letto.math.enums.TOLERANZMODE;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToleranzDto implements Cloneable {

    /** Wert der Toleranz als Absolutwert, oder als relativer Wert (bei 1Prozent ist toleranz=0.01) */
    private double toleranz;

    /** RELATIV : Die Toleranz wird relativ zum größeren Wert des Vergleiches bestimmt<br>
     *  ABSOLUT : Die Toleranz wird absolut angegeben und ist von den Werten unabhängig  */
    private TOLERANZMODE mode;

    public ToleranzDto() {
        this.toleranz = 1e-10;
        this.mode = TOLERANZMODE.RELATIV;
    }

    public boolean relativ() {
        return this.mode == TOLERANZMODE.RELATIV;
    }

    public boolean absolut() {
        return this.mode == TOLERANZMODE.ABSOLUT;
    }

}
