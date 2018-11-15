package com.codeL.mc.agent.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/7/17
 */
public class McNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("driven", new McDrivenBeanDefinitionParser());
    }

}
