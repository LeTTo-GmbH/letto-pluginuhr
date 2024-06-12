package at.letto.globalinterfaces;

/**
 * Interface für Objekte mit Eigenschaft name und id:
 * Klassen, die eine Namensbezeichnung enthalten, können das Interface Named implenteiern
 */
public interface Named {
	
	/**
	 * Klassen, die eine Namensbezeichnung enthalten, können das Interface Named implenteiern
	 * @return Name des Objekts
	 */
	public String getName();

	/**
	 * @return ID des Obekts
	 */
	public int getId();

}
