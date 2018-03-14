package cn.com.servyou.yypt.opmc.agent.log;

/**
 * 通用日志接口，用于适配其他日志框架
 * <p>
 * Created by wangql on 2016/12/23.
 */
public interface Log {

    boolean isDebugEnabled();

    void error(String msg, Throwable e);

    void error(String msg);

    boolean isInfoEnabled();

    void info(String msg);

    void debug(String msg);

    void debug(String msg, Throwable e);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String msg, Throwable e);

    boolean isErrorEnabled();

    int getErrorCount();

    int getWarnCount();

    int getInfoCount();

    int getDebugCount();

    void resetStat();

}
