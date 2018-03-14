package cn.com.servyou.yypt.opmc.agent.log;

import java.lang.reflect.Constructor;

/**
 * <p>Function: [功能模块：日志工厂]</p>
 * <p>Description: [功能描述：当前只提供slf4j、Log4jv1、jdk logger及no logger的实现，其他日志框架按需要可再增加实现类]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author wangql
 * @version 1.0
 * @date 2016/12/23
 * @see
 */
public class LogFactory {

    private static Constructor logConstructor;

    static {
        String packageName = LogFactory.class.getPackage().getName();
        String logType = System.getProperty("yypt.logType");
        if (logType != null) {
            if (logType.equalsIgnoreCase("slf4j")) {
                tryImplementation("org.slf4j.Logger", packageName + ".Slf4jImpl");
            } else if (logType.equalsIgnoreCase("log4j")) {
                tryImplementation("org.apache.log4j.Logger", packageName + ".Log4jImpl");
            } else if (logType.equalsIgnoreCase("log4j2")) {
                tryImplementation("org.apache.logging.log4j.Logger", packageName + ".Log4j2Impl");
            } else if (logType.equalsIgnoreCase("commonsLog")) {
                tryImplementation("org.apache.commons.logging.LogFactory", packageName + ".JakartaCommonsLoggingImpl");
            } else if (logType.equalsIgnoreCase("jdkLog")) {
                tryImplementation("java.util.logging.Logger", packageName + ".Jdk14LoggingImpl");
            }
        }
        // 优先选择slf4j,log4j,而非Apache Common Logging. 因为后者无法设置真实Log调用者的信息
        tryImplementation("org.slf4j.Logger", packageName + ".Slf4jImpl");
        tryImplementation("org.apache.log4j.Logger", packageName + ".Log4jImpl");
        tryImplementation("org.apache.logging.log4j.Logger", packageName + ".Log4j2Impl");
        tryImplementation("org.apache.commons.logging.LogFactory", packageName + ".JakartaCommonsLoggingImpl");
        tryImplementation("java.util.logging.Logger", packageName + ".Jdk14LoggingImpl");

        if (logConstructor == null) {
            try {
                logConstructor = NoLoggingImpl.class.getConstructor(String.class);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void tryImplementation(String testClassName, String implClassName) {
        if (logConstructor != null) {
            return;
        }

        try {
            Resources.classForName(testClassName);
            Class implClass = Resources.classForName(implClassName);
            logConstructor = implClass.getConstructor(new Class[]{String.class});

            Class<?> declareClass = logConstructor.getDeclaringClass();
            if (!Log.class.isAssignableFrom(declareClass)) {
                logConstructor = null;
            }

            try {
                if (null != logConstructor) {
                    logConstructor.newInstance(LogFactory.class.getName());
                }
            } catch (Throwable t) {
                logConstructor = null;
            }

        } catch (Throwable t) {
            // skip
        }
    }

    public static Log getLog(Class clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String loggerName) {
        try {
            return (Log) logConstructor.newInstance(loggerName);
        } catch (Throwable t) {
            throw new RuntimeException("Error creating logger for logger '" + loggerName + "'.  Cause: " + t, t);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void selectLog4JLogging() {
        try {
            Resources.classForName("org.apache.log4j.Logger");
            Class implClass = Resources.classForName("cn.com.servyou.yypt.opmc.agent.log.Log4jImpl");
            logConstructor = implClass.getConstructor(new Class[]{String.class});
        } catch (Throwable t) {
            // ignore
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void selectJavaLogging() {
        try {
            Resources.classForName("java.util.logging.Logger");
            Class implClass = Resources.classForName("cn.com.servyou.yypt.opmc.agent.log.Jdk14LoggingImpl");
            logConstructor = implClass.getConstructor(new Class[]{String.class});
        } catch (Throwable t) {
            // ignore
        }
    }
}