package at.letto.tools.html;

/**
 * Mode für die Verarbeitung von berechnenden und Formelelementen
 * TEXT           normaler Text<br>
 * FORMEL2DOLLAR  Formeln welche innerhalb von doppelten Dollarzeichen stehen<br>
 * FORMEL         Formeln welche innerhalb von einfachen Dollarzeichen stehen<br>
 * BERECHNEND     Berechnende Elemente innerhalb {= }<br>
 * @author Werner
 *
 */
public enum FMODE {
    TEXT, FORMEL2DOLLAR, FORMEL, BERECHNEND
}
