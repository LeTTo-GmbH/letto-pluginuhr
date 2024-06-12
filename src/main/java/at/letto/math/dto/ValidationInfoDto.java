package at.letto.math.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Informationen zum Ergebnisdatentyp für die Validierung der Schülereingaben
 * und zum Parsen der Schülerantwort
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ValidationInfoDto {

    /** Ergebnis ist ein Vektor */
    private boolean calcVector;

    /** Ergebnis ist eine Matrix */
    private boolean calcMatrix;

    /** Ergebnis ist eine Zahl */
    private boolean calcNumber;

    /** Ergebnis ist eine physikale Größe */
    private boolean calcPhysical;

    /** Ergebnis ist symbolisch */
    private boolean calcSymbol;

    /** Ergebnis ist ein String */
    private boolean calcString;

    /** Ergebnis ist ein long-Zahlenwert */
    private boolean calcLong;

    /** Ergebnis ist eine rationale Zahl */
    private boolean calcRational;

    /** Ergebnis ist eine Double Zahl */
    private boolean calcDouble;

    /** Ergebnis ist eine Double Zahl mit Einheit */
    private boolean calcDoubleEinheit;

    /** Ergebnis ist eine komplexe Zahl */
    private boolean calcComplex;

    /** Ergebnis ist eine komplexe Zahl mit Einheit */
    private boolean calcComplexEinheit;

    /** Ergebnis ist boolsch */
    private boolean calcBoolean;

    /** Bei der Ergebnis-Berechnung trat ein Fehler auf */
    private boolean calcError;

    /** Ergebnis ist ein Polynom */
    private boolean calcPolynom;

    /** Ergebnis ist rein numerisch */
    private boolean numeric;

    /** Wenn das Ergebnis ein Polynom ist, der Name der Polynom-Variable */
    String  polynomVarName       = "";

    /** Wenn das Ergebnis ein Polynom ist die Recheneinheit der Polynom-Variable */
    String  polynomRechenEinheit = "1";
}
