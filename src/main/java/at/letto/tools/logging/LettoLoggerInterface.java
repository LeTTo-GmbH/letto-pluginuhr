package at.letto.tools.logging;

public interface LettoLoggerInterface {

    String getName();
    void trace(String msg);
    void debug(String msg);
    void info(String msg);
    void warn(String msg);
    void error(String msg);
    void error(String msg, Throwable t);
    LogLevel getLogLevel();

}
