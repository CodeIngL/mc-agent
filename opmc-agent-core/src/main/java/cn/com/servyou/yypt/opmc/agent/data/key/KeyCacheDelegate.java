package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/3/15
 */
public class KeyCacheDelegate {

    private static final Log LOGGER = LogFactory.getLog(KeyCacheDelegate.class);

    @ConfigAnnotation(name = OpmcConfigConstants.CLASS_INTERNAL_KEY_CACHE_REGISTRY)
    private KeyCacheRegistry keyCacheRegistry = new KeyCacheRegistry();

    private int count = -2;
    private int preCount = -1;


    /**
     * @param method
     * @return
     */
    public KeyCache takeKeyCache(Method method, String type) {
        return keyCacheRegistry.get(method, type);
    }

    /**
     * @param method
     * @param keyCache
     */
    public void registerKeyCache(Method method, String type, KeyCache keyCache) {
        keyCacheRegistry.registerKeyCache(method, type, keyCache);
    }

    /**
     * 清楚消息
     */
    public void clear() {
        HashMap<Method, Map<String, KeyCache>> keyCacheMap = keyCacheRegistry.getKeyCacheMap();
        if (keyCacheMap.size() < 500) {
            return;
        }
        if (preCount == keyCacheMap.size()) {
            count++;
        }
        preCount = keyCacheMap.size();
        if (count != 0) {
            return;
        }
        try {
            Set<Method> methods = new HashSet<Method>();
            for (Map.Entry<Method, Map<String, KeyCache>> entry : keyCacheMap.entrySet()) {
                Map<String, KeyCache> keyCacheHolder = entry.getValue();
                if (keyCacheHolder == null) {
                    continue;
                }
                for (Map.Entry<String,KeyCache> keyCacheEntry: keyCacheHolder.entrySet()) {
                    KeyCache keyCache = keyCacheEntry.getValue();
                    if (keyCache == null){
                        continue;
                    }
                    if (System.currentTimeMillis() - keyCacheRegistry.getExpireTime() > keyCache.getTimestamp()) {
                        methods.add(entry.getKey());
                        break;
                    }
                }
            }
            for (Method method : methods) {
                keyCacheMap.remove(method);
            }
            methods.clear();
        } catch (Exception e) {
            LOGGER.error("corruent happen error, try next", e);
        } finally {
            count = -2;
            preCount = -1;
        }
    }
}
