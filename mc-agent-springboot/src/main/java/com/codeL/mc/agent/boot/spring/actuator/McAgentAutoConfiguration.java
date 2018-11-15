package com.codeL.mc.agent.boot.spring.actuator;

import com.codeL.mc.agent.boot.spring.aspect.ExceptionAspect;
import com.codeL.mc.agent.boot.spring.aspect.MonitorByAnnotationAspect;
import com.codeL.mc.agent.boot.spring.processor.McMetricRegistryProcessor;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/10
 */
@Configuration
@EnableConfigurationProperties({McAgentConfigurationProperties.class})
public class McAgentAutoConfiguration {

    @Bean
    com.codeL.mc.agent.config.Configuration configuration(McAgentConfigurationProperties confProperties) {
        com.codeL.mc.agent.config.Configuration configuration = new com.codeL.mc.agent.config.Configuration();
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
    @ConditionalOnProperty(name = "mc.enableMetrics", havingValue = "true")
    McMetricRegistryProcessor mcMetricRegistryProcessor(McAgentConfigurationProperties confProperties) {
        McMetricRegistryProcessor processor = new McMetricRegistryProcessor(confProperties.getReporters());
        return processor;
    }
}
