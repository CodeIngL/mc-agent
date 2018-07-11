package cn.com.servyou.yypt.opmc.agent.boot.spring.processor;

import cn.com.servyou.yypt.opmc.agent.boot.spring.actuator.OpmcAgentConfigurationProperties;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.ryantenney.metrics.spring.MetricsBeanPostProcessorFactory;
import com.ryantenney.metrics.spring.reporter.AbstractReporterElementParser;
import com.ryantenney.metrics.spring.reporter.ReporterElementParser;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

/**
 * @author laihj
 *         2018/7/10
 */
public class OpmcMetricRegistryProcessor implements BeanPostProcessor, ApplicationContextAware, Ordered {

    static final Map<String, String> TYPE_MAPPING;
    static final String ID = "id";
    static final String TYPE = "type";
    static final String ENABLED = "enabled";
    static final String METRIC_REGISTRY_REF = "metric-registry";

    private AtomicBoolean mark = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    static {
        TYPE_MAPPING = new HashMap<String, String>();
        TYPE_MAPPING.put("jmx", "com.ryantenney.metrics.spring.reporter.JmxReporterFactoryBean");
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

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //<metrics:metric-registry name="metricRegistry" id="metricRegistry"/>
        final String name = "metricRegistry";
        final BeanDefinitionBuilder beanDefBuilder = build(SharedMetricRegistries.class, null, ROLE_APPLICATION);
        beanDefBuilder.setFactoryMethod("getOrCreate");
        beanDefBuilder.addConstructorArgValue(name);
        registry.registerBeanDefinition(name, beanDefBuilder.getBeanDefinition());
        //<metrics:health-check-registry id="health"/>
        final String healthId = "health";
        registerComponent(registry, build(HealthCheckRegistry.class, null, ROLE_APPLICATION), healthId);
        //<metrics:annotation-driven metric-registry="metricRegistry"/>
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setProxyTargetClass(true);

        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("exceptionMetered").addConstructorArgReference(name).addConstructorArgValue(proxyConfig), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("metered").addConstructorArgReference(name).addConstructorArgValue(proxyConfig), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("timed").addConstructorArgReference(name).addConstructorArgValue(proxyConfig), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("counted").addConstructorArgReference(name).addConstructorArgValue(proxyConfig), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("gaugeField").addConstructorArgReference(name), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("gaugeMethod").addConstructorArgReference(name), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("cachedGauge").addConstructorArgReference(name), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("metric").addConstructorArgReference(name), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("healthCheck").addConstructorArgReference(healthId), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("legacyCounted").addConstructorArgReference(name).addConstructorArgValue(proxyConfig), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("legacyCachedGauge").addConstructorArgReference(name), null);
        registerComponent(registry, this.build(MetricsBeanPostProcessorFactory.class, null, ROLE_INFRASTRUCTURE).setFactoryMethod("legacyMetric").addConstructorArgReference(name), null);


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
                    final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(cls);
                    builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                    AbstractBeanDefinition rawBeanDefinition = builder.getRawBeanDefinition();
                    rawBeanDefinition.setAutowireCandidate(false);
                    rawBeanDefinition.setSource(null);
                    addDefaultProperties(reporter, builder);
                    registerComponent(registry, builder, null);
                    break;
                }
            }
        }
        return;
    }


    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            postProcessBeanDefinitionRegistry(defaultListableBeanFactory);
        }
        //ignore

    }

    private BeanDefinitionBuilder build(Class<?> klazz, Object source, int role) {
        final BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(klazz);
        beanDefBuilder.setRole(role);
        beanDefBuilder.getRawBeanDefinition().setSource(source);
        return beanDefBuilder;
    }

    private String registerComponent(BeanDefinitionRegistry registry, BeanDefinitionBuilder beanDefBuilder, String id) {
        final BeanDefinition beanDef = beanDefBuilder.getBeanDefinition();
        final String beanName;
        if (!StringUtils.hasText(id)) {
            beanName = BeanDefinitionReaderUtils.generateBeanName(beanDef, registry);
        } else {
            beanName = id;
        }
        registry.registerBeanDefinition(beanName, beanDef);
        return beanName;
    }

    protected void addDefaultProperties(OpmcAgentConfigurationProperties.OpmcReporterProperties reporterProperties, BeanDefinitionBuilder beanDefBuilder) {
        final Map<String, String> properties = new HashMap<String, String>();
        if (reporterProperties.getKv() != null) {
            properties.putAll(reporterProperties.getKv());
        }
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
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!mark.get()) {
            if (mark.compareAndSet(false, true)) {
                postProcessBeanFactory((ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
