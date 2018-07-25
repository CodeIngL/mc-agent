package cn.com.servyou.yypt.opmc.agent.reporter;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsDelegate;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Slf4jReporter;

import java.util.concurrent.TimeUnit;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/5/30
 */
public class Slf4jMetricsReporter implements ApplicationListener {

    private Slf4jReporter slf4jReporter;

    @ConfigAnnotation(name = ReporterConstant.OPMC_USER_METRICS_CONSOLE_REPORTER_PERIOD)
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
