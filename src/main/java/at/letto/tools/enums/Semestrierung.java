package at.letto.tools.enums;

import at.letto.ServerConfiguration;
import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Enum zur Darstellung von möglichen Optionen der Semestrierung <br>
 * Wird einer Klasse zugewiesen 
 * @author Thomas Mayer
 *
 */
public enum Semestrierung implements Selectable {
	/**
	 * Der Lehrplan für diese Klasse ist nicht semestriert
	 */
	NichtSemestriert,
	/**
	 * Diese Klasse hat einen semestrierten Lehrplan,
	 * Klassenbezeichnung im Wintersemester
	 */
	Wintersemester,
	/**
	 * Diese Klasse hat einen semestrierten Lehrlan,
	 * Klassenbezeichnung im Sommersemester
	 */
	Sommersemester,
	/**
	 * Diese Klasse hat einen semestrierten Lehrlan,
	 * die Klassenbezeichnung ist aber für das ganze Schuljahr gültig
	 */
	GanzesJahr,
	/**
	 * Zuweisung abwechselnd von Sommer und Wintersemester
	 */
	wechselnd,
	/**
	 * Laden des aktuellen Semesters
	 */
	AktuellesSemester;
	
	/**
	 * @return Check ob Sommer- oder Wintersemester
	 * 
	 */
	@JsonIgnore
	public boolean isSem() {
		return this.equals(Sommersemester) || this.equals(Wintersemester); 
	}
	/**
	 * @return Check ob Wintersemester
	 */
	@JsonIgnore
	public boolean isWinter() {
		return this.equals(Wintersemester); 
	}
	/**
	 * @return Check ob Sommersemester
	 */
	@JsonIgnore
	public boolean isSommer() {
		return this.equals(Sommersemester); 
	}
	/**
	 * @return Check ob Ganzes Jahr für diese Klasse gesetzt ist 
	 */
	@JsonIgnore
	public boolean isGanzesJahr() {
		return this.equals(GanzesJahr); 
	}
	/**
	 * @return Gibt die Anzahl an Noten in diesem Kusr zurück:<br>
	 * 1, wenn Sommer- oder Wintersemester<br>
	 * 2, wenn Ganzes Jahr oder nicht semestriert 
	 */
	@JsonIgnore
	public int getNotenAnzahl() {
		return this.equals(Semestrierung.GanzesJahr) || this.equals(Semestrierung.NichtSemestriert) ? 2 : 1;
	}

	/**
	 * @return Rückgabe der entsprechenden Suchnummer für Deskriptoren und Lehrinhalte<br>
	 * 1 für Wintersemester<br>
	 * 2 für Sommersemester<br>
	 * 0 für alles andere
	 */
	@JsonIgnore
	public int getNr() {
		switch (this) {
		case Sommersemester:
			return 2;
		case Wintersemester:
			return 1;
		case GanzesJahr:
		case NichtSemestriert:
		case wechselnd:
		case AktuellesSemester:
		}
		return 0;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	@JsonIgnore
	public String getText() {
		String resname = "Semestrierung.TEXT."+toString();
		return ServerConfiguration.service.Res(resname);
	}
	@JsonIgnore
	public String getAbk() {
		String resname = "Semestrierung.ABK."+toString();
		return ServerConfiguration.service.Res(resname);
	}
	@JsonIgnore
	@Override
	public int getId() {
		return this.ordinal();
	}
	
}
