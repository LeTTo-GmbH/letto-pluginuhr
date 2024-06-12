package at.letto.dto;

/**
 * Zustand der Übertragung einer Rest-Verbindung. Dieser Status wird normalerweiser vom REST-Server geliefert,
 * kann aber bei einem Übertragungsfehler auch vom Rest-Service gesetzt werden.
 */
public enum RestStatus {

    /** (STANDARD) Wenn das DTO bei einem Request verwendet wird, sollte der RestStatus auf UNDEFINED liegen */
    UNDEFINED,

    /** keine Verbindung zum Rest-Server möglich! */
    NOCONNECTION,

    /** erfolgreich ausgeführt, wird beim lesen von Daten verwendet*/
    OK,

    /** Datensatz wurde gespeichert, wird beim Schreiben von Daten verwendet */
    UPDATED,

    /** Datensatz wurde erfolgreich  gelöscht */
    DELETED,

    /** Datensatz wurde erstellt */
    CREATED,

    /** Datensatz kann nicht gespeichert werden, da eine Schlüsselverletzung eines unique-Keys vorliegt! */
    UNIQUEFAIL,

    /** Datensatz kann nicht gespeichert werden, da inkonsistente Daten vorhanden sind! */
    INKONSISTENT,

    /** Restkey Fehlerhaft oder nicht vorhanden */
    RESTKEYFAIL,

    /** keine Rechte für diese Anfrage */
    FORBIDDEN,

    /** Datensatz kann nicht gefunden werden */
    NOTFOUND;

}
