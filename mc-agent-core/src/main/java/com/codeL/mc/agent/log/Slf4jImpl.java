package com.codeL.mc.agent.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 通用日志接口的slf4j实现
 * <p>
 */
public class Slf4jImpl implements Log {

    private static final String callerFQCN = Slf4jImpl.class.getName();
    private static final Logger testLogger = LoggerFactory.getLogger(Slf4jImpl.class);

    static {
        // if the logger is not a LocationAwareLogger instance, it can not get
        // correct stack StackTraceElement
        // so ignore this implementation.
        if (!(testLogger instanceof LocationAwareLogger)) {
            throw new UnsupportedOperationException(testLogger.getClass() + " is not a suitable logger");
        }
    }

    private int errorCount;
    private int warnCount;
    private int infoCount;
    private int debugCount;
    private LocationAwareLogger log;

    public Slf4jImpl(LocationAwareLogger log) {
        this.log = log;
    }

    public Slf4jImpl(String loggerName) {
        this.log = (LocationAwareLogger) LoggerFactory.getLogger(loggerName);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void error(String msg, Throwable e) {
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, e);
        errorCount++;
    }

    @Override
    public void error(String msg) {
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
        errorCount++;
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        infoCount++;
        log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, msg, null, null);
    }

    @Override
    public void debug(String msg) {
        debugCount++;
        log.log(null, callerFQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
    }

    @Override
    public void debug(String msg, Throwable e) {
        debugCount++;
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, e);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void warn(String msg) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, null);
        warnCount++;
    }

    @Override
    public void warn(String msg, Throwable e) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, e);
        warnCount++;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    @Override
    public int getInfoCount() {
        return infoCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

}
