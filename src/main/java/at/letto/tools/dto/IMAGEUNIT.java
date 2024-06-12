package at.letto.tools.dto;

/**
 * Einheiten welche für die Größenangaben von Bildern verwendet werden können <br>
 *     none : keine Angabe der Dateigröße <br>
 *     px   : Pixel <br>
 *     pt   : Punkte <br>
 *     cm   : Zentimeter <br>
 *     percent : Prozent der Bildschirmbreite <br>
 *     em   : Vielfache der Standardschriftgröße <br>
 */
public enum IMAGEUNIT {

    /** Keine Angabe der Dateigröße */
    none,

    /** Angabe in Bildschirmpixel */
    px,

    /** Angabe in Punkten */
    pt,

    /** Angabe in cm */
    cm,

    /** Angabe in Prozent */
    percent,

    /** Angabe in Vielfachen der Standardschriftgröße */
    em;

}
