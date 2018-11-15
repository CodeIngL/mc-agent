package com.codeL.mc.agent.spring.manger;

import com.codeL.mc.agent.config.Configuration;
import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.config.McClientInitManager;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codahale.metrics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: spring相关</p>
 * <p></p>
 *
 * @author laihj
 * @date 2018/2/8
 */
@Component(value = "mcSpringClientInitManager")
@DependsOn(value = "mcConfiguration")
public class McSpringClientInitManager {

    @Autowired
    Configuration configuration;

    private McClientInitManager mcClientInitManager = new McClientInitManager();

    @PostConstruct
    public void init() {
        mcClientInitManager.start();
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        MetricsDelegate metricsDelegate = (MetricsDelegate) stateHolder.getBean(CLASS_INTERNAL_METRICS_DELEGATE, true);
        metricsDelegate.setRegistry(SharedMetricRegistries.getOrCreate("metricRegistry"));
        Configuration configuration = (Configuration) stateHolder.getBean(CLASS_INTERNAL_CONFIGURATION, true);
        copyConfiguration(configuration);
        if (configuration.isEnable()) {
            stateHolder.setContextState(ConfigurationStateHolder.ConfigurationState.AVAILABLE);
        }
    }

    private void copyConfiguration(Configuration conf) {
        conf.setEnable(configuration.isEnable());
        conf.setAppName(configuration.getAppName());
        conf.setServerUrl(configuration.getServerUrl());
        conf.getExceptionIncludes().addAll(configuration.getExceptionIncludes());
        conf.getExceptionExcludes().addAll(configuration.getExceptionExcludes());
        return;
    }

}
