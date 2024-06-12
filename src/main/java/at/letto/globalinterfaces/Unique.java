package at.letto.globalinterfaces;

/**
 * Interface für Entities
 * @author Thomas
 *
 */
public interface Unique extends IdEntity {
	
	/**
	 * @return String mit einer unique-Bezeichnung, die nur einmal in DB vorkommen darf
	 */
	public String key();
}
