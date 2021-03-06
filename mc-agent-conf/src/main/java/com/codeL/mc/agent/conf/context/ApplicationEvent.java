package com.codeL.mc.agent.conf.context;

import java.util.EventObject;

/**
 *
 * <p>Description: 应用事件抽象类</p>
 * <p></p>
 *
 * @author laihj
 */
public abstract class ApplicationEvent extends EventObject {

    /**
     * 事件时间戳
     */
    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

}
