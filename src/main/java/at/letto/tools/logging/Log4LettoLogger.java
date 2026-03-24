package at.letto.tools.logging;

import org.slf4j.Logger;

/**
 * Erzeugt aus einem Log4J Logger einen Logger mit LettoLoggerInterface
 */
public class Log4LettoLogger implements LettoLoggerInterface {

    private Logger logger;

    public Log4LettoLogger(Logger logger) {
        this.logger = logger;
    }


    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void trace(String msg) {
        logger.trace(msg);
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg,t);
    }

    @Override
    public LogLevel getLogLevel() {
        if (logger.isTraceEnabled()) return LogLevel.ALL;
        if (logger.isDebugEnabled()) return LogLevel.DEBUG;
        if (logger.isInfoEnabled())  return LogLevel.NORMAL;
        if (logger.isWarnEnabled())  return LogLevel.WARNING;
        if (logger.isErrorEnabled()) return LogLevel.CRITICAL;
        return LogLevel.OFF;
    }

}
