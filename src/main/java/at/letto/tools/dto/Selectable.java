package at.letto.tools.dto;

/**
 * Interface für die SelectBox
 * @author Thomas
 *
 */
public interface Selectable {
	
	/**
	 * Liefert den Inhalt für die Anzeige der Selectbox
	 * @return Liefert den Inhalt für die Anzeige der Selectbox 
	 */
	public abstract String getText();
	
	/**
	 * Klassen, die Selectable implementieren, müssen eine getId-Methode haben
	 * wird für generellen Converter verwendet
	 * @return id des Objekts aus der Datenbank
	 */
	public int getId();


}
