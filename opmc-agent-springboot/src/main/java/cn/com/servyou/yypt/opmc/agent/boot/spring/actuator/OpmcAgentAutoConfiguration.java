package cn.com.servyou.yypt.opmc.agent.boot.spring.actuator;

import cn.com.servyou.yypt.opmc.agent.boot.spring.aspect.ExceptionAspect;
import cn.com.servyou.yypt.opmc.agent.boot.spring.aspect.MonitorByAnnotationAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

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
        configuration.setAppName(configuration.getAppName());
        configuration.setServerUrl(configuration.getServerUrl());
        if (configuration.getExceptionIncludes() != null) {
            configuration.getExceptionIncludes().addAll(confProperties.getExceptionIncludes());
        }
        configuration.setEnable(configuration.isEnable());
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

}
