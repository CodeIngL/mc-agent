package com.codeL.mc.agent.data.key;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.constant.McConfigConstants;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/3/15
 */
public class KeyCacheDelegate {

    private static final Log LOGGER = LogFactory.getLog(KeyCacheDelegate.class);

    @ConfigAnnotation(name = McConfigConstants.CLASS_INTERNAL_KEY_CACHE_REGISTRY)
    private KeyCacheRegistry keyCacheRegistry = new KeyCacheRegistry();

    private int count = -2;
    private int preCount = -1;


    /**
     * @param method
     * @return
     */
    public KeyCache takeKeyCache(Method method) {
        return keyCacheRegistry.get(method);
    }

    /**
     * @param method
     * @param keyCache
     */
    public void registerKeyCache(Method method, KeyCache keyCache) {
        keyCacheRegistry.registerKeyCache(method, keyCache);
    }

    /**
     * 清楚消息
     */
    public void clear() {
        HashMap<Method, KeyCache> keyCacheMap = keyCacheRegistry.getKeyCacheMap();
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
            for (Map.Entry<Method, KeyCache> entry : keyCacheMap.entrySet()) {
                KeyCache keyCache = entry.getValue();
                if (keyCache == null) {
                    continue;
                }
                if (System.currentTimeMillis() - keyCacheRegistry.getExpireTime() > keyCache.getTimestamp()) {
                    methods.add(entry.getKey());
                    break;
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
