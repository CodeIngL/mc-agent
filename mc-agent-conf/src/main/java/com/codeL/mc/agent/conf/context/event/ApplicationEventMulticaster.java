package com.codeL.mc.agent.conf.context.event;

import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;

/**
 * <p>Description: 多播器</p>
 * <p></p>
 *
 * @author laihj
 */
public interface ApplicationEventMulticaster {

    /**
     * 增加监听器
     *
     * @param listener 监听器
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 增加监听器
     *
     * @param listenerBeanName 监听名字
     */
    void addApplicationListenerBean(String listenerBeanName);

    /**
     * 移除监听器
     *
     * @param listener 监听器
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 移除监听器
     *
     * @param listenerBeanName 监听名字
     */
    void removeApplicationListenerBean(String listenerBeanName);

    /**
     * 移除所有监听器
     */
    void removeAllListeners();

    /**
     * 广播
     *
     * @param event 事件
     */
    void multicastEvent(ApplicationEvent event);

}
