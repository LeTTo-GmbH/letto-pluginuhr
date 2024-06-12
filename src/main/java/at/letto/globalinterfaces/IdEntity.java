package at.letto.globalinterfaces;

/**
 * Interface für Entities
 * @author Thomas
 *
 */
public interface IdEntity {
	
	/**
	 * Klassen, die Entities implementieren, müssen eine getId-Methode haben:
	 * ID ist der Primärschlüssel der Entity
	 * @return id des Objekts aus der Datenbank
	 */
	public int getId();

}
