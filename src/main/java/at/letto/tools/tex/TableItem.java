package at.letto.tools.tex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface für ein Element welches in einer Tabelle aufgenommen werden kann, und in Tex gedruckt werden kann<br> 
 * @author damboeck
 *
 */
public abstract class TableItem implements Cloneable{
	
	/** Zelle überspannte colspan Spalten, Standardwert = 1 */
	public int colspan=1;

	/** Zelle überspannt rowspan Zeilen, Standardwert = 1 */
	public int rowspan=1;
	
	/** Der Inhalt wird in eine Spalte gepresst, ohne einen Zeilenvorschub */
	public boolean singleLine=true;
	
	/** Breite des Eintrages als Tabellen-spalten-Definition wie p{3cm} */
	protected String width;
	
	/** true wenn die Linien unterhalb des Tabellenelements gezeichnet werden soll */
	public boolean cline=true;
	
	/** true wenn die Rahmenlinien gezeichnet werden sollen */
	protected boolean showGrid=true;
	
	/** True wenn es sich um die erste Spalte einer Tabelle handelt */
	protected boolean firstColumn=false;
	
	/** Tex-Code für die Farbe */
	public String color="";
	
	/** Fett-Schrift */
	public boolean fett=false;
	
	/** Eintrag zentriert*/
	public boolean center=false;
	
	/** @return Wandelt den Inhalt in einen Tex-formatierten String */
	public abstract String toTex();
	
	/** @return Liefert die Spaltenbreite als Tabellen-spalten-Definition wie p{3cm} */
	public String getWidth() {
		return width;
	}
	
	private static final Pattern pWidth = Pattern.compile("^.*\\{(?<w>(\\d+(\\.\\d+)?|\\.\\d+)(cm|mm|in|pt|pc|bp|dd|cc|sp|em|ex))\\}.*");	
	/** @return Liefert die Spaltenbreite als String mit Tex-Einheiten wie z.B. 3cm */
	public String getTexWidth() {
		Matcher m;
		if (width==null) return "";		
		if ((m=pWidth.matcher(width)).find()) {
			return m.group("w");
		}
		return "";
	}
	
	@Override
	public TableItem clone() throws CloneNotSupportedException {
		TableItem ret = (TableItem)(super.clone());
		return ret;
	}
	
}
