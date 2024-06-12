package at.open.letto.plugins.uhr;

import at.letto.math.dto.CalcErgebnisDto;
import at.letto.math.dto.ToleranzDto;
import at.letto.math.dto.VarHashDto;
import at.letto.math.enums.CALCERGEBNISTYPE;
import at.letto.plugins.dto.*;
import at.letto.plugins.enums.ORIENTATIONX;
import at.letto.plugins.enums.ORIENTATIONY;
import at.letto.plugins.service.BasePlugin;
import at.letto.plugins.tools.PluginToolsAwt;
import at.letto.plugins.tools.PluginToolsMath;
import at.letto.tools.Datum;
import at.letto.tools.enums.Score;
import lombok.Getter;
import java.awt.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Plugin für eine einfache Uhr zum Reinklicken
 *
 * @author LeTTo
 */
@Getter
public class PluginUhr extends BasePlugin {

	// ---------------------------------------------------------------------------------------------------------------------
	//        Constructor
	// ---------------------------------------------------------------------------------------------------------------------
	/**
	 * Konstruktor eines Plugins für eine Uhr als Demo der Plugin-Verwendung
	 * Beim Registrieren des Plugins am LeTTo-Server wird der Constructor mit
	 * name="" und params="" aufgerufen, dabei muss er alle Standard-Werte korrekt setzen. <br>
	 * In einer Frage werden dann über den params-String die Konfigurations-Parameter des Plugins
	 * gesetzt und können die Werte der Plugin-Variablen anpassen.<br>
	 * @param name		Plugin-Name
	 * @param params	Parameter für Plugin
	 */
	public PluginUhr(String name, String params) {
		super(name, params);
		version           = "1.0";         		// Version des Plugins
		helpfiles         = new String[]{"plugins/uhr/Uhr.html"};    	// Plugin Hilfe als HTML für den Plugin - Dialog
		javascriptLibs    = new String[]{"plugins/uhr/uhrScript.js","plugins/uhr/uhrConfigScript.js","plugins/plugintools.js"};  // Javascript Libraries für das Plugin
		javaScript        = true;  				// gibt an ob das Plugin eine Java-Script Schnittstelle bei der Beispieldarstellung hat
		initPluginJS      = "initPluginUhr";  	// Name der JAVA-Script Methode zur Plugin-Initialisierung für die interaktive Ergebniseingabe
		configPluginJS    = "configPluginUhr";  // Name der JAVA-Script Methode zur Configuration des Plugins
		configurationMode = PluginConfigurationInfoDto.CONFIGMODE_JAVASCRIPT;   // Konfigurations-Mode für die Konfiguration des Plugins
		wikiHelp          = "Plugins";  		// Namen der Wiki-Seite wenn eine Doku am LeTTo-Wiki vorliegt
		helpUrl			  = "";    // Hilfe-URL für die Beschreibung des Plugins
		width  			  = 400;   // Breite des zu erzeugenden Bildes
		height 			  = 400;   // Höhe des zu erzeugenden Bildes
		imageWidthProzent = 100;   // Größe des Bildes in Prozent
		math 			  = false; // True wenn das Plugin CalcErgebnis und VarHash als JSON verarbeiten kann
		cacheable 		  = true;  // Plugin ist stateless und liefert bei gleicher Angabe immer das gleiche Verhalten
		useQuestion		  = true;  // Gibt an ob im Plugin die Frage benötigt wird
		useVars			  = true;  // gibt an ob die Datensatz-Variable ohne Konstante benötigt werden
		useCVars		  = true;  // gibt an ob die Datensatz-Variable mit Konstanten benötigt werden
		useMaximaVars	  = true;  // gibt an ob die Maxima-Durchrechnungen ohne eingesetzte Datensätze benötigt werden
		useMVars		  = true;  // gibt an ob die Maxima-Durchrechnungen mit eingesetzten Datensätzen benötigt werden
		addDataSet		  = true;  // Gibt an, ob im Plugin-Konfig-Dialog Datensätze hinzugefügt werden können => Button AddDataset in Fußzeile des umgebenden Dialogs, (nicht vom Plugin)
		externUrl		  = true;  // Gibt an, ob das Plugin über den Browser direkt erreichbar ist
		calcMaxima		  = true;  // Gibt an ob im Plugin bei der Konfiguration die Maxima-Berechnung durchlaufen werden kann. => Button Maxima in Fußzeile des umgebenden Dialogs, (nicht vom Plugin)

		// Auswertung der Parameter
		Matcher m;
		String pa[]=null;
		pa = 	params.split(";");
		if (pa!=null && pa.length>0) {
			for (String p:pa) {
				if ((m=Pattern.compile("^\\s*[wW](\\d+)\\s*$").matcher(p)).find()) {
					this.imageWidthProzent = Integer.parseInt(m.group(1));
					if (imageWidthProzent<0)   imageWidthProzent=1;
					if (imageWidthProzent>100) imageWidthProzent=100;
				}
				switch(p.trim().replaceAll("\\s+","")) {
					case "mode=iframe" -> configurationMode = PluginConfigurationInfoDto.CONFIGMODE_URL;
					case "mode=string" -> configurationMode = PluginConfigurationInfoDto.CONFIGMODE_STRING;
					case "mode=jsf" -> configurationMode = PluginConfigurationInfoDto.CONFIGMODE_JSF;
					case "mode=js" -> configurationMode = PluginConfigurationInfoDto.CONFIGMODE_JAVASCRIPT;
				}
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------
	//        Methoden
	// ---------------------------------------------------------------------------------------------------------------------
	@Override
	public void paint(Graphics2D g, PluginImageResultDto pluginImageResultDto) {
		int xMiddle = width/2;
		int yMiddle = height/2;
		int radius  = (int)(Math.min(width,height)*0.48);
		g.setColor(Color.blue);
		Stroke stroke = g.getStroke();
		for (int i=1;i<=12;i++) {
			double alpha = Math.PI/2-2*Math.PI/12d*i;
			g.setStroke(new BasicStroke(4f));
			g.drawLine((int)(xMiddle+radius*0.9*Math.cos(alpha)), (int)(yMiddle-radius*0.9*Math.sin(alpha)),
					   (int)(xMiddle+radius*Math.cos(alpha)),     (int)(yMiddle-radius*Math.sin(alpha)));
			PluginToolsAwt.drawString(g,""+i,
					      (int)(xMiddle+radius*0.83*Math.cos(alpha)), (int)(yMiddle-radius*0.83*Math.sin(alpha)),
						radius/10, ORIENTATIONX.CENTER, ORIENTATIONY.CENTER,0);
		}
		g.setColor(Color.black);
		g.drawOval(xMiddle-radius,yMiddle-radius,2*radius,2*radius);
		g.fillOval(xMiddle-7,yMiddle-7,14,14);
		g.setStroke(stroke);
	}

	@Override
	public void parseDrawParams(String params, PluginQuestionDto q, PluginImageResultDto pluginImageResultDto) {
		this.width=500;
		this.height=500;
		for (String p:params.split(",")) {
			Matcher m;
			if ((m= Pattern.compile("^size=(\\d+)x(\\d+)$").matcher(p)).find()) {
				int w = Integer.parseInt(m.group(1));
				int h = Integer.parseInt(m.group(2));
				width = w;
				height= h;
			} else if ((m=Pattern.compile("^size=(\\d+)$").matcher(p)).find()) {
				int w = Integer.parseInt(m.group(1));
				width = w;
				height= w;
			}
		}
	}

	@Override
	public Vector<String> getVars() {
		return null;
	}

	@Override
	public Vector<String[]> getImageTemplates() {
		Vector<String[]> ret = new Vector<String[]>();
		ret.add(new String[]{"Uhr ","[PIG "+this.name+" \"\"]","Uhrblatt"});
		return ret;
	}

	@Override
	public PluginScoreInfoDto score(PluginDto pluginDto, String antwort, ToleranzDto toleranz, VarHashDto varsQuestion, PluginAnswerDto pluginAnswerDto, double grade){
		String ze = pluginAnswerDto.getZe();
		String answerText = pluginAnswerDto.getAnswerText();
		PluginScoreInfoDto info = new PluginScoreInfoDto(
				new CalcErgebnisDto(antwort,null, CALCERGEBNISTYPE.STRING),ze,0.,grade,Score.FALSCH,"",""
		);
    	try {
			double richtig = Datum.parseTime(pluginAnswerDto.getAnswerText());
			double eingabe = Datum.parseTime(antwort);
			if (PluginToolsMath.equals(richtig,eingabe,toleranz))
				info =  new PluginScoreInfoDto(new CalcErgebnisDto(antwort,null, CALCERGEBNISTYPE.STRING),ze,grade,grade, Score.OK,"","");
		} catch (Exception ex) { }
		info.setHtmlScoreInfo("Wert:"+antwort);
    	return info;
	}

	@Override
	public PluginDto loadPluginDto(String params, PluginQuestionDto q,int nr) {
		return new PluginDto(params, this, q,  nr);
	}

	/**
	 * Liefert die Informationen welche notwendig sind um einen Konfigurationsdialog zu starten<br>
	 * Ist die configurationID gesetzt wird eine Konfiguration gestartet und damit auch die restlichen Endpoints für die
	 * Konfiguration aktiviert.
	 * @param configurationID    eindeutige ID welche für die Verbindung zwischen Edit-Service, Browser und Plugin-Konfiguration verwendet wird
	 * @return                   alle notwendigen Konfig
	 */
	@Override
	public PluginConfigurationInfoDto configurationInfo(String configurationID) {
		PluginConfigurationInfoDto pluginConfigurationInfoDto = super.configurationInfo(configurationID);
		pluginConfigurationInfoDto.setConfigurationMode(configurationMode);
		pluginConfigurationInfoDto.setJavaScriptMethode(configPluginJS);
		pluginConfigurationInfoDto.setConfigurationID(configurationID);
		return pluginConfigurationInfoDto;
	}

	/**
	 * Sendet alle notwendigen (im ConfigurationInfo) angeforderten Daten im Mode CONFIGMODE_URL an die Plugin-Konfiguration
	 * @param configuration   aktueller Konfigurations-String des Plugins
	 * @param questionDto     Question-DTO mit Varhashes
	 * @return                Liefert die Daten welche an JS weitergeleitet werden.
	 */
	@Override
	public PluginConfigDto setConfigurationData(String configuration, PluginQuestionDto questionDto) {
		this.config = configuration;
		PluginConfigDto pluginConfigDto = new PluginConfigDto();
		pluginConfigDto.getParams().put("TEST","testinhalt");
		pluginConfigDto.getParams().put("help",this.getHelp());
		pluginConfigDto.getParams().put("vars",questionDto.getVars()!=null?questionDto.getVars().toString():"null");
		/*if (getWikiHelp().length()>0) {
			pluginConfigDto.getParams().put("wikiurl", "https://wiki.letto.at/wiki/index.php/" + getWikiHelp());
		}*/
		return pluginConfigDto;
	}

	/**
	 * Liefert die aktuelle Konfiguration eines Plugins welches sich gerade in einem CONFIGMODE_URL Konfigurationsdialog befindet
	 * @return                Konfigurationsparameter oder "@ERROR: Meldung" wenn etwas nicht funktioniert hat
	 */
	@Override
	public String getConfiguration() {
		return this.config;
	}

}
