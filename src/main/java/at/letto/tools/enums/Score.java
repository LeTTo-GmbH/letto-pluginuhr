package at.letto.tools.enums;

import at.letto.tools.tex.Tex;

/**
 * Ergebnis einer Fragen-Beurteilung<br>
 * NotScored : nicht bewertet, da das richtige Ergebnis nicht bekannt ist. zB bei Freitextfragen <br>
 * OK        : Antwort ist richtig  <br>
 * FALSCH    : Antwort ist falsch <br>
 * TEILWEISE_OK        : Antwort ist teilweise richtig  <br>
 * EINHEITENFEHLER     : Antwort hat einen Einheitenfehler <br>
 * OK_Lehrer           : Das Ergebnis wurde vom Lehrer händisch als richtig beurteilt <br>
 * FALSCH_Lehrer       : Das Ergebnis wurde vom Lehrer händisch als falsch beurteilt  <br>
 * TEILWEISE_OK_Lehrer : Das Ergebnis wurde vom Lehrer händisch als teilweise richtig beurteilt  <br>
 * EINHEITENFEHLER_Lehrer : Im Ergebnis der Beispielangabe ist ein Einheitenfehler  <br>
 * ANGABEFEHLER_EH        : Das Ergebnis der Beispielangabe kann wegen eines Rechenfehlers nicht berechnet werden <br>
 * PARSERFEHLER_SYSTEM    : Der Parser kann das richtige Ergebnis nicht berechnen <br>
 * NichtEntschieden       : Calculated: die Bewertung ist noch nicht entschieden, da das Ergebnis nicht gepasst hat. Sind alle Answers einer Frage nicht entschieden, so ist die Antwort falsch <br>
 * <br>
 *  
 * @author L-DAMB
 *
 */
public enum Score implements Cloneable {
	// Neue Elemente nur HINTEN anreihen !!!
	
	/** nicht bewertet, da das richtige Ergebnis nicht bekannt ist. zB bei Freitextfragen */ 
	NotScored,
	/** Antwort ist richtig */
	OK,
	/** Antwort ist falsch*/
	FALSCH,
	/** Antwort ist teilweise richtig */
	TEILWEISE_OK,
	/** Antwort hat einen Einheitenfehler */
	EINHEITENFEHLER,
	/** Das Ergebnis wurde vom Lehrer händisch als richtig beurteilt */
	OK_Lehrer,
	/** Das Ergebnis wurde vom Lehrer händisch als falsch beurteilt */
	FALSCH_Lehrer,
	/** Das Ergebnis wurde vom Lehrer händisch als teilweise richtig beurteilt */
	TEILWEISE_OK_Lehrer,
	/** Im Ergebnis der Beispielangabe ist ein Einheitenfehler */
	EINHEITENFEHLER_Lehrer,
	/** Das Ergebnis der Beispielangabe kann wegen eines Rechenfehlers nicht berechnet werden */
	ANGABEFEHLER_EH,
	/** Der Parser kann das richtige Ergebnis nicht berechnen */
	PARSERFEHLER_SYSTEM,
	/** Calculated: die Bewertung ist noch nicht entschieden, da das Ergebnis nicht gepasst hat. Sind alle Answers einer Frage nicht entschieden, so ist die Antwort falsch */
	NichtEntschieden,
	/** Mehrfachbeantwortung der Frage, Antwort richtig */
	MEHRFACHANTWORT_OK,
	/** Mehrfachbeantwortung der Frage, Antwort richtig, vom Lehrer definiert */
	MEHRFACHANTWORT_OK_LEHRER,
	/** Mehrfachbeantwortung der Frage, Antwort tw. richtig */
	MEHRFACHANTWORT_TW_RICHTIG,
	/** Mehrfachbeantwortung der Frage, Antwort tw. richtig, Lehrerbeurteilung */
	MEHRFACHANTWORT_TW_RICHTIG_LEHRER;
	
	public String getScoreTexColor() {
		switch (this) {
		case ANGABEFEHLER_EH:
		case EINHEITENFEHLER_Lehrer:
		case FALSCH_Lehrer:
		case PARSERFEHLER_SYSTEM:
			return Tex.ColorAntwortAngabeFehler;
		case TEILWEISE_OK:
		case TEILWEISE_OK_Lehrer:
		case EINHEITENFEHLER:
		case MEHRFACHANTWORT_TW_RICHTIG:
		case MEHRFACHANTWORT_TW_RICHTIG_LEHRER:
			return Tex.ColorAntwortTeilrichtig;
		case FALSCH:
			return Tex.ColorAntwortFalsch;
		case OK:
		case OK_Lehrer:
		case MEHRFACHANTWORT_OK:
		case MEHRFACHANTWORT_OK_LEHRER:
			return Tex.ColorAntwortRichtig;		
		case NotScored:
		case NichtEntschieden:
			return Tex.ColorAntwortNichtentschieden;
		}
		return "";
	}
}
