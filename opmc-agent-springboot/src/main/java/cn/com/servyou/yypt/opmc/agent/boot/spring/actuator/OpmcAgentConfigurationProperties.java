package cn.com.servyou.yypt.opmc.agent.boot.spring.actuator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

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
}
