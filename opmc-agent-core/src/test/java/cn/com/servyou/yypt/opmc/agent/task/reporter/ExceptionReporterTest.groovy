package cn.com.servyou.yypt.opmc.agent.task.reporter

import cn.com.servyou.yypt.opmc.agent.config.Configuration
import cn.com.servyou.yypt.opmc.agent.data.exception.ExceptionHolder
import cn.com.servyou.yypt.opmc.agent.data.exception.ExceptionHolderRegistry
import cn.com.servyou.yypt.opmc.agent.task.ExceptionReportStarter
import org.junit.Before
import org.junit.Test

/**
 cif
 *
 */
class ExceptionReporterTest {

    ExceptionReporter reporter = new ExceptionReporter();

    class AException extends Exception {}

    class BException extends Exception {}

    class CException extends Exception {}

    class DException extends Exception {}

    class EException extends Exception {}

    @Before
    void init() {
        Configuration configuration = new Configuration();
        configuration.appName = "servyou";
        configuration.enable = true;
        configuration.exceptionExcludes = ["123", "234"];
        configuration.serverUrl = "";
        configuration.exceptionIncludes = ["123", "234"];
        reporter.configuration = configuration;
        ExceptionHolderRegistry registry = new ExceptionHolderRegistry();
        registry.queueSize = 1000;
        reporter.exceptionHolderRegistry = registry;
    }

    @Test
    void testExceptionReporter() {
        reporter.exceptionHolderRegistry.init();
        new Thread(new Runnable() {
            @Override
            void run() {
                int i = 0;
                while (true) {
                    ExceptionHolder exceptionHolder = new ExceptionHolder()
                    exceptionHolder.available = true
                    Throwable throwable = null
                    switch (i % 5) {
                        case 0:
                            throwable = new AException();
                            break;
                        case 1:
                            throwable = new BException();
                            break;
                        case 2:
                            throwable = new CException();
                            break;
                        case 3:
                            throwable = new DException();
                            break;
                        case 4:
                            throwable = new EException();
                            break;
                    }
                    exceptionHolder.throwable = throwable;
                    exceptionHolder.counts = 0;
                    exceptionHolder.timestamp = System.currentTimeMillis();
                    reporter.exceptionHolderRegistry.put(exceptionHolder);
                    i++;
                    if (i == 10) {
                        break;
                    }
                }
            }
        }).start();
        ExceptionReportStarter starter = new ExceptionReportStarter();
        starter.exceptionReporter = reporter;
        starter.init();
        System.sleep(50000);
        println(reporter.configuration);
    }
}
