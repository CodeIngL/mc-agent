package cn.com.servyou.opmc.agent.conf.context;

import java.util.EventListener;

/**
 *
 * <p>Description: 监听器，监听合适的事件</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    void onApplicationEvent(E event);

}