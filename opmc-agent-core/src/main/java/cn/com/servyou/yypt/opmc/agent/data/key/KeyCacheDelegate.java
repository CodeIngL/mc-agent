package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;

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
 * @date 2018/3/15
 * @see
 */
public class KeyCacheDelegate {

    @ConfigAnnotation(name = OpmcConfigConstants.CLASS_INTERNAL_KEY_CACHE_REGISTRY)
    private KeyCacheRegistry keyCacheRegistry = new KeyCacheRegistry();

    /**
     * 注册缓存。todo
     *
     * @param staticKey
     * @param method
     * @param divideConfigInfo
     */
    public void registerKeyCache(String staticKey, Method method, DivideConfigInfo divideConfigInfo) {
        if (staticKey == null || "".equals(staticKey.trim())) {
            return;
        }
        if (method == null) {
            return;
        }
        KeyCache keyCacheInfo = new KeyCache();
        keyCacheInfo.setStaticKey(staticKey);
        keyCacheInfo.setConfigIno(divideConfigInfo);
        if (divideConfigInfo != null &&
                divideConfigInfo.getDivideParamGetType() != null &&
                divideConfigInfo.getDivideParamGetType().length > 0) {
            keyCacheInfo.setHasDynamicKey(true);
        }
        keyCacheRegistry.registerKeyCache(method, keyCacheInfo);
    }

    /**
     * @param method
     * @return
     */
    public KeyCache takeKeyCache(Method method) {
        return keyCacheRegistry.get(method);
    }

    /**
     * 注册缓存。todo
     *
     * @param staticKey
     * @param method
     */
    public void registerKeyCache(String staticKey, Method method) {
        if (staticKey == null || "".equals(staticKey.trim())) {
            return;
        }
        if (method == null) {
            return;
        }
        HashMap<Method, KeyCache> keyCacheInfoHashMap = keyCacheRegistry.getKeyCacheMap();
        if (keyCacheInfoHashMap.containsKey(method)) {
            return;
        }
        KeyCache keyCacheInfo = new KeyCache();
        keyCacheInfo.setStaticKey(staticKey);
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
        Set<Method> methods = new HashSet<Method>();
        for (Map.Entry<Method, KeyCache> entry : keyCacheMap.entrySet()) {
            KeyCache keyCache = entry.getValue();
            if (keyCache == null) {
                continue;
            }
            if (System.currentTimeMillis() - keyCacheRegistry.getExpireTime() > keyCache.getTimestamp()) {
                methods.add(entry.getKey());
            }
        }
        for (Method method : methods) {
            keyCacheMap.remove(method);
        }
        methods.clear();

    }
}
