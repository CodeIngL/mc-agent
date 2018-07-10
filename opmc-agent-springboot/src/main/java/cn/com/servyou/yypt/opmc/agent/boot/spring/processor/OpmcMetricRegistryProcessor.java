package cn.com.servyou.yypt.opmc.agent.boot.spring.processor;

import cn.com.servyou.yypt.opmc.agent.boot.spring.actuator.OpmcAgentConfigurationProperties;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.ryantenney.metrics.spring.reporter.AbstractReporterElementParser;
import com.ryantenney.metrics.spring.reporter.ReporterElementParser;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;

/**
 * @author laihj
 *         2018/7/10
 */
public class OpmcMetricRegistryProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {

    static final Map<String, String> TYPE_MAPPING;
    static final String ID = "id";
    static final String TYPE = "type";
    static final String ENABLED = "enabled";
    static final String METRIC_REGISTRY_REF = "metric-registry";

    static {
        TYPE_MAPPING = new HashMap<String, String>();
        TYPE_MAPPING.put("jmx", "com.ryantenney.metrics.spring.reporter.");
        TYPE_MAPPING.put("console", "com.ryantenney.metrics.spring.reporter.ConsoleReporterFactoryBean");
        TYPE_MAPPING.put("csv", "com.ryantenney.metrics.spring.reporter.CsvReporterFactoryBean");
        TYPE_MAPPING.put("datadog", "com.ryantenney.metrics.spring.reporter.DatadogReporterFactoryBean");
        TYPE_MAPPING.put("ganglia", "com.ryantenney.metrics.spring.reporter.GangliaReporterFactoryBean");
        TYPE_MAPPING.put("graphite", "com.ryantenney.metrics.spring.reporter.GraphiteReporterFactoryBean");
        TYPE_MAPPING.put("librato", "com.ryantenney.metrics.spring.reporter.LibratoReporterFactoryBean");
        TYPE_MAPPING.put("newrelic", "com.ryantenney.metrics.spring.reporter.NewRelicReporterFactoryBean");
        TYPE_MAPPING.put("slf4j", "com.ryantenney.metrics.spring.reporter.Slf4jReporterFactoryBean");
    }

    private final ServiceLoader<ReporterElementParser> reporterElementParserLoader = ServiceLoader.load(ReporterElementParser.class);

    private final List<OpmcAgentConfigurationProperties.OpmcReporterProperties> reporters;

    public OpmcMetricRegistryProcessor(List<OpmcAgentConfigurationProperties.OpmcReporterProperties> reporters) {
        this.reporters = reporters;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //<metrics:metric-registry name="metricRegistry" id="metricRegistry"/>
        final String name = "metricRegistry";
        final BeanDefinitionBuilder beanDefBuilder = build(SharedMetricRegistries.class, null, ROLE_APPLICATION);
        beanDefBuilder.setFactoryMethod("getOrCreate");
        beanDefBuilder.addConstructorArgValue(name);
        registry.registerBeanDefinition(name, beanDefBuilder.getBeanDefinition());
        //<metrics:annotation-driven metric-registry="metricRegistry"/>
        registerComponent(registry, build(MetricRegistry.class, null, ROLE_APPLICATION));
        //<metrics:health-check-registry id="health"/>
        registerComponent(registry, build(HealthCheckRegistry.class, null, ROLE_APPLICATION));

        //<metrics:reporter type="jmx" id="metricJmxReporter" metric-registry="metricRegistry" />
        for (OpmcAgentConfigurationProperties.OpmcReporterProperties reporter : reporters) {
            String type = reporter.getType();
            if (type == null || "".equals(type)) {
                continue;
            }
            for (ReporterElementParser reporterElementParser : reporterElementParserLoader) {
                if (type.equals(reporterElementParser.getType())) {
                    if (!(reporterElementParser instanceof AbstractReporterElementParser)) {
                        break;
                    }
                    Class cls;
                    try {
                        cls = Class.forName(TYPE_MAPPING.get(type));
                    } catch (ClassNotFoundException e) {
                        break;
                    }
                    final BeanDefinitionBuilder builder;
                    builder = BeanDefinitionBuilder.rootBeanDefinition(cls.getClass());
                    builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                    AbstractBeanDefinition rawBeanDefinition = builder.getRawBeanDefinition();
                    rawBeanDefinition.setAutowireCandidate(false);
                    rawBeanDefinition.setSource(null);
                    addDefaultProperties(reporter, builder);
                    registerComponent(registry, beanDefBuilder);
                    break;
                }
            }
        }
        return;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //ignore
    }

    private BeanDefinitionBuilder build(Class<?> klazz, Object source, int role) {
        final BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(klazz);
        beanDefBuilder.setRole(role);
        beanDefBuilder.getRawBeanDefinition().setSource(source);
        return beanDefBuilder;
    }

    private String registerComponent(BeanDefinitionRegistry registry, BeanDefinitionBuilder beanDefBuilder) {
        final BeanDefinition beanDef = beanDefBuilder.getBeanDefinition();
        final String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDef, registry);
        registry.registerBeanDefinition(beanName, beanDef);
        return beanName;
    }

    protected void addDefaultProperties(OpmcAgentConfigurationProperties.OpmcReporterProperties reporterProperties, BeanDefinitionBuilder beanDefBuilder) {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.putAll(reporterProperties.getKv());
        properties.remove(METRIC_REGISTRY_REF);
        properties.remove(ID);
        properties.remove(TYPE);
        String enabled = properties.remove(ENABLED);
        beanDefBuilder.addPropertyReference("metricRegistry", "metricRegistry");
        if (StringUtils.hasText(enabled)) {
            beanDefBuilder.addPropertyValue("enabled", enabled);
        }
        beanDefBuilder.addPropertyValue("properties", properties);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
