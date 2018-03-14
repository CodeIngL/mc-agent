package cn.com.servyou.opmc.agent.conf.core;

import java.util.Map;

/**
 *
 * <p>Description: 配置接口</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/7
 */
public interface Config {

    /**
     * 设置属性值
     */
    void setProperty(String key, String value);

    /**
     * 返回所有属性
     */
    Map<String, String> getProperties();
}
