package com.codeL.mc.agent.conf.context;

/**
 *
 * <p>Description: 发布者，发布相关的事件</p>
 * <p></p>
 *
 * @author laihj
 */
public interface ApplicationEventPublisher {

    /**
     * 发布事件
     * @param event 事件
     */
    void publishEvent(ApplicationEvent event);
}
