package cn.com.servyou.yypt.opmc.agent.boot.spring.actuator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author laihj
 *         2018/7/10
 */
@ConfigurationProperties(prefix = "opmc")
@Getter
@Setter
public class OpmcAgentConfigurationProperties {

    String appName;

    String serverUrl;

    boolean enable;

    Set<String> exceptionIncludes;

    boolean enableMetrics;

    List<OpmcReporterProperties> reporters;

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "reporter")
    public static class OpmcReporterProperties {

        String type;

        Map<String, String> kv;

    }

}
