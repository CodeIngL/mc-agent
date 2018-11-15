package com.codeL.mc.agent.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Function: [功能模块：通用日志接口的jdk logger实现]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Company: </p>
 *
 * @version 1.0
 * @date 2016/12/23
 * @see
 */
public class Jdk14LoggingImpl implements Log {

    private Logger log;

    private int errorCount;
    private int warnCount;
    private int infoCount;
    private int debugCount;

    private String loggerName;

    public Jdk14LoggingImpl(String loggerName) {
        this.loggerName = loggerName;
        log = Logger.getLogger(loggerName);
    }

    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }

    public void error(String s, Throwable e) {
        log.logp(Level.SEVERE, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s, e);
        errorCount++;
    }

    public void error(String s) {
        log.logp(Level.SEVERE, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s);
        errorCount++;
    }

    public void debug(String s) {
        debugCount++;
        log.logp(Level.FINE, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s);
    }

    public void debug(String s, Throwable e) {
        debugCount++;
        log.logp(Level.FINE, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s, e);
    }

    public void warn(String s) {
        log.logp(Level.WARNING, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s);
        warnCount++;
    }

    public void warn(String s, Throwable e) {
        log.logp(Level.WARNING, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), s, e);
        warnCount++;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }

    public void info(String msg) {
        log.logp(Level.INFO, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), msg);
        infoCount++;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }

    public int getDebugCount() {
        return debugCount;
    }

    public boolean isErrorEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

}
