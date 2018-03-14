package cn.com.servyou.yypt.opmc.agent.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>Function: [功能模块：Log4j代理实现类]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author wangql
 * @version 1.0
 * @date 2016/12/23
 * @see
 */
public class Log4jImpl implements Log {

    private static final String callerFQCN = Log4jImpl.class.getName();

    private Logger log;

    private int errorCount;
    private int warnCount;
    private int infoCount;
    private int debugCount;


    public Log4jImpl(Logger log) {
        this.log = log;
    }

    public Log4jImpl(String loggerName) {
        log = Logger.getLogger(loggerName);
    }

    public Logger getLog() {
        return log;
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String s, Throwable e) {
        errorCount++;
        log.log(callerFQCN, Level.ERROR, s, e);
    }

    public void error(String s) {
        errorCount++;
        log.log(callerFQCN, Level.ERROR, s, null);
    }

    public void debug(String s) {
        debugCount++;
        log.log(callerFQCN, Level.DEBUG, s, null);
    }

    public void debug(String s, Throwable e) {
        debugCount++;
        log.log(callerFQCN, Level.DEBUG, s, e);
    }

    public void warn(String s) {
        log.log(callerFQCN, Level.WARN, s, null);
        warnCount++;
    }

    public void warn(String s, Throwable e) {
        log.log(callerFQCN, Level.WARN, s, e);
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

    public int getDebugCount() {
        return debugCount;
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void info(String msg) {
        infoCount++;
        log.log(callerFQCN, Level.INFO, msg, null);
    }

    public boolean isWarnEnabled() {
        return log.isEnabledFor(Level.WARN);
    }

    public boolean isErrorEnabled() {
        return log.isEnabledFor(Level.ERROR);
    }

    public int getInfoCount() {
        return infoCount;
    }

    public String toString() {
        return log.toString();
    }
}
