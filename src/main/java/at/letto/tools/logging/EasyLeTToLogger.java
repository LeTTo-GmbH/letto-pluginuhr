package at.letto.tools.logging;

import at.letto.tools.Datum;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Einfacher Logger für Letto in ein beliebiges Logfile mit LettoLoggerInterface<br>
 * siehe auch Log4LettoLogger und JavaUtilLettoLogger
 */
public class EasyLeTToLogger implements LettoLoggerInterface {

    private static String   logNameStandard = "EasyLeTToLogger";

    @Getter @Setter private File      logfile  = null;
    @Getter @Setter private LogLevel  logLevel = LogLevel.OFF;
    @Getter @Setter private String    logName          = logNameStandard;
    @Getter @Setter private LogLevel  standardMessageLogLevel = LogLevel.MESSAGE_STANDARD;

    /** Erzeugt einen Logger welcher mit LogLevel=OFF abgeschaltet und auf die stdout gesetzt ist */
    public EasyLeTToLogger() { }

    public EasyLeTToLogger(String logfileName, String logName, LogLevel logLevel) {
        this(new File(logfileName), logName, logLevel);
    }

    public EasyLeTToLogger(String logfileName, String logName) {
        this(new File(logfileName), logName, LogLevel.STANDARD);
    }

    public EasyLeTToLogger(String logfileName, LogLevel logLevel) {
        this(new File(logfileName), logNameStandard, logLevel);
    }

    public EasyLeTToLogger(String logfileName) {
        this(new File(logfileName), logNameStandard, LogLevel.STANDARD);
    }

    public EasyLeTToLogger(File logfile) {
        this(logfile, logNameStandard, LogLevel.STANDARD);
    }

    public EasyLeTToLogger(File logfile, String logName) {
        this(logfile, logName, LogLevel.STANDARD);
    }

    public EasyLeTToLogger(File logfile, LogLevel logLevel) {
        this(logfile, logNameStandard, logLevel);
    }

    public EasyLeTToLogger(File logfile, String logName, LogLevel logLevel) {
        this.logLevel = logLevel;
        this.logName = logNameStandard;
        this.logfile = logfile;
    }

    /** Prüft ob das Logfile vorhanden ist und legt es erforderlichenfalls neu an */
    private boolean checkLogfile() {
        try {
            if (this.logfile.exists()) return true;
            else {
                File parent = new File(this.logfile.getParent());
                if (!parent.exists())
                    parent.mkdirs();
                if (!this.logfile.exists())
                    this.logfile.createNewFile();
            }
        } catch (Exception ex) { }
        if (this.logfile.exists()) return true;
        return false;
    }

    public void logMessage(String msg) {
        logMessage(msg, standardMessageLogLevel);
    }

    public void logMessageCritical(String msg) {
        logMessage(msg, LogLevel.CRITICAL);
    }

    public void logMessageDebug(String msg) {
        logMessage(msg, LogLevel.DEBUG);
    }

    public void logMessageNormal(String msg) {
        logMessage(msg, LogLevel.NORMAL);
    }

    public void logMessageAll(String msg) {
        logMessage(msg, LogLevel.ALL);
    }

    public void logMessage(String msg, LogLevel msgLogLevel) {
        if (msgLogLevel.ordinal() > logLevel.ordinal()) return;
        String logMsg = logDate() + " : " + msg;
        if (checkLogfile()) {
            try {
                if (logfile != null) {
                    List<String> log = new Vector<>();
                    log.add(logMsg);
                    Files.write(logfile.toPath(), log, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                    return;
                }
            } catch (Exception e) {
            }
        }
        System.out.println("LOG-"+logName + " : "+logMsg);
    }

    public void logMessage(String msg, LogLevel msgLogLevel, Throwable throwable) {
        if (msgLogLevel.ordinal()>logLevel.ordinal()) return;
        String logMsg = logDate() + " : "+msg;
        try {
            if (logfile!=null) {
                List<String> log = new Vector<>();
                log.add(logMsg);
                Files.write(logfile.toPath(), log, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                if (throwable!=null) {
                    try (PrintWriter pw = new PrintWriter(new FileWriter(logfile, true))) {
                        throwable.printStackTrace(pw);
                    }
                }
                return;
            }
        } catch (Exception e) { }
        System.out.println(logName + " : "+logMsg);
    }

    public static String logDate() {
        return Datum.simpleDateFormatMillis.format(new Date());
    }

    /** löscht das Logfile und legt es leer neu an */
    public void clearLogfile() throws IOException {
        logfile.delete();
        logfile.createNewFile();
    }

    @Override
    public void trace(String msg) {
        this.logMessage(msg, LogLevel.TRACE);
    }

    @Override
    public void debug(String msg) {
        this.logMessage(msg, LogLevel.DEBUG);
    }

    @Override
    public void info(String msg) {
        this.logMessage(msg, LogLevel.INFO);
    }

    @Override
    public void warn(String msg) {
        this.logMessage(msg, LogLevel.WARNING);
    }

    @Override
    public void error(String msg) {
        this.logMessage(msg, LogLevel.CRITICAL);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        this.logMessage(msg, LogLevel.CRITICAL,throwable);
    }

    // ---------------------------------- Logger Interface ------------
    @Override public String getName() { return logName; }

}
