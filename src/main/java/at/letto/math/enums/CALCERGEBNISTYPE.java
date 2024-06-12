package at.letto.math.enums;

/**
 * Mögliche Typen für CalcErgebnisDto bei reiner String-Übertragung
 */
public enum CALCERGEBNISTYPE {

    /** Wird als mathematischer Ausdruck geparst */
    CALCULATE,

    /** Reiner String, welcher nicht geparst wird */
    STRING,

    /** Fehlermeldung welche nicht geparst wird */
    ERROR

}
