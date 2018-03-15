package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.HashMap;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/3/15
 * @see
 */
@Slf4j
public class KeyCacheRegistry {

    @ConfigAnnotation(name = OpmcConfigConstants.OPMC_USER_KEY_CACHE_REGISTRY_MAX_SIZE)
    private Integer maxSize = Integer.MAX_VALUE;

    @ConfigAnnotation(name = OpmcConfigConstants.OPMC_USER_KEY_CACHE_REGISTRY_EXPIRE_TIME)
    private Long expireTime = 30000L;

    /**
     * 缓存，用于快速刷新
     */
    private HashMap<Method, KeyCache> keyCacheMap = new HashMap<>();


    /**
     * @param method
     * @param keyCache
     */
    public void registerKeyCache(Method method, KeyCache keyCache) {
        if (method == null || keyCache == null) {
            return;
        }
        if (keyCacheMap.containsKey(method)) {
            return;
        }
        if (keyCacheMap.size() > maxSize) {
            return;
        }
        keyCacheMap.put(method, keyCache);
    }

    /**
     * @param method
     * @return
     */
    public KeyCache remove(Method method) {
        return keyCacheMap.remove(method);
    }

    /**
     *
     * @param method
     * @return
     */
    public KeyCache get(Method method) {
        return keyCacheMap.get(method);
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public HashMap<Method, KeyCache> getKeyCacheMap() {
        return keyCacheMap;
    }

    public void setKeyCacheMap(HashMap<Method, KeyCache> keyCacheMap) {
        this.keyCacheMap = keyCacheMap;
    }


}
