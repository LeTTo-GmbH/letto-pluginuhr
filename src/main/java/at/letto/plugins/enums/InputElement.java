package at.letto.plugins.enums;

/**
 * Definiert das Eingabeelement für die Subquestion, deren Typ
 * auf Plugin gesetzt ist.
 * Mögliche Eingabetypen:
 * @author Thomas Mayer
 *
 */
public enum InputElement {
    /**
     * TextField für ein Textfeld
     */
    TextField,
    /**
     * TextArea für einen größeren Eingabebereich
     */
    TextArea,
    Button,
    MULTICHOICE,
    XHTML,
    /**
     * Javascript-Editor für Eingabe von Source-Code von Programmen
     */
    SourceCode,
    JAVASCRIPT
}
