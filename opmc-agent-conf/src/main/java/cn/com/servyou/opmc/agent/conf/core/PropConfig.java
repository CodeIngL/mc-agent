package cn.com.servyou.opmc.agent.conf.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <p>Description: 简单的配置实现类</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/5
 */
public class PropConfig implements Config {

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
