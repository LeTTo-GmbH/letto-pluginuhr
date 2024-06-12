package at.letto.tools;

/**
 * 
 * @author Thomas
 *
 */
public class EH_Erg{
	/**
	 * Formel-Bezeichner wie zB. R für Widerstand, L für Induktivität,...
	 */
	private String bez = "";
	/**
	 * Wenn true, dann wird beim Anlegen von neuen Datensätze die in einheit
	 * definierte Einheit verwendet
	 */
	private boolean replace = false;
	/**
	 * Definition der Einheit als String, die bei Ersetzung verwendet wird
	 */
	private String einheit = "";
	/**
	 * Wertebereich, der für neuen Datensatz verwendet wird
	 */
	private String datasetInit = "";
	/**
	 * Konstruktor
	 * @param bez	Formel-Bezeichner wie zB. R für Widerstand, L für Induktivität,...
	 * @param datasetInit	Wertebereich, der für neuen Datensatz verwendet wird 
	 * @param replace	Wenn true, dann wird beim Anlegen von neuen Datensätze die in einheit definierte Einheit verwendet
	 * @param einheit	Definition der Einheit als String, die bei Ersetzung verwendet wird
	 */
	public EH_Erg(String bez, String datasetInit, boolean replace, String einheit ) {
		this.bez = bez;
		this.einheit = einheit;
		this.replace = replace;
		this.datasetInit = datasetInit;
	}
	public String getBez() {
		return bez;
	}
	public void setBez(String bez) {
		this.bez = bez;
	}
	public boolean isReplace() {
		return replace;
	}
	public void setReplace(boolean replace) {
		this.replace = replace;
	}
	public String getEinheit() {
		return einheit;
	}
	public void setEinheit(String einheit) {
		this.einheit = einheit;
	}
	public String getDatasetInit() {
		return datasetInit;
	}
	public void setDatasetInit(String datasetInit) {
		this.datasetInit = datasetInit;
	}
}
