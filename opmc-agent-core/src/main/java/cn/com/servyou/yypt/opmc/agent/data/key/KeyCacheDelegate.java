package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;

import java.lang.reflect.Method;
import java.util.HashMap;

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
        HashMap<Method, KeyCache> keyCacheInfoHashMap = keyCacheRegistry.getKeyCacheMap();
        if (keyCacheInfoHashMap.containsKey(method)) {
            return;
        }
        KeyCache keyCacheInfo = new KeyCache();
        keyCacheInfo.setStaticKey(staticKey);
        keyCacheInfo.setConfigIno(divideConfigInfo);
        if (divideConfigInfo == null) {
            keyCacheInfo.setHasDynamicKey(false);
            return;
        }
        if (divideConfigInfo.getDivideParamGetType() == null || divideConfigInfo.getDivideParamGetType().length == 0) {
            keyCacheInfo.setHasDynamicKey(false);
            return;
        }
        keyCacheInfo.setHasDynamicKey(true);
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

}
