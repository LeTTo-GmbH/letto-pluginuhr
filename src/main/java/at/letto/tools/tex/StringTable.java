package at.letto.tools.tex;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eine Tabelle welche aus lauter Strings besteht und dynamisch erweiterbar ist<br>
 * Wird hautpsächlich verwendet, um eine Tabelle in Tex sinnvoll ausgeben zu können<br>
 * @author damboeck
 *
 */
public class StringTable {

	/**
	 * Tabelle als zweidimensionaler Vektor <br>
	 * tab.get(zeile).get(spalte)<br>
	 */
	public Vector<Vector<TableItem>> tabelle;	
	/** Kopfdefinition für eine Tex-Tabelle */
	public Vector<String> spalten;	
	/** Definition des Zeilenendes für die Zeilen */
	public Vector<String> zeilen;
	/** Anzahl der Zeilen am Tabellenkopf, die auf jeder Seite gedruckt werden sollen */
	public int kopfzeilen = 0;
	/** Anzahl der Zeilen am Tabellenfuss, die auf jeder Seite gedruckt werden sollen */
	public int fusszeilen = 0;
	/** Anzahl der Spalten auf der linken Seite, die auf jeder Seite gedruckt werden sollen */
	public int fixLinks   = 0;
	/** Anzahl der Spalten auf der rechten Seite, die auf jeder Seite gedruckt werden sollen */
	public int fixRechts  = 0;
	/** Maximale Anzahl von Spalten welche zusätzlich zu den fixierten Spalten auf ein Blatt gedruckt werden sollen */
	public int anzSpalten = 15;
	/** Maximale Anzahl von Zeilen welche zusätzlich zu den fixierten Zeilen auf ein Blatt gedruckt werden sollen */
	public int anzZeilen  = 48;
	/** Drucken des Tabellen-Gitters	 */
	public boolean showGrid = true;
	
	/** Erzeugt eine leere Tabelle, welche für die Tex-Druck verwendet werden kann */
	public StringTable() {
		tabelle   = new Vector<Vector<TableItem>>();
		spalten   = new Vector<String>();
		zeilen    = new Vector<String>();
		kopfzeilen=0;
		fusszeilen=0;
		fixLinks  =0;
		fixRechts =0;
	}
	
	/** @return Liefert die Anzahl der Spalten der Tabelle */
	public int getSpalten() { return spalten.size(); }
	/** @return Liefert die Anzahl der Zeilen der Tabelle */
	public int getZeilen() { return tabelle.size(); }
	
	/** @param mode Mode
	 * Fügt eine Spalte ganz links in die Tabelle ein  **/
	public void insertSpalteLinks(String mode) { insertSpalteAt(0,mode);}
	/** @param mode Mode
	 * Fügt eine Spalte ganz rechts in die Tabelle ein **/
	public void insertSpalteRechts( String mode) { insertSpalteAt(getSpalten(),mode); }
	/** Fügt eine Spalte an der gegebenen Position in die Tabelle ein
	 * @param pos  Position
	 * @param mode Mode **/
	public void insertSpalteAt(int pos, String mode) {
		spalten.insertElementAt(mode, pos);
		for (Vector<TableItem> z:tabelle) {
			z.insertElementAt(new TableItemString(""), pos);
		}
	}
	/** @return Fügt eine Zeile am Tabellenende an und gibt eine Referenz darauf zurück */
	public Vector<TableItem> addZeile() {
		Vector<TableItem> z = new Vector<TableItem>();
		for (@SuppressWarnings("unused") String f:spalten) z.add(new TableItemString(""));
		tabelle.add(z);
		zeilen.add("");
		return z;
	}
	
	/**
	 * Fügt einen Tex-String in die Tabelle an die Position zeile/Spalte ein<br>
	 * Ist die Tabelle zu klein, wird sie automatisch vergrößert, hierbei wird die Spaltenformatierung aus der, am weitesten links liegenden Spalte übernommen<br> 
	 * @param zeile  Zeile 
	 * @param spalte Spalte
	 * @param inhalt Inhalt des Tabellenelements als Tex-formatierter String
	 * @return Refernz auf das Element, welches hinzugefügt wurde
	 */
	public TableItem put(int zeile, int spalte, TableItem inhalt) {
		if (getSpalten()==0) insertSpalteRechts("l");
		String w = inhalt.getWidth();
		if (w.length()==0) w = spalten.get(spalten.size()-1);
		while (spalten.size()<=spalte) insertSpalteRechts(w);
		while (getZeilen()<=zeile) addZeile();
		TableItem ret = inhalt;
		tabelle.get(zeile).set(spalte, ret);
		return ret;
	}
	
	/**
	 * Fügt einen Tex-String in die Tabelle an die Position zeile/Spalte ein<br>
	 * Ist die Tabelle zu klein, wird sie automatisch vergrößert, hierbei wird die Spaltenformatierung aus der, am weitesten links liegenden Spalte übernommen<br> 
	 * @param zeile  Zeile 
	 * @param spalte Spalte
	 * @param inhalt Inhalt des Tabellenelements als Tex-formatierter String
	 * @return Refernz auf das Element, welches hinzugefügt wurde
	 */
	public TableItem put(int zeile, int spalte, String inhalt) {
		return put(zeile,spalte,new TableItemString(inhalt));
	}
	
	/**
	 * Liefert das Tabellenelement an der gegebenen Position
	 * @param zeile   Zeile
	 * @param spalte  Spalte
	 * @return        Tabelleninhalt
	 */
	public TableItem get(int zeile, int spalte) {
		return tabelle.get(zeile).get(spalte);
	}
	
	/**
	 * Druckt die Tabelle als Tex-Code für maximal anz Spalten und Zeilen ab der Zelle (zeile,spalte)<br>
	 * Wiederholungszeilen und Widerholungsspalten werden immer gedruckt!! 
	 * @param zeile Zeile ab der gedruckt wird
	 * @param spalte Spalte ab der gedruckt wird
	 * @param width Anzahl von Spalten ausser der fixierten Spalten die gedruckt werden
	 * @param width Anzahl von Zeilen ausser der fixierten Zeilen die gedruckt werden
	 * @return Tex-Code
	 */
	private String texSubTable(int zeile, int spalte, int width, int height) {
		Vector<Integer> dz = new Vector<Integer>(); // zu druckende Zeilen
		Vector<Integer> ds = new Vector<Integer>(); // zu druckende Spalten
		
		for (int i=0;i<fixLinks;i++) ds.add(i);
		for (int i=spalte;i<spalte+width && i<getSpalten()-fixRechts;i++) ds.add(i);
		for (int i=getSpalten()-fixRechts;i<getSpalten();i++) ds.add(i);
		
		for (int i=0;i<kopfzeilen;i++) dz.add(i);
		for (int i=zeile;i<zeile+height && i<getZeilen()-fusszeilen;i++) dz.add(i);
		for (int i=getZeilen()-fusszeilen;i<getZeilen();i++) dz.add(i);
		
		String ret="";
		String line = "";
		
		for (int s:ds) {
			String sp=spalten.get(s);
			if (showGrid) {
				if (line.length()==0) line = "|";
			}
			line += sp;
			if (showGrid) line += "|";
		}
		// Tabelle zeichnen
		ret += "\n\\setlength{\\tabcolsep}{1pt}\n";
		ret += "\\begin{tabular}{"+line+"}\n";
		if (showGrid) ret += "\\hline";
				
		for (int z:dz) {	
			Vector<TableItem> tz = tabelle.get(z);
			line = null;		
			int sv = 0;
			for (int vs=0;vs<ds.size() && sv<ds.size();vs++) {
				int s = ds.get(vs);
				TableItem sp = tz.get(s);
				try {
					sp = sp.clone();
				} catch (CloneNotSupportedException e1) { }
				sp.showGrid = this.showGrid;
				if (vs==0) sp.firstColumn=true; else sp.firstColumn=false;
				if (sp.colspan+sv>ds.size()) sp.colspan = ds.size()-sv;
				if (sp.colspan>1) {
					// Berechne der Breite von mehreren Spalten
					double w =0;
					String e ="mm";
					String pf="";
					String sf="";
					Matcher m;
					for (int y=sv;y<sv+sp.colspan;y++) {
						if (y<this.spalten.size()) {
							if ((m=pWidth.matcher(this.spalten.get(y))).find()) {
								if (pf.length()==0) pf=m.group("pf");
								if (sf.length()==0) sf=m.group("sf");
								e = m.group("e");
								w += Double.parseDouble(m.group("w"));
							}							
						} 
					}
					sp.width = pf+w+e+sf;
				} else sp.width = this.spalten.get(s);
				String sx = sp.toTex();
				if (line==null) line = sx;
				else line += " & " + sx;				
				for (int z1=z;z1<z+sp.rowspan-1;z1++)
					for (int s1=s;s1<s+sp.colspan;s1++)
						if (z1<tabelle.size() && s1<tabelle.get(z1).size())
							tabelle.get(z1).get(s1).cline=false;
				sv += (sp.colspan>1)?(sp.colspan-1):0;
				sv++;				
			}
			line += "\\\\"+zeilen.get(z);
			if (showGrid) {
				for (int s=0;s<ds.size();s++)
					if (tz.get(ds.get(s)).cline) 
						line += " \\cline{"+(s+1)+"-"+(s+1)+"}";
			}
			ret += line;			
		}
		ret += "\\end{tabular}"+Tex.LF;	
		return ret;
	}
	
	private static final Pattern pWidth = Pattern.compile("^(?<pf>.*\\{)(?<w>(\\d+(\\.\\d+)?|\\.\\d+))(?<e>(cm|mm|in|pt|pc|bp|dd|cc|sp|em|ex))(?<sf>\\}.*)$");	
	/** @return erzeugt einen Tex-Code, welcher die Tabelle darstellt */
	public String toTex() {
		String ret="";
		for (int vonS=fixLinks;vonS<getSpalten()-fixRechts;vonS+=anzSpalten) {
			for (int vonZ=kopfzeilen;vonZ<getZeilen()-fusszeilen; vonZ+=anzZeilen) {
				if (ret.length()>0) ret+=Tex.FF;
				ret += "{\\sffamily "; 
				ret += texSubTable(vonZ, vonS, anzSpalten, anzZeilen);
				ret += "}";
			}
		}
		return ret;
	}
	
	@Override 
	public String toString() {
		String ret ="[";
		for (int z=0;z<tabelle.size();z++) {
			if (z>0) ret += ",";
			ret+="[";
			for (int s=0;s<tabelle.get(z).size();s++) {
				if (s>0) ret += ",";
				ret += tabelle.get(z).get(s).toString();
			}
			ret+="]";
		}
		ret+="]";
		return ret;
	}
	
}
