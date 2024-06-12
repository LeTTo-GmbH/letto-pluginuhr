package at.letto.basespringboot.config;

import at.letto.tools.Cmd;
import at.letto.tools.config.MicroServiceConfigurationInterface;
import at.letto.tools.logging.EasyLeTToLogger;
import at.letto.tools.logging.LogLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Getter
@Configuration
public class BaseLoggingConfiguration {

    private Logger logger = LoggerFactory.getLogger(BaseLoggingConfiguration.class);

    private String logfilePath="";

    private File logfileError=null;

    private File logfileLogin=null;

    private File logfileStart=null;

    private HashMap<String, EasyLeTToLogger> leTToLoggerHashMap = new HashMap<>();

    private MicroServiceConfigurationInterface microServiceConfiguration = null;

    public void config(MicroServiceConfigurationInterface microServiceConfiguration) {
        this.microServiceConfiguration = microServiceConfiguration;
        this.logfilePath = microServiceConfiguration.getLogfilePath().trim().replaceAll("\\\\","/");
        while (this.logfilePath.endsWith("/")) this.logfilePath = this.logfilePath.substring(0,this.logfilePath.length()-1);
        File path = new File(logfilePath);
        if (path.exists() && path.isDirectory()) {
            // Pfad existiert alles ok
            logger.info("Logfilepath ok: "+path.getAbsolutePath());
        } else if (path.exists()) {
            logger.error("Logfilepath already exists as File not as Path: "+logfilePath);
        } else {
            path.mkdirs();
            if (path.exists() && path.isDirectory()) {
                // Pfad existiert alles ok
                logger.info("Logfilepath ok: "+path.getAbsolutePath());
            } else {
                logger.error("Cannot create Logfilepath: "+path.getAbsolutePath());
            }
        }
    }

    /** @return liefert einen Logger, wenn er vorhanden ist, anderfalls null */
    public EasyLeTToLogger getLettoLogger(String name) {
        if (leTToLoggerHashMap.containsKey(name)) return leTToLoggerHashMap.get(name);
        return null;
    }

    /**
     * Erzeugt einen Logger, wenn er noch nicht vorhanden ist
     * @param name        Name des Loggers
     * @param clear       gibt an ob das Logfile gelöscht werden soll
     * @return            neuer oder bestehender Logger
     */
    public EasyLeTToLogger createLettoLogger(String name, boolean clear) {
        return createLettoLogger(name, Cmd.renameFile(name)+".log", clear);
    }

    /**
     * Erzeugt einen Logger, wenn er noch nicht vorhanden ist
     * @param name        Name des Loggers
     * @param filename    Dateiname des Loggers
     * @return            neuer oder bestehender Logger
     */
    public EasyLeTToLogger createLettoLogger(String name, String filename, boolean clear) {
        EasyLeTToLogger easyLeTToLogger;
        if (leTToLoggerHashMap.containsKey(name)) easyLeTToLogger = leTToLoggerHashMap.get(name);
        else  {
            easyLeTToLogger = new EasyLeTToLogger(logfilePath+"/"+filename,name);
            leTToLoggerHashMap.put(name,easyLeTToLogger);
        }
        if (clear) {
            try {
                easyLeTToLogger.clearLogfile();
            } catch (IOException e) {
                logger.error("cannot clear logfile "+ easyLeTToLogger.getLogfile().getAbsolutePath());
            }
        }
        // Setze LogLevel mit Env-Var letto_log_level und letto_log_level_name
        LogLevel logLevel = LogLevel.parse(microServiceConfiguration.getLettoLogLevel());
        if (logLevel!=null) easyLeTToLogger.setLogLevel(logLevel);
        else logger.error("Fehlerhafter Loglevel : "+microServiceConfiguration.getLettoLogLevel());
        if (System.getenv().containsKey("letto_log_level_"+name)) {
            String loglevel = System.getenv().get("letto_log_level_"+name);
            if (loglevel!=null && loglevel.trim().length()>0) {
                logLevel = LogLevel.parse(loglevel);
                if (logLevel!=null) easyLeTToLogger.setLogLevel(logLevel);
                else logger.error("Fehlerhafter Loglevel "+loglevel+" in variable "+"letto_log_level_"+name);
            }
        }
        return easyLeTToLogger;
    }

    /**
     * Erzeugt einen Loglevel für einen EasyLeTToLogger<br>
     * Ist eine loglevel als String angegeben, dann wird der genommen,<br>
     * ist dieser nicht gesetzt wird von der Umgebungsvariable loglevel versucht den LogLevel zu setzen<br>
     * ist dieser ebenfalls nicht vorhanden, dann wird der LogLevel auf STANDARD gesetzt.
     * @param loglevel Loglevel als String
     * @return         LogLevel
     */
    public static LogLevel newLogLevel(String loglevel) {
        LogLevel level = LogLevel.STANDARD;
        try {
            if (System.getenv().containsKey("loglevel")) {
                LogLevel lln = LogLevel.parse(System.getenv("loglevel").trim());
                if (lln != null) level = lln;
            }
        } catch (Exception ex) {}
        try {
            if (loglevel!=null) {
                String ll = loglevel.trim();
                LogLevel lln = LogLevel.parse(ll);
                if (lln != null) level = lln;
            }
        } catch (Exception ex) {}
        return level;
    }

}
