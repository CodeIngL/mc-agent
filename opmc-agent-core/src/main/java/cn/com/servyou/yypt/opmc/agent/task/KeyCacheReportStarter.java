package cn.com.servyou.yypt.opmc.agent.task;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import cn.com.servyou.yypt.opmc.agent.task.reporter.KeyCacheReporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/3/15
 * @see
 */
public class KeyCacheReportStarter implements ApplicationListener {

    @ConfigAnnotation(name = OpmcConfigConstants.CLASS_INTERNAL_KEY_CACHE_REPORTER)
    private KeyCacheReporter keyCacheReporter;

    /**
     * 执行器
     */
    private static ScheduledExecutorService schedualService = Executors.newScheduledThreadPool(1);

    /**
     * 延迟时间,时间单位为毫秒
     */
    private static final long KEY_CACHE_INIT_DELAY_MS = 5000;

    /**
     * 间隔,时间单位为毫秒
     */
    private static final long KEY_CACHE_BETWEEN_PERIOD_MS = 300000;

    /**
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AvailableEvent) {
            init();
        }
    }

    /**
     *
     */
    public void init() {
        schedualService.scheduleAtFixedRate(keyCacheReporter,
                KEY_CACHE_INIT_DELAY_MS,
                KEY_CACHE_BETWEEN_PERIOD_MS,
                TimeUnit.MILLISECONDS);
    }

    //------------get---------set----------


    public KeyCacheReporter getKeyCacheReporter() {
        return keyCacheReporter;
    }

    public void setKeyCacheReporter(KeyCacheReporter keyCacheReporter) {
        this.keyCacheReporter = keyCacheReporter;
    }
}
