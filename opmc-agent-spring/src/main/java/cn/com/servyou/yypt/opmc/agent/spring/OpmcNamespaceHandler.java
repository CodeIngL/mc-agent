package cn.com.servyou.yypt.opmc.agent.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/7/17
 */
public class OpmcNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("driven", new OpmcDrivenBeanDefinitionParser());
    }

}
