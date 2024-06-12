package at.letto.math.dto;

import at.letto.math.enums.CALCMODE;
import at.letto.math.enums.SHOWPOTENZ;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcParamsDto implements Cloneable {

    /** Art der Optimierung */
    private String optmode;
    /** Toleranz für Vergleiche */
    private ToleranzDto toleranz;
    /** Zugehörige Frage, um bei Plugins, die Plugin-Methoden verwenden zu können */
    //private PluginQuestionDto     q;
    /** Auswertung und Optimierung wird rekursiv auf alle Parameter der Funktion oder des Operators angewendet */
    private boolean 	  rekursiv;
    /** bei false werden alle numerischen Werte normal berechnet, bei true werden
     *  Ganzzahlen und mathematische Konstante (e,pi) nur dann als Zahlenwert eingesetzt,
     *  wenn sie mit einer Gleitkommazahl über einen Operator verbunden werden. */
    private boolean      symbolicMode;
    /**
     * Gibt an, ob eine Potenz als Potenz oder als Wurzel dargestellt werden soll<br>
     * AUTO : Die Darstellung wird automatisch festgelegt<br>
     * POW  : Potenzen werden als Potenz dargestellt<br>
     * SQRT : Wenn es sinnvoll ist, werden Potenten als Wurzel dargestellt<br>
     */
    private SHOWPOTENZ showpotenz;
    /**
     * Art der Berechnung<br>
     * MAXIMA  : Berechnung im Maxima-Feld einer Frage<br>
     * LOESUNG : Berechnung der Lösung aus dem Lösungsfeld<br>
     * ERGEBNIS: Vergleich des Ergebnisses des Schülers mit dem Ergebnis des Lehrers<br>
     * VIEW    : Berechnungen direkt mit Angabefeld mit {=
     */
    private CALCMODE calcmode;
    /**
     * Gibt an, ob Summen ausmultipliziert werden sollen, bzw. ob aus Summen herausgehoben werden soll
     */
    private boolean ausmultiplizieren;
    /**
     * Gibt an, ob aus Summen herausgehoben werden soll
     */
    private boolean herausheben;
    /** Datensätze welche bei der Berechnung verwendet werden können */
    //private VarHash vars;
    /** Ist dieser Parameter gesetzt, so wird ein noopt jedenfalls entfernt und optimiert */
    private boolean forceOpt;
}
