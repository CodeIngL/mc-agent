package com.codeL.mc.agent.boot.spring;

import com.codeL.mc.agent.config.Configuration;
import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.config.McClientInitManager;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/10
 */
public class McAgentApplicationRunListener implements SpringApplicationRunListener {

    private McClientInitManager mcClientInitManager = new McClientInitManager();

    private final SpringApplication application;

    private final String[] args;

    public McAgentApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        mcClientInitManager.start();
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
        conf.getExceptionExcludes().addAll(beanConf.getExceptionExcludes());
        if (conf.isEnable()) {
            stateHolder.setContextState(ConfigurationStateHolder.ConfigurationState.AVAILABLE);
        }
    }
}
