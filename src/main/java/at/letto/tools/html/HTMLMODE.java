package at.letto.tools.html;

import at.letto.tools.tex.Tex;
import at.letto.tools.tex.TexPrintContext;

/**
 * Mode des HTML-Knotens<br>
 * XML     1 Erzeugung des HTML-Baumes aus der XML-Quelle <br>
 * GUI     2 Erzeugung des HTML-Baumes aus der GUI<br>
 * HTML    3 Erzeugen eines HTML-Baumes aus der HTML-Ansicht der GUI jedoch ohne eingebettete Bilder<br>
 * XHTML   4 Ausgabe eines XHTML-Strings für den Export<br>
 * TEXN    10 Erzeugen der TeX Ausgabe nur mit der Frage<br>
 * TEXA    11 Erzeugen der TeX Ausgabe mit Frage und Antwort <br>
 * TEXF    12 Erzeugen der TeX Ausgabe mit Frage,Antwort und Formel <br>
 * TEXI    13 Erzeugen der TeX Ausgabe mit Frage und Moodle Info <br>
 * TEXS    14 Erzeugen der TeX Ausgabe mit Frage und Schülerantwort <br>
 * TEXSE   15 Erzeugen der TeX Ausgabe mit Frage und Schülerantwort und Ergebnis <br>
 * TEXEN   16 Erzeugen der Tex-Ausgabe mit Schülerantwort jedoch ohne Multiple-Choice und Zuordnungsfragen <br>
 * TEXD    17 TeX Ausgabe einer normalen HTML-Datei<br>
 * XMLLIST 18 Erzeugen einer XML Ausgabe eines Sourcecode-Listings<br>
 * TEXLIST 19 Erzeugen einer TeX Ausgabe eines Sourcecode-Listings<br>
 * XHTMLLIST 20 Erzeugen einer TeX Ausgabe eines Sourcecode-Listings<br>
 * TEXT    HTML jedoch ohne br und p tags<br>
 * FORMELXML Elemente innerhalb einer Tex-Formel für Mathjax<br>
 * FORMEL  Elemente innerhalb einer Tex-Formel mit $ $<br>
 * CALC    Elemente innerhalb eines Berechnungsausdruckes {= }
 * PLAINTEXT Erzeugung eines Textes ohne TEX und HTML-Codierung
 * @author Werner
 *
 */
public enum HTMLMODE {
    XML, GUI, HTML, XHTML, TEXN, TEXA, TEXF, TEXI, TEXS, TEXSE, TEXEN,  TEXD, XMLLIST,
    TEXLIST, XHTMLLIST, TEXT, FORMEL, FORMELXML, CALC, PLAINTEXT;
    public TexPrintContext context = null;
    public Tex.PRINTMODE getPRINTMODE() {
        switch (this) {
            case TEXN          : return Tex.PRINTMODE.PRINTFRAGE;
            case TEXA          : return Tex.PRINTMODE.PRINTERGEBNIS;
            case TEXF	       : return Tex.PRINTMODE.PRINTFORMEL;
            case TEXI	       : return Tex.PRINTMODE.PRINTINFO;
            case TEXD          : return Tex.PRINTMODE.PRINTFRAGE;
            case TEXLIST       : return Tex.PRINTMODE.PRINTFRAGE;
            case TEXS          : return Tex.PRINTMODE.PRINTANTWORT;
            case TEXSE         : return Tex.PRINTMODE.PRINTANTWORTERGEBNIS;
            case TEXEN         : return Tex.PRINTMODE.PRINTEINSICHTNAHME;
        }
        return Tex.PRINTMODE.PRINTFRAGE;
    }

    public boolean formel() {
        switch (this) {
            case FORMEL: case FORMELXML : return true;
        default: return false;
        }
    }
}
