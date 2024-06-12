package at.letto.tools.tex;

import java.util.Vector;

/**
 * Modus für die Druckausgabe eines Tex-Elements
 * @author L-DAMB
 *
 */
public class TexPrintMode {
	
	private Tex.PRINTMODE printmode;
	private boolean recurse;
	private boolean newPage;
	private boolean line;
	private Vector<Integer> datasets;
	
	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 * @param printmode Art des Druckes  
	 * @param datasets  Feld aller zu druckenden Datensätze
	 * @param recurse   rekursiver Druck
	 * @param newPage   neue Seite nach jedem Element/Frage
	 * @param line      Linie nach jedem Element/Frage
	 */
	public TexPrintMode(Tex.PRINTMODE printmode, int[] datasets, boolean recurse, boolean newPage, boolean line) {
		this.printmode = printmode;
		this.datasets  = new Vector<Integer>();
		for (int i:datasets) 
			this.datasets.add(i);
		this.recurse   = recurse;
		this.newPage   = newPage;
		this.line      = line;
	}
	
	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 * @param printmode Art des Druckes  
	 * @param datasets  Feld aller zu druckenden Datensätze
	 * @param recurse   rekursiver Druck
	 * @param newPage   neue Seite nach jedem Element/Frage
	 * @param line      Linie nach jedem Element/Frage
	 */
	public TexPrintMode(Tex.PRINTMODE printmode, Vector<Integer> datasets, boolean recurse, boolean newPage, boolean line) {
		this(printmode, new int[0],recurse,newPage,line);
		this.setDatasets(datasets);
	}

	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 * @param printmode Art des Druckes
	 * @param dataset   zu druckender Datensatz
	 * @param recurse   rekursiver Druck
	 * @param newPage   neue Seite nach jedem Element/Frage
	 * @param line      Linie nach jedem Element/Frage
	 */
	public TexPrintMode(Tex.PRINTMODE printmode, int dataset, boolean recurse, boolean newPage, boolean line) {
		this(printmode, new int[0],recurse,newPage,line);
		Vector<Integer> datasets = new Vector<>();
		datasets.add(dataset);
		this.setDatasets(datasets);
	}
	
	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 */
	public TexPrintMode() {
		this(Tex.PRINTMODE.PRINTFRAGE, new int[]{0},false, false, false);
	}
	
	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 * @param printmode Art des Druckes  
	 */
	public TexPrintMode(Tex.PRINTMODE printmode) {
		this(printmode, new int[]{0},false, false, false);
	}
	
	/**
	 * Erzeugt ein Mode-Objekt für einen Tex-PDF-Druckjob
	 * @param printmode Art des Druckes  
	 * @param datasets  Feld aller zu druckenden Datensätze
	 */
	public TexPrintMode(Tex.PRINTMODE printmode, Vector<Integer> datasets) {
		this(printmode, datasets, false, false, false);
	}
	
	/**
	 * @return the printmode
	 */
	public Tex.PRINTMODE getPrintmode() {
		return printmode;
	}

	/**
	 * @param printmode the printmode to set
	 */
	public void setPrintmode(Tex.PRINTMODE printmode) {
		this.printmode = printmode;
	}

	/**
	 * @return Rekursiver Druck von Kategorien
	 */
	public boolean isRecurse() {
		return recurse;
	}

	/**
	 * @param recurse setzen vom rekursiven Druck von Kategorien
	 */
	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}

	/**
	 * @return neue Seite nach jeder Frage
	 */
	public boolean isNewPage() {
		return newPage;
	}

	/**
	 * @param newPage neue Seite nach jeder Frage
	 */
	public void setNewPage(boolean newPage) {
		this.newPage = newPage;
	}

	/**
	 * @return Linie nach jeder Frage
	 */
	public boolean isLine() {
		return line;
	}

	/**
	 * @param line Linie nach jeder Frage
	 */
	public void setLine(boolean line) {
		this.line = line;
	}
	
	/**
	 * @return Alle Datensatznummern, die gedruckt werden sollen
	 */
	public Vector<Integer> getDatasets() {
		return datasets;
	}

	public void setDatasets(Vector<Integer> datasets) {
		this.datasets = new Vector<Integer>();
		for (Integer i:datasets)
			this.datasets.add(i);
	}
	
}
