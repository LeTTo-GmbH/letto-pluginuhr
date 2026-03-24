package at.letto.tools.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Erzeugt aus einem java.util.logging Logger einen Logger mit LettoLoggerInterface
 */
public class JavaUtilLettoLogger implements LettoLoggerInterface{

    private Logger logger;

    public JavaUtilLettoLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void trace(String msg) {
        logger.finest(msg);
    }

    @Override
    public void debug(String msg) {
        logger.fine(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warning(msg);
    }

    @Override
    public void error(String msg) {
        logger.severe(msg);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.throwing(msg,"",t);
    }

    @Override
    public LogLevel getLogLevel() {
        if (logger.getLevel()==Level.FINEST) return LogLevel.TRACE;
        if (logger.getLevel()==Level.FINE)   return LogLevel.FINE;
        if (logger.getLevel()==Level.INFO)   return LogLevel.INFO;
        if (logger.getLevel()==Level.WARNING)return LogLevel.WARNING;
        if (logger.getLevel()==Level.SEVERE) return LogLevel.SEVERE;
        if (logger.getLevel()==Level.ALL)    return LogLevel.TRACE;
        if (logger.getLevel()==Level.CONFIG) return LogLevel.INFO;
        if (logger.getLevel()==Level.FINER)  return LogLevel.DEBUG;
        if (logger.getLevel()==Level.OFF)    return LogLevel.OFF;
        return LogLevel.OFF;
    }

    public void setLogLevel(LogLevel logLevel) {
        switch (logLevel) {
            case ALL     -> logger.setLevel(Level.FINEST);
            case DEBUG   -> logger.setLevel(Level.FINER);
            case NORMAL  -> logger.setLevel(Level.INFO);
            case WARNING -> logger.setLevel(Level.WARNING);
            case CRITICAL-> logger.setLevel(Level.SEVERE);
            default      -> logger.setLevel(Level.OFF);
        }
    }

}
