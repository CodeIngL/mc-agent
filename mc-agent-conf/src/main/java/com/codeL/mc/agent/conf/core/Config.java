package com.codeL.mc.agent.conf.core;

import java.util.Map;

/**
 * <p>Description: 配置接口</p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/7
 */
public interface Config {

    /**
     * 设置属性值
     *
     * @param key   值
     * @param value 键
     */
    void setProperty(String key, String value);

    /**
     * @return 返回所有属性
     */
    Map<String, String> getProperties();
}
