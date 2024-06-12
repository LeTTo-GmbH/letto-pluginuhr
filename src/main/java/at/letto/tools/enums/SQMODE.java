package at.letto.tools.enums;

import at.letto.ServerConfiguration;
import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Art der Subquestion <br>
 * NONE           nicht definiert<br>
 * SHORTANSWER    Textantwort<br>
 * NUMERICAL      Numerischer Wert ohne Berechnung<br>
 * CALCULATED     Berechneter Wert<br>
 * BOOLSCH        Bei Lückentextfragen für die Bewertung der Eingaben, für diese Subquestion darf es keine Schülereingabe geben, sondern hier werden alle anderen Eingaben bewertet.<br>
 * PLUGIN         Die Lösung wird nicht durch den Letto-Kern beurteilt, sondern durch das Plugin direkt. Im Maximfeld der Subquestion steht der Name des Plugins, im Lösungsfeld gibt es Parameter für die Auswertung<br>
 * FREITEXT		  Textarea one automatischer Korrektur<br>
 * ZUORDNUNG	  Zuordnungsfrage<br>
 * IMAGE		  Abgabe in Form eines Images (Zwischenablage oder Foto)<br>
 * SCHIEBER		  Abgabe in Form eines Schiebers für Umfragen<br>
 * MULTIPLECALC   Berechneter Wert mit mehreren Eingabefeldern<br>
 * MULTIPLETEXT   Für jede Antwort wird ein Engabefeld eingeblendet
 * @author damboeck
 *
 */
public enum SQMODE implements Selectable {
	SINGLECHOICE("Auswahl"),
	TEXT("Text"),
	REGEXP("Regular Expression"),
	MULTICHOICE("Multiple Choice"),
	CALCULATED("Berechnung"),
	BOOLSCH("Boolsch"),
	PLUGIN("Plugin"),
	FREITEXT("Textfeld"),
	ZUORDNUNG("Zurordnung"),
	IMAGE("Bildabgabe"),
	SCHIEBER("Schieber"),
	MULTIPLECALC("Mehrfach-Berechnung"),
	MULTIPLETEXT("Mehrfach-Antwort");

	private String text;

	SQMODE(String Text) {
		this.text = text;
	}
	/**
	 * Erzeugt aus einem String den zugehörigen Subquestion-Mode
	 * @param s  Mode als String
	 * @return   Mode als enum, kann nicht geparst werden, so wird CALCULATED returniert
	 */
	public static SQMODE parse(String s) {
		for (SQMODE m:SQMODE.values()) {
			if (m.toString().equals(s.toUpperCase())) return m;
		}
		if (s.equalsIgnoreCase("SHORTANSWER")) return TEXT;
		return CALCULATED;
	}
	/**
	 * Erzeugt aus einem Zahlenwert den zugehörigen Subquestion-Mode
	 * @param i  Zahlenwert
	 * @return   Mode als enum, kann nicht geparst werden, so wird CALCULATED returniert
	 */
	public static SQMODE parse(int i) {
		try {
			return SQMODE.values()[i];
		} catch (Exception e){ return CALCULATED;}
	}

	public String getImage() {
		return this.toString().toLowerCase()+".gif";
	}
	public String getExplanation() {
		return ServerConfiguration.service.Res("SQMODE." + this.toString().toUpperCase());

	}
	@JsonIgnore
	@Override
	public String getText() {
		return this.toString();
	}
	@JsonIgnore
	@Override
	public int getId() {
		return this.ordinal();
	}
}
