package com.codeL.mc.agent.boot.spring.actuator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author laihj
 *         2018/7/10
 */
@ConfigurationProperties(prefix = "mc")
@Getter
@Setter
@Component
public class McAgentConfigurationProperties {

    String appName;

    String serverUrl;

    boolean enable;

    Set<String> exceptionIncludes;

    Set<String> exceptionExcludes;

    boolean enableMetrics;

    boolean catchAll;

    List<McReporterProperties> reporters;

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "reporter")
    public static class McReporterProperties {

        String type;

        Map<String, String> kv;

    }
}
