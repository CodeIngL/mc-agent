package cn.com.servyou.yypt.opmc.agent.task.reporter;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import cn.com.servyou.yypt.opmc.agent.data.key.KeyCacheDelegate;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/3/15
 * @see
 */
public class KeyCacheReporter implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(KeyCacheReporter.class);

    @ConfigAnnotation(name = OpmcConfigConstants.CLASS_INTERNAL_KEY_CACHE_DELEGATE)
    private KeyCacheDelegate keyCacheDelegate;

    @Override
    public void run() {
        keyCacheDelegate.clear();
    }
}
