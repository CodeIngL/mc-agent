package cn.com.servyou.opmc.agent.conf.context;

/**
 *
 * <p>Description: 发布者，发布相关的事件</p>
 * <p>税友软件集团有限公司</p>
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
