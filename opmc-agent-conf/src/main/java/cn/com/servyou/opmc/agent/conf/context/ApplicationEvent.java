package cn.com.servyou.opmc.agent.conf.context;

import java.util.EventObject;

/**
 *
 * <p>Description: 应用事件抽象类</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
public abstract class ApplicationEvent extends EventObject {

    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

}
