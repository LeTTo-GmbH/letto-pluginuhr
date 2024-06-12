package at.letto.tools.tex;

public class TableItemString extends TableItem {

	/** String der in der Tabelle steht */
	private String s;
	
	/** Schrift senkrecht*/
	public boolean senkrecht=false;
	
	/** Höhe einer senkrechte Box in mm */
	public int height=10;
		
	/**
	 * Erzeugt eine Tabellen-Zelle mit einem String
	 * @param s String
	 */
	public TableItemString(String s) {
		this(s,"");
	}
	/**
	 * Erzeugt eine Tabellen-Zelle mit einem String und einer definierten Breite
	 * @param s      String
	 * @param width  Breite
	 */
	public TableItemString(String s,String width) {
		this.s=s;
		this.width = width;
	}
		
	@Override
	public String toTex() {
		String ret = Tex.stringToTex(s);
		//String ret = s;
		if (fett) ret = "\\textbf{"+ret+"}";
		if (color.length()>0) 
			ret = "{\\color{"+color+"}"+ret+"}";
		if (senkrecht) {
			if (ret.length()>0) {
				String w=height+"mm";
				ret = "\\setbox0\\hbox{"+ret+"}\\ifdim\\wd0>"+w+"\\resizebox{"+w+"}{\\ht0}{\\copy0}\\else\\resizebox{\\wd0}{\\ht0}{\\copy0}\\fi";
				ret = "\\rotatebox{90}{"+ret+"\\,}";
			}
		} else {	
			if (singleLine && getTexWidth().length()>0 && ret.length()>0) {
				String w = getTexWidth();
				ret = "\\setbox0\\hbox{"+ret+"}\\ifdim\\wd0>"+w+"\\resizebox{"+w+"}{\\ht0}{\\copy0}\\else\\resizebox{\\wd0}{\\ht0}{\\copy0}\\fi";
				if (center) 
					ret = "\\makebox["+w+"]{"+ret+"}";				
			} 
		}
		if (colspan>1) 
			ret = "\\multicolumn{"+colspan+"}{"+(showGrid?(firstColumn?"|c|":"c|"):"c")+"}{"+ret+"}";
		if (rowspan>1)
			ret = "\\multirow{"+rowspan+"}{*}{"+ret+"}";
		return ret;
	}
	
	@Override
	public String toString() {
		return s;
	}
			
}
