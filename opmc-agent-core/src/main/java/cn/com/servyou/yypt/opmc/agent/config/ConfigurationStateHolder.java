package cn.com.servyou.yypt.opmc.agent.config;


import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.opmc.agent.conf.context.event.ApplicationEventMulticaster;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import cn.com.servyou.yypt.opmc.agent.event.UnavailableEvent;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.StringUtils;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder.ConfigurationState.UNAVAILABLE;

/**
 * <p>Description: 核心单例，再其状体完成后，会广播消息给监听者</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
public class ConfigurationStateHolder implements ApplicationEventMulticaster {

    private static final Log LOGGER = LogFactory.getLog(ConfigurationStateHolder.class);

    /**
     * 核心单例
     */
    static final ConfigurationStateHolder STATE_HOLDER = new ConfigurationStateHolder();

    public static ConfigurationStateHolder getInstance() {
        return STATE_HOLDER;
    }

    /**
     * 用于说明配置状态，{@code UNAVAILABLE}说明不可用，{@code AVAILABLE}说明可用
     */
    private ConfigurationState contextState = UNAVAILABLE;

    /**
     * 注册表，维护了内部构建的对象
     */
    private static final Map<String, Object> REGISTRY = new HashMap<String, Object>();

    /**
     * 监听对象，
     */
    private List<ApplicationListener> applicationListeners = new LinkedList<ApplicationListener>();

    ConfigurationState getContextState() {
        return contextState;
    }

    public void setContextState(ConfigurationState contextState) {
        this.contextState = contextState;
        LOGGER.debug("切换状态，准备广播消息");
        switch (contextState) {
            case UNAVAILABLE:
                multicastEvent(new UnavailableEvent(this));
                break;
            case AVAILABLE:
                multicastEvent(new AvailableEvent(this));
                break;
            default:
                break;
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        if (applicationListeners.contains(listener)) {
            return;
        }
        applicationListeners.add(listener);
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        Object listener = REGISTRY.get(listenerBeanName);
        if (listener == null || !(listener instanceof ApplicationListener)) {
            return;
        }
        addApplicationListener((ApplicationListener<?>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {
        Object listener = REGISTRY.get(listenerBeanName);
        if (listener == null || !(listener instanceof ApplicationListener)) {
            return;
        }
        removeApplicationListener((ApplicationListener<?>) listener);
    }

    @Override
    public void removeAllListeners() {
        applicationListeners = new LinkedList<ApplicationListener>();
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener listener : applicationListeners) {
            listener.onApplicationEvent(event);
        }
    }

    public enum ConfigurationState {
        /**
         * 不可用状态
         */
        UNAVAILABLE,
        /**
         * 可用状态
         */
        AVAILABLE
    }

    public Object getBean(String name) {
        if (StringUtils.isEmpty(name) || getContextState() == UNAVAILABLE) {
            return null;
        }
        return REGISTRY.get(name);
    }

    /**
     * 危险的操作,你必须要知道force的意义
     * 获得注册表中的对象
     * @param name 对象名
     * @param force 强制获得
     * @return 对象
     */
    public Object getBean(String name, Boolean force) {
        if (force) {
            return REGISTRY.get(name);
        }
        return getBean(name);
    }


    /**
     * 注册bean
     * @param registry 注册表
     */
    void registryAllBean(Map<String, Object> registry) {
        if (registry == null || registry.size() == 0) {
            return;
        }
        ConfigurationStateHolder.REGISTRY.putAll(registry);
    }

}
