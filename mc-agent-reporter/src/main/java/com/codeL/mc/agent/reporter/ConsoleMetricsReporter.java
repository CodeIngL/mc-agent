package com.codeL.mc.agent.reporter;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;
import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codeL.mc.agent.event.AvailableEvent;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;

import java.util.concurrent.TimeUnit;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/4/17
 */
public class ConsoleMetricsReporter implements ApplicationListener {


    private ConsoleReporter consoleReporter;

    @ConfigAnnotation(name = ReporterConstant.MC_USER_METRICS_CONSOLE_REPORTER_PERIOD)
    private int consolePeriod = 30;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AvailableEvent) {
            init();
        }
    }

    private void init() {
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        MetricsDelegate metricsDelegate = (MetricsDelegate) stateHolder.getBean(CLASS_INTERNAL_METRICS_DELEGATE, true);
        consoleReporter = ConsoleReporter
                .forRegistry(metricsDelegate.getRegistry())
                .filter(MetricFilter.ALL)
                .build();
        if (consolePeriod < 0) {
            consolePeriod = 30;
        }
        consoleReporter.start(consolePeriod, TimeUnit.SECONDS);
    }
}
