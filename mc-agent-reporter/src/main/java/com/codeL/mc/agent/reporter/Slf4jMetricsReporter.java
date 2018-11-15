package com.codeL.mc.agent.reporter;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;
import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codeL.mc.agent.event.AvailableEvent;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Slf4jReporter;

import java.util.concurrent.TimeUnit;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/5/30
 */
public class Slf4jMetricsReporter implements ApplicationListener {

    private Slf4jReporter slf4jReporter;

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
        slf4jReporter = Slf4jReporter
                .forRegistry(metricsDelegate.getRegistry())
                .filter(MetricFilter.ALL)
                .build();
        slf4jReporter.start(consolePeriod, TimeUnit.SECONDS);
    }
}
