package at.letto.tools.logging;

/**
 * Die Log-Level gehen hierarchisch von OFF,CRITICAL,NORMAL,DEBUG bis ALL<br>
 * Eine Meldung mit Level CRITICAL wird demnach von allen gleich oder höherwertigen Log-Level Einstellungen geloggt.<br>
 * Eine Meldung mit Level ALL wird nur bei ALL geloggt.
 */
public enum LogLevel {

    /** Nichts wird geloggt */
    OFF,
    /** Nur kritische Fehler werden geloggt - ERROR */
    CRITICAL,
    /** unkritische Fehler und Warnungen - WARN */
    WARNING,
    /** Normales Logging für den Betrieb - INFO*/
    NORMAL,
    /** Debug-Logging für die Fehlersuche - DEBUG */
    DEBUG,
    /** Alles wird geloggt - TRACE */
    ALL;

    /** LogLevel für einen neuen Logger wenn nichts angegeben wurde */
    public static final LogLevel STANDARD = LogLevel.NORMAL;

    /** LOG4J Level */
    public static final LogLevel TRACE = LogLevel.ALL;
    public static final LogLevel INFO  = LogLevel.NORMAL;
    public static final LogLevel WARN  = LogLevel.WARNING;
    public static final LogLevel ERROR = LogLevel.CRITICAL;

    /** Java-Util Logger Level */
    public static final LogLevel FINEST = LogLevel.ALL;
    public static final LogLevel FINE   = LogLevel.DEBUG;
    public static final LogLevel SEVERE = LogLevel.CRITICAL;

    /** LogLevel für eine Message wenn nichts angegeben wurde */
    public static final LogLevel MESSAGE_STANDARD = LogLevel.NORMAL;

    /** Bestimmt aus einem String den Loglevel */
    public static LogLevel parse(String s) {
        if (s==null || s.trim().length()==0) return LogLevel.STANDARD;
        s = s.trim();
        for (LogLevel logLevel:LogLevel.values()) {
            if (s.equalsIgnoreCase(logLevel.toString())) return logLevel;
        }
        return null;
    }

}
