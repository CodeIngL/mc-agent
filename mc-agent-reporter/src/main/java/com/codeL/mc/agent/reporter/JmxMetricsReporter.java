package com.codeL.mc.agent.reporter;

import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;
import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codeL.mc.agent.event.AvailableEvent;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/4/17
 */
public class JmxMetricsReporter implements ApplicationListener {

    private JmxReporter jmxReporter;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AvailableEvent) {
            init();
        }
    }

    private void init() {
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        MetricsDelegate metricsDelegate = (MetricsDelegate) stateHolder.getBean(CLASS_INTERNAL_METRICS_DELEGATE, true);
        jmxReporter = JmxReporter
                .forRegistry(metricsDelegate.getRegistry())
                .filter(MetricFilter.ALL)
                .build();
        jmxReporter.start();
    }
}
