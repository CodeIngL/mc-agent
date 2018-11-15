package com.codeL.mc.agent.conf.context;

import java.util.EventListener;

/**
 * <p>Description: 监听器，监听合适的事件</p>
 * <p></p>
 *
 * @author laihj
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    /**
     * 事件回调
     *
     * @param event 事件
     */
    void onApplicationEvent(E event);

}