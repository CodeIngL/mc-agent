package cn.com.servyou.yypt.opmc.agent.config;

import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.opmc.agent.conf.core.ObjectWrapper;
import cn.com.servyou.opmc.agent.conf.init.Initializer;
import cn.com.servyou.opmc.agent.conf.manager.ConfManager;
import cn.com.servyou.yypt.opmc.agent.data.metrics.MetricsDelegate;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/5
 */
public class OpmcClientInitManager {

    private static final Log LOGGER = LogFactory.getLog(OpmcClientInitManager.class);

    private static final String SYSTEM_FILE_CONF = "opmcSystem.properties";

    private static final String USER_FILE_CONF = "opmc.properties";

    private static final String SYSTEM_CLASS_PREFIX = "opmcSystem.class";

    private static final String USER_CLASS_PREFIX = "opmc.user.class";

    private ConfManager confManager = new ConfManager();

    /**
     * 入口进行加载,构建依赖关系
     */
    public void start() {
        //加载系统配置
        confManager.loadFrameworkConf(SYSTEM_FILE_CONF);
        //加载用户配置文件
        confManager.loadFrameworkConf(USER_FILE_CONF);
        try {
            //注册类
            registerClass(confManager.getInnerConf());
            //构建依赖关系
            confManager.buildSimpleDependency();
        } catch (Exception e) {
            LOGGER.error("配置文件存在问题", e);
            throw new RuntimeException(e);
        }

        Map<String, Object> registry = new HashMap<String, Object>();
        Map<String, ObjectWrapper> beanMap = confManager.getAll();
        for (Map.Entry<String, ObjectWrapper> beanEntry : beanMap.entrySet()) {
            registry.put(beanEntry.getKey(), beanEntry.getValue().getObject());
        }

        //转移注册表
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        stateHolder.registryAllBean(registry);

        //添加监听器
        for (Object listener : registry.values()) {
            if (listener instanceof ApplicationListener) {
                stateHolder.addApplicationListener((ApplicationListener<?>) listener);
            }
        }

        List<Initializer> initializers = new ArrayList<Initializer>();
        for (Object initializer : registry.values()) {
            if (initializer instanceof Initializer) {
                initializers.add((Initializer) initializer);
            }
        }

        for (Initializer initializer : initializers) {
            initializer.init();
        }

        //设置状态
        Configuration configuration = (Configuration) registry.get(CLASS_INTERNAL_CONFIGURATION);
        if (configuration.isEnable()) {
            stateHolder.setContextState(ConfigurationStateHolder.ConfigurationState.AVAILABLE);
        }
    }

    /**
     * 从conf找出类
     *
     * @param conf
     */
    private void registerClass(Map<String, String> conf) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (Map.Entry<String, String> entry : conf.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(SYSTEM_CLASS_PREFIX) || key.startsWith(USER_CLASS_PREFIX)) {
                confManager.putBean(entry.getKey(), Class.forName(entry.getValue()));
            }
        }
    }
}
