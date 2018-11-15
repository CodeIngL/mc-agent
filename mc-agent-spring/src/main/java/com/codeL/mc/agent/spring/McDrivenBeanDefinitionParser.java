package com.codeL.mc.agent.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.core.io.ByteArrayResource;
import org.w3c.dom.Element;


/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/17
 */
@Slf4j
public class McDrivenBeanDefinitionParser implements BeanDefinitionParser {

    static final String MC_PACKAGE = "com.codeL.mc.agent.spring";

    static final String METRIC_DEFAULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
            "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "       xmlns:metrics=\"http://www.ryantenney.com/schema/metrics\"\n" +
            "       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\n" +
            "       http://www.ryantenney.com/schema/metrics http://www.ryantenney.com/schema/metrics/metrics.xsd\">\n" +
            "    <metrics:metric-registry name=\"metricRegistry\" id=\"metricRegistry\"/>\n" +
            "    <metrics:health-check-registry id=\"metricHealthCheck\"/>\n" +
            "    <metrics:annotation-driven metric-registry=\"metricRegistry\"/>\n" +
            "    <metrics:reporter type=\"jmx\" id=\"metricJmxReporter\" metric-registry=\"metricRegistry\" />\n" +
            "</beans>";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        AopConfigUtils.registerAutoProxyCreatorIfNecessary(
                parserContext.getRegistry(), parserContext.extractSource(element));
        String mark = element.getAttribute("useDefaultMetric");
        if (!"false".equals(mark)) {
            BeanDefinitionRegistry registry = parserContext.getRegistry();
            ByteArrayResource byteArrayResource = new ByteArrayResource(METRIC_DEFAULT.getBytes());
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(new SimpleBeanDefinitionRegistry());
            int count = reader.loadBeanDefinitions(byteArrayResource);
            if (count > 0) {
                BeanDefinitionRegistry metricRegistry = reader.getRegistry();
                for (String beanName : metricRegistry.getBeanDefinitionNames()) {
                    if (registry.containsBeanDefinition(beanName)) {
                        log.warn("has existing dupkey beanName :{} so we disable ompc metrics", beanName);
                        return null;
                    }
                }
                for (String beanName : metricRegistry.getBeanDefinitionNames()) {
                    registry.registerBeanDefinition(beanName, metricRegistry.getBeanDefinition(beanName));
                }
            }
        }
        element.removeAttribute("useDefaultMetric");
        element.setAttribute("base-package", MC_PACKAGE);
        ComponentScanBeanDefinitionParser parser = new ComponentScanBeanDefinitionParser();
        parser.parse(element, parserContext);
        return null;
    }
}
