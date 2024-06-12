package at.letto.tools.enums;

import at.letto.ServerConfiguration;
import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Vector;


/**
 * Definiert Auswahlmöglichkeiten für die Einstellungen 
 * der Schwierigkeit der frage
 * @author Thomas Mayer
 *
 */
public enum Schwierigkeit implements Selectable {
	Normal(null),
	Streng(null),
	Mild(null),
	HundertProz(null);
	/**
	 * Fragetyp, für den Auswahl bereitgestellt wird,
	 * null bedeutet dass Auswahl für alle Typen gültig ist
	 */
	private SQMODE qTyp; // Fragetyp, für den Auswahl bereitgestellt wird
	/**
	 * Konstructor 
	 * @param typ	Typ der Subquestion oder null
	 */
	Schwierigkeit(SQMODE typ) {
	    qTyp = typ;
	}

	public static Schwierigkeit getType(int ordinal) {
		return Schwierigkeit.values()[ordinal];
	}
	/**
	 * Liefert die volle Bezeichnung für den Fragetyp
	 * @return	Bezeichnung des Fragetyps
	 */
	public String getName() {
		return ServerConfiguration.service.Res(this.toString());
	}

	@JsonIgnore
	@Override
	public String getText() {
		return this.toString().replace("_", " ");
	}

	@JsonIgnore
	@Override
	public int getId() {
		return this.ordinal();
	}
	/**
	 * Liefert alle enum-Einträge, die für einen Fragetyp passen
	 * @param typ	Fragetyp
	 * @return		Liste mit allen passenden ENUM-Werten
	 */
	public static List<Schwierigkeit> getSchwierigkeiten(SQMODE typ) {
		Vector<Schwierigkeit> ret = new Vector<Schwierigkeit>();
		for (Schwierigkeit a : Schwierigkeit.values()) 
			if (a.qTyp==null || a.qTyp.equals(typ))
				ret.add(a);
		return ret;		
	}

}
