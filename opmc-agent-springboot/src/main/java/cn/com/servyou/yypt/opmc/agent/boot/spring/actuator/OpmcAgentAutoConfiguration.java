package cn.com.servyou.yypt.opmc.agent.boot.spring.actuator;

import cn.com.servyou.yypt.opmc.agent.boot.spring.aspect.ExceptionAspect;
import cn.com.servyou.yypt.opmc.agent.boot.spring.aspect.MonitorByAnnotationAspect;
import cn.com.servyou.yypt.opmc.agent.boot.spring.processor.OpmcMetricRegistryProcessor;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/10
 */
@Configuration
@EnableConfigurationProperties({OpmcAgentConfigurationProperties.class})
public class OpmcAgentAutoConfiguration {

    @Bean
    cn.com.servyou.yypt.opmc.agent.config.Configuration configuration(OpmcAgentConfigurationProperties confProperties) {
        cn.com.servyou.yypt.opmc.agent.config.Configuration configuration = new cn.com.servyou.yypt.opmc.agent.config.Configuration();
        configuration.setAppName(confProperties.getAppName());
        configuration.setServerUrl(confProperties.getServerUrl());
        if (configuration.getExceptionIncludes() != null) {
            configuration.getExceptionIncludes().addAll(confProperties.getExceptionIncludes());
        }
        if(configuration.getExceptionExcludes() != null){
            configuration.getExceptionExcludes().addAll(confProperties.getExceptionExcludes());
        }
        configuration.setEnable(confProperties.isEnable());
        configuration.setCatchAll(confProperties.isCatchAll());
        return configuration;
    }

    @Bean
    ExceptionAspect exceptionAspect() {
        return new ExceptionAspect();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RequestMapping")
    MonitorByAnnotationAspect monitorByAnnotationAspect() {
        return new MonitorByAnnotationAspect();
    }

    @Bean
    MetricRegistry metricRegistry() {
        return SharedMetricRegistries.getOrCreate("metricRegistry");
    }

    @Bean
    @ConditionalOnProperty(name = "opmc.enableMetrics", havingValue = "true")
    OpmcMetricRegistryProcessor opmcMetricRegistryProcessor(OpmcAgentConfigurationProperties confProperties) {
        OpmcMetricRegistryProcessor processor = new OpmcMetricRegistryProcessor(confProperties.getReporters());
        return processor;
    }
}