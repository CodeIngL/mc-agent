package com.codeL.mc.agent.conf.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: 简单的配置实现类</p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/5
 */
public class PropConfig implements Config {

    /**
     * 配置对
     */
    private Map<String, String> conf = new ConcurrentHashMap<String, String>(16);

    @Override
    public void setProperty(String key, String value) {
        if (key == null || "".equals(key)) {
            return;
        }
        conf.put(key, value);
    }

    @Override
    public Map<String, String> getProperties() {
        return conf;
    }

}
