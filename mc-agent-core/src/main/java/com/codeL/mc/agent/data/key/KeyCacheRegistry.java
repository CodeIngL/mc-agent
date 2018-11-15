package com.codeL.mc.agent.data.key;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.constant.McConfigConstants;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/3/15
 */
@Slf4j
public class KeyCacheRegistry {

    /**
     * 过期时间
     */
    @ConfigAnnotation(name = McConfigConstants.MC_USER_KEY_CACHE_REGISTRY_EXPIRE_TIME)
    private Long expireTime = 30000L;

    /**
     * 缓存，用于快速刷新
     */
    private HashMap<Method, KeyCache> keyCacheMap = new HashMap<Method, KeyCache>(1024);

    /**
     * 注册键值对
     *
     * @param method   键
     * @param keyCache 值
     */
    public void registerKeyCache(Method method, KeyCache keyCache) {
        if (method == null || keyCache == null) {
            return;
        }
        if (keyCacheMap.size() >= 500) {
            return;
        }
        //这里并发是没有问题的，总是不存在引用链，也就是说总是可标记的
        keyCacheMap.put(method, keyCache);
    }

    /**
     * @param method 方法
     * @return 对应的KeyCache
     */
    public KeyCache get(Method method) {
        return keyCacheMap.get(method);
    }

    //-------get--------set方法

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public HashMap<Method, KeyCache> getKeyCacheMap() {
        return keyCacheMap;
    }



}
