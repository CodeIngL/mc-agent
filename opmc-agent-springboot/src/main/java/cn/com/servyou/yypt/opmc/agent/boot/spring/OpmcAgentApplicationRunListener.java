package cn.com.servyou.yypt.opmc.agent.boot.spring;

import cn.com.servyou.yypt.opmc.agent.config.Configuration;
import cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder;
import cn.com.servyou.yypt.opmc.agent.config.OpmcClientInitManager;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsDelegate;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/10
 */
public class OpmcAgentApplicationRunListener implements SpringApplicationRunListener {

    private OpmcClientInitManager opmcClientInitManager = new OpmcClientInitManager();

    private final SpringApplication application;

    private final String[] args;

    public OpmcAgentApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        opmcClientInitManager.start();
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        MetricsDelegate metricsDelegate = (MetricsDelegate) stateHolder.getBean(CLASS_INTERNAL_METRICS_DELEGATE, true);
        metricsDelegate.setRegistry(SharedMetricRegistries.getOrCreate("metricRegistry"));
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        //ignore
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        //ignore
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        //ignore
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        Configuration beanConf = context.getBean(Configuration.class);
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        Configuration conf = (Configuration) stateHolder.getBean(CLASS_INTERNAL_CONFIGURATION, true);
        conf.setEnable(beanConf.isEnable());
        conf.setAppName(beanConf.getAppName());
        conf.setServerUrl(beanConf.getServerUrl());
        conf.setCatchAll(beanConf.isCatchAll());
        conf.getExceptionIncludes().addAll(beanConf.getExceptionIncludes());
        if (conf.isEnable()) {
            stateHolder.setContextState(ConfigurationStateHolder.ConfigurationState.AVAILABLE);
        }
    }
}
