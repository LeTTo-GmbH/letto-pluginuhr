package at.letto.config;

import at.letto.tools.Cmd;
import at.letto.tools.JSON;
import at.letto.tools.LogService;
import lombok.Getter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Liest die Properties eines Servers aus der application.properties,
 * einer externen Konfigurationsdatei, aus Betriebssystemumgebungsvariablen aus und kombiniert sie mit den
 * Werten die aus der Datenbank kommen und als Parameter des Constructors übergeben werden müssen.
 *
 * Bei mehrfachen Definitionen gilt grundsätzlich ein Wert nach folgender Priorität ( kleinere Nummer gilt vorher):
 *   1. Umgebungsvariable letto.[name]
 *   2. Umgebungsvariable letto_[name]
 *   3. Wert aus application.properties letto.[name]
 *   4. Wert aus config.json [name]
 *   5. Wert aus der Datenbank
 */
public class LeTToProperties {

    /** Quelle in der die Property gefunden wurde <br>
     * ENVIRONMENT .. Umgebungsvariable letto_NAME <br>
     * APPLICATION .. application.properties letto.NAME <br>
     * CONFIG      .. config.json NAME <br>
     * DATABASE    .. Config-Eintrag in der Datenbank mit den Namen NAME <br>
     * SET         .. nachträglich gesetzt <br>
     * UNDEF       .. nicht definierte Quelle <br>
     * */
    public enum PropertySource {
        /** Umgebungsvariable letto_NAME oder letto.NAME */
        ENVIRONMENT,
        /** application.properties letto.NAME */
        APPLICATION,
        /** config.json NAME */
        CONFIG,
        /** Config-Eintrag in der Datenbank mit den Namen NAME */
        DATABASE,
        /** nachträglich gesetzt */
        SET,
        /** nicht definierte Quelle */
        UNDEF;
    }

    /** Eine Property als String mit Inhalt und der Quelle von der sie gesetzt wurde */
    @Getter
    public class Property {
        /** Quelle von der die Property kommt */
        private PropertySource propertySource = PropertySource.UNDEF;
        /** Wert der Property */
        private String value = null;
        /** Wenn die Property von einer Datei-Quelle kommt, die Datei in der die Property gestanden ist */
        private File file = null;
        public Property(PropertySource propertySource, String value, File file) {
            this.propertySource = propertySource==null?PropertySource.UNDEF:propertySource;
            this.value = value;
            this.file  = file;
        }
        @Override public String toString() {
            return "Property["+propertySource+","+value+"]";
        }
    }

    /** Alle properties in einer HashMap mit Quelle und Inhalt */
    public final HashMap<String,Property> properties = new HashMap<>();

    /** Enthält alle Properties mit all den zugehörigen Quellen */
    private final Vector<Property> propertiesVector = new Vector<Property>();

    /** Alle Orte der Konfigurationsdatei application.properties, erster Eintrag wird beschrieben und als letztes geladen */
    private Vector<File> applicationPropertiesFiles = new Vector<File>();

    /** Alle Orte der Konfigurationsdatei mit JSON-Hash der Konfiguration, erster Eintrag wird beschrieben und als letztes geladen  */
    private Vector<File> configJsonFiles = new Vector<File>();

    /** Erzeugt ein Properties-Objekt wobei die Position der application.properties und config.json automatisch ermittelt wird <br>
     *  application.properties : 1.in Umgebungsvariable letto_properties definiert 2.aktuelles Verzeichnis 3./opt/letto/ <br>
     *  config.json : 1. in Umgebungsvariable letto_configFile definiert 2.aktuelles Verzeichnis 3./opt/letto<br>
     *  <br>
     *  Beim Ladevorgang werden die Einträge aus dem übergebenen Parameter "config" mit den
     *  @param  config Konfig-Werte, welche aus der Datenbank geladen wurden
     */
    public LeTToProperties(Map<String,String> config) {
        if (config==null) config = new HashMap<String, String>();
        // Datenbank-Values in den Properties speichern
        for (String key:config.keySet()) {
            Property property = new Property(PropertySource.DATABASE,config.get(key),null);
            properties.put(key,property);
            propertiesVector.add(property);
        }
        // Umgebungsvariable überschreiben die Datenbank-Values
        LogService.logToTmpFile("Environment:");
        Map<String,String> env = System.getenv();
        for (String key:env.keySet()) {
            if (key.startsWith("letto_") || key.startsWith("letto.")) {
                Property property = new Property(PropertySource.ENVIRONMENT, env.get(key), null);
                key = key.substring(6);
                properties.put(key, property);
                propertiesVector.add(property);
                LogService.logToTmpFile("* " + key + " --- " + property);
            }
        }
        // Position der application.properties bestimmen
        File applicationProperties=null;
        if (env.containsKey("letto_properties")) { // Erstmals wird bei den Umgebungsvariable
            applicationProperties = new File(env.get("letto_properties"));
            applicationPropertiesFiles.add(applicationProperties);
        }
        if (env.containsKey("letto.properties")) { // Erstmals wird bei den Umgebungsvariable
            applicationProperties = new File(env.get("letto.properties"));
            applicationPropertiesFiles.add(applicationProperties);
        }
        if (config.containsKey("properties")) {
            applicationProperties = new File(config.get("properties"));
            applicationPropertiesFiles.add(applicationProperties);
        }
        applicationProperties = new File("application.properties");
        String apn = "";
        if (applicationProperties.exists()) {
            applicationPropertiesFiles.add(applicationProperties);
            apn = applicationProperties.getAbsolutePath();
        }
        File optapp = new File("/opt/letto/application.properties");
        if (optapp.exists() && !(apn.equals(optapp.getAbsolutePath()))) {
            applicationPropertiesFiles.add(optapp);
        }
        // application.properties einlesen
        // Wert wird nur gespeichert, wenn er noch nicht existiert, oder aus der Datenbank kommt
        for (File ap:applicationPropertiesFiles) if (ap!=null && ap.exists() && ap.isFile()) {
            Vector<String> data = Cmd.readfile(ap);
            Property property = null;
            for (String line:data) {
                boolean folgeZeile = line.endsWith("\\");
                line = line.trim();
                if (line.trim().startsWith("#")) ; // Bemerkungen werden ignoriert!!
                else {
                    if (folgeZeile) line = line.substring(0, line.length() - 1);
                    if (property != null) { //letzte Zeile wurde mit einem Backslash beendet, wird also fortgesetzt
                        if (line.startsWith("\\ ")) line = line.substring(1);
                        property.value += line;
                    } else {
                        Matcher m = Pattern.compile("^\\s*letto[\\._]([^=\\s]+)\\s*=(.*)$").matcher(line);
                        if (m.find()) {
                            String key   = m.group(1);
                            property = new Property(PropertySource.APPLICATION,m.group(2),ap);
                            if (!properties.containsKey(key) || properties.get(key).propertySource==PropertySource.DATABASE)
                                // Nur hinzufügen wenn noch nicht vorhanden oder aus der Datebank!!
                                properties.put(key,property);
                            propertiesVector.add(property);
                        }
                    }
                    if (!folgeZeile) {
                        property = null;
                    }
                }
            }
        }

        // Position der config.json bestimmen
        File configJson=null;
        if (env.containsKey("letto_configFile")) { // Erstmals wird bei den Umgebungsvariable
            configJson = new File(env.get("letto_configFile"));
            configJsonFiles.add(configJson);
        }
        if (env.containsKey("letto.configFile")) { // Erstmals wird bei den Umgebungsvariable
            configJson = new File(env.get("letto.configFile"));
            configJsonFiles.add(configJson);
        }
        if (properties.containsKey("configFile")) {
            configJson = new File(properties.get("configFile").value);
            configJsonFiles.add(configJson);
        }
        configJson = new File("config.json");
        String cj = "";
        if (configJson.exists()) {
            configJsonFiles.add(configJson);
            cj = configJson.getAbsolutePath();
        }
        File optconf = new File("/opt/letto/config.json");
        if (optconf.exists() && !(cj.equals(optconf.getAbsolutePath()))) {
            configJsonFiles.add(optconf);
        }
        // config.json einlesen, Werte werden nur gesetzt wenn sie aus der Datenbank kommen oder noch nicht existieren
        for (File ap:configJsonFiles) if (ap!=null && ap.exists() && ap.isFile()) {
            Map<String, String> template = new HashMap<String, String>();
            try {
                Path path = Paths.get(ap.toURI());
                String confContent = Files.readAllLines(path, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
                Map<String, String> confHash = JSON.jsonToObj(confContent, template.getClass());
                confHash.keySet().forEach(k -> {
                    String key = k;
                    String data = confHash.get(k);
                    Property property = new Property(PropertySource.CONFIG,data,ap);
                    if (!properties.containsKey(key) || properties.get(key).propertySource==PropertySource.DATABASE)
                        // Nur hinzufügen wenn noch nicht vorhanden oder aus der Datebank!!
                        properties.put(key,property);
                    propertiesVector.add(property);
                });
            } catch (Exception ex) {
                System.out.println(ap.toURI()+" kann nicht gelesen werden!");
            }
        }
    }

    public Map<String,String> getPropertyMap() {
        Map<String,String> props = new HashMap<String,String>();
        for (String key:properties.keySet())
            props.put(key,properties.get(key).value);
        return props;
    }

    /**
     * Wenn die Property mit dem Namen "key" existiert, liefert es diese als String, ansonsten null
     * @param key Schlüssel der Property
     * @return    Inhalt
     */
    public String propertyString(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key).value;
        } else return null;
    }

    /**
     * Ändern einer String-Property
     * @param key   Schlüssel der Property
     * @param val   Neuer Wert
     * @return  true, wenn erfolgreich
     */
    public boolean setPropertyString(String key, String val) {
        if (!properties.containsKey(key)) return false;
        properties.get(key).value = val;
        return true;
    }

    /**
     * Wenn die Property mit dem Namen "key" existiert, liefert es diese als Boolean, ansonsten null
     * @param key Schlüssel der Property
     * @return    Inhalt
     */
    public Boolean propertyBoolean(String key) {
        String prop = propertyString(key);
        if (key!=null) {
            try {
                boolean b = Boolean.parseBoolean(prop);
                return b;
            } catch (Exception e) {}
            try {
                int ret= Integer.parseInt(prop);
                return ret!=0;
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Wenn die Property mit dem Namen "key" existiert, liefert es diese als Integer, ansonsten null
     * @param key Schlüssel der Property
     * @return    Inhalt
     */
    public Integer propertyInteger(String key) {
        String prop = propertyString(key);
        if (key!=null) {
            try {
                int ret= Integer.parseInt(prop);
                return ret;
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Wenn die Property mit dem Namen "key" existiert, liefert es diese als Long, ansonsten null
     * @param key Schlüssel der Property
     * @return    Inhalt
     */
    public Long propertyLong(String key) {
        String prop = propertyString(key);
        if (key!=null) {
            try {
                long ret= Long.parseLong(prop);
                return ret;
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Wenn die Property mit dem Namen "key" existiert, liefert es diese als Double, ansonsten null
     * @param key Schlüssel der Property
     * @return    Inhalt als Double
     */
    public Double propertyDouble(String key) {
        String prop = propertyString(key);
        if (key!=null) {
            try {
                double ret= Double.parseDouble(prop);
                return ret;
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Prüft ob ein Key in den Properties vorhanden ist
     * @param key Schlüssel der Property
     * @return    true wenn vorhanden
     */
    public boolean containsKey(String key) { return properties.containsKey(key); }


    /**
     * Sucht nach einer Environment Variablen und gibt sie zurück<br>
     * Suche nach name, letto_name und letto.name<br>
     * enthält name einen Punkt oder einen Unterstrich wird dieser bei letto_ bzw. letto. durch Unterstrich bzw. Punkt ersetzt.
     * @param name  Name der Environment Variablen
     * @return      Gibt den Inhalt der Environment Variablen oder null zurück wenn sie nicht existiert
     */
    public static String getEnv(String name) {
        String s;
        Map<String,String> env = System.getenv();
        if (env.containsKey(name))          return env.get(name);
        if (env.containsKey(s = "letto_"+name)) return env.get(s);
        if (env.containsKey(s = "letto_"+name.replaceAll("\\.","_"))) return env.get(s);
        if (env.containsKey(s = "letto."+name)) return env.get(s);
        if (env.containsKey(s = "letto."+name.replaceAll("_","."))) return env.get(s);
        return null;
    }

}
