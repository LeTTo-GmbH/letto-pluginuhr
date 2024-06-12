package at.letto.tools.threads;


/**
 * Klasse für alle Timerwerte, die in Letto gesetzt sind.<br>
 * Alle Timerwerte werden in ms angegeben<br>
 * @author Werner Damböck
 *
 */
public class LettoTimer {
	
	
	/* ----------------------------------------------------------------------------------
	 *  Timerzeiten in ms. Ist die Zeit auf 0 gesetzt, so ist der Timer nicht aktiv
	 *  ----------------------------------------------------------------------------------
	 */
		
	private static int pluginImage  	= 0;
	private static int calcErgebnis 	= 0;
	/** Timer für den LaTeX Ausdruck */
	private static int print        	=  7200000;
	private static int handler  		=   300000;
	private static int setDatasetBean	=   300000;
	private static int selQuestion      =    60000;
	private static int onMaxima         =   300000;
	private static int getPluginImage   =   120000;
	private static int parser       	=    60000;
	private static int debug        	= 10000000;
	/** Timer für die Wartezeit, welche einem Thread gegeben wird nachdem der Timer abgelaufen ist im ihn mit interrupt zu beenden. */
	private static int interrupt        =    60000;
	private static int second           =     1000;

	/**
	 * Timer für das Question-Plugin, für die Erzeugung von AWT-Images
	 * @return Timerwert in ms
	 */
	public static int getPluginImageTimer() {
		return pluginImage;
	}

	/**
	 * Timer für eine Calculate.toCalcErgebnis Berechnung
	 * @return Timerwert in ms
	 */
	public static int getToCalcErgebnisTimer() {
		return calcErgebnis;
	}
	
	/**
	 * Timer für die Tex-Ausgabe
	 * @return Timerwert in ms
	 */
	public static int getPrintTimer() {
		return print;
	}

	/**
	 * Timer für alle Handler-Timer
	 * @return Timerwert in ms
	 */
	public static int getHandlerTimer() {
		return handler;
	}
	
	/**
	 * Timer für die Berechnung aller Datasets im Dataset-Bean
	 * @return Timerwert in ms
	 */
	public static int getSetDatasetBeanTimer() {
		return setDatasetBean;
	}


	/**
	 * Timer für das Laden einer Frage
	 * @return Timerwert in ms
	 */
	public static int getSelQuestionTimer() {
		return selQuestion;
	}

	/**
	 * Timer für das Berechnen des Maxima-Feldes einer Frage
	 * @return Timerwert in ms
	 */
	public static int getOnMaximaTimer() {
		return onMaxima;
	}
	
	/**
	 * Timer für das Berechnen eines Plugin-Bildes einer Frage
	 * @return Timerwert in ms
	 */
	public static int getGetPluginImageTimer() {
		return getPluginImage;
	}
	
	/**
	 * Timer für den Parser von Ausdrücken
	 * @return Timerwert in ms
	 */
	public static int getParserTimer() {
		return parser;
	}
	
	/**
	 * Timer für alle Timer wenn der Debugger eingeschaltet ist<br>
	 * Ist der Wert auf 0 gesetzt, so wird der Debug-Timer nicht verwendet!<br>
	 * @return Timerwert in ms
	 */
	public static int getDebugTimer() {
		// Setzt man den Debug-Timer auf 0, so wird er nicht verwendet!!
		return debug;
	}
	
	/**
	 * Timer für die Wartezeit, welche einem Thread gegeben wird nachdem der Timer abgelaufen ist im ihn mit interrupt zu beenden. Danach wird der Thread mit
	 * stop() brutal beendet.<br>
	 * Ist der Wert auf 0 gesetzt, so gibt es kein brutales Beenden des Threads, und der Thread läuft dann so lange weiter, bis das Programm beendet wird.
	 * @return Timerwert in ms
	 */
	public static int getInterruptTimer() {
		// Setzt man den Debug-Timer auf 0, so wird er nicht verwendet!!
		return interrupt;
	}
	
	/**
	 * Setzt alle Timerwerte auf den angegebenen Wert
	 * @param timems Zeit in ms
	 */
	public static void setAllTimers(int timems) {
		pluginImage  	= timems;
		calcErgebnis 	= timems;
		handler      	= timems;
		setDatasetBean	= timems;
		selQuestion     = timems;
		parser       	= timems;
		debug        	= timems;
	}
	
	/**
	 * Prüft ob der aktuelle Thread durch interrupt eine Unterbrechungsanforderung bekommen hat und wirft in diesem Fall eine LettoTimeoutException<br>
	 * Dies Methode sollte in allen Methoden und Schleifen verwendet werden, welche zu Endlosschleifen führen könnten.
	 */
	public static void checkInterrupt() {
		if (Thread.currentThread().isInterrupted())
			throw new LettoTimeoutException();
	}

	public static void delay_ms(int ms) {
		try{
			Thread.sleep(ms);
		} catch (Exception ex) {}
	}
	
}
