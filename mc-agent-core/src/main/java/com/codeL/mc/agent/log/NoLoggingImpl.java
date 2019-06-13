package com.codeL.mc.agent.log;

import java.io.PrintStream;

/**
 * 没有任何日志框架的实现，纯打印
 * <p>
 */
public class NoLoggingImpl implements Log {

    private int infoCount;
    private int errorCount;
    private int warnCount;
    private int debugCount;
    private String loggerName;

    private boolean debugEnable = false;
    private boolean infoEnable = true;
    private boolean warnEnable = true;
    private boolean errorEnable = true;

    public NoLoggingImpl(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public boolean isDebugEnabled() {
        return debugEnable;
    }

    public void error(String s, Throwable e) {
        if (!errorEnable) {
            return;
        }

        error(s);

        if (e != null) {
            //此处应是e的输出操作
        }
    }

    public void error(String s) {
        errorCount++;
        if (s != null) {
            PrintStream ps = System.err;
            ps.println(loggerName + " : " + s);
        }
    }

    public void debug(String s) {
        debugCount++;
    }

    public void debug(String s, Throwable e) {
        debugCount++;
    }

    public void warn(String s) {
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        warnCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return infoEnable;
    }

    @Override
    public void info(String s) {
        infoCount++;
    }

    @Override
    public boolean isWarnEnabled() {
        return warnEnable;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public boolean isErrorEnabled() {
        return errorEnable;
    }

    public void setErrorEnabled(boolean value) {
        this.errorEnable = value;
    }
}
