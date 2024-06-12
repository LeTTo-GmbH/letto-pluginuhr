package at.letto.tools.enums;

import at.letto.ServerConfiguration;
import at.letto.tools.dto.Selectable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Vector;


/**
 * Definiert Auswahlmöglichkeiten für die
 * Definition des Anzeigeverhaltens zum Mischen der Antworten
 * @author Thomas Mayer
 *
 */
public enum AntwortenMischen implements Selectable {
	 Nicht_mischen(null),
	 Mischen(null),
	 Nur_links_mischen(SQMODE.ZUORDNUNG),
	 Nur_rechts_mischen(SQMODE.ZUORDNUNG),
	 
	 Normal(SQMODE.IMAGE),
	 Streng(SQMODE.IMAGE),
	 Mild(SQMODE.IMAGE);
	/**
	 * Fragetyp, für den Auswahl bereitgestellt wird,
	 * null bedeutet dass Auswahl für alle Typen gültig ist
	 */
	private SQMODE qTyp;  
	/**
	 * Konstructor 
	 * @param typ	Typ der Subquestion oder null
	 */
	AntwortenMischen(SQMODE typ) {
	    qTyp = typ;
	}

	public static AntwortenMischen getType(int ordinal) {
		return AntwortenMischen.values()[ordinal];
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
	public static List<AntwortenMischen> getAntwortenMischen(SQMODE typ) {
		Vector<AntwortenMischen> ret = new Vector<AntwortenMischen>();
		for (AntwortenMischen a : AntwortenMischen.values()) 
			if (a.qTyp==null || a.qTyp.equals(typ))
				ret.add(a);
		return ret;		
	}

}
