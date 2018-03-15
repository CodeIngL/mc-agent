package cn.com.servyou.yypt.opmc.agent.spring.manger;

import cn.com.servyou.yypt.opmc.agent.config.Configuration;
import cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder;
import cn.com.servyou.yypt.opmc.agent.config.OpmcClientInitManager;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsDelegate;
import com.codahale.metrics.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: spring相关</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/8
 */
@Component(value = "opmcSpringClientInitManager")
@DependsOn(value = "opmcConfiguration")
public class OpmcSpringClientInitManager implements ApplicationContextAware {

    @Autowired
    Configuration configuration;

    private ApplicationContext context;

    private OpmcClientInitManager opmcClientInitManager = new OpmcClientInitManager();

    @PostConstruct
    public void init() {
        opmcClientInitManager.start();
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
        conf.getExceptionExcludes().addAll(configuration.getExceptionExcludes());
        conf.getExceptionIncludes().addAll(configuration.getExceptionExcludes());
        conf.setServerUrl(configuration.getServerUrl());
        return;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
