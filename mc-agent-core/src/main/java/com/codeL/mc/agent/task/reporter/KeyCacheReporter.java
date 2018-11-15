package com.codeL.mc.agent.task.reporter;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.constant.McConfigConstants;
import com.codeL.mc.agent.data.key.KeyCacheDelegate;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/3/15
 */
public class KeyCacheReporter implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(KeyCacheReporter.class);

    /**
     * keyCache委托
     */
    @ConfigAnnotation(name = McConfigConstants.CLASS_INTERNAL_KEY_CACHE_DELEGATE)
    private KeyCacheDelegate keyCacheDelegate;

    @Override
    public void run() {
        keyCacheDelegate.clear();
    }

    //------------get---------set----------

    public KeyCacheDelegate getKeyCacheDelegate() {
        return keyCacheDelegate;
    }

    public void setKeyCacheDelegate(KeyCacheDelegate keyCacheDelegate) {
        this.keyCacheDelegate = keyCacheDelegate;
    }
}
