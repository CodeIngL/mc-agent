package cn.com.servyou.yypt.opmc.agent.reporter;

import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsDelegate;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
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
