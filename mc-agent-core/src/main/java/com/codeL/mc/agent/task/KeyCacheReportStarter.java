package com.codeL.mc.agent.task;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;
import com.codeL.mc.agent.common.NamedThreadFactory;
import com.codeL.mc.agent.constant.McConfigConstants;
import com.codeL.mc.agent.event.AvailableEvent;
import com.codeL.mc.agent.task.reporter.KeyCacheReporter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 *         2018/3/15
 */
public class KeyCacheReportStarter implements ApplicationListener {

    /**
     * 报告逻辑器
     */
    @ConfigAnnotation(name = McConfigConstants.CLASS_INTERNAL_KEY_CACHE_REPORTER)
    private KeyCacheReporter keyCacheReporter;

    /**
     * 执行器
     */
    private static ScheduledExecutorService schedualService = new ScheduledThreadPoolExecutor(1,new NamedThreadFactory("mc-kcc"));

    /**
     * 延迟时间,时间单位为毫秒。等价于5S
     */
    private static final long KEY_CACHE_INIT_DELAY_MS = 5000;

    /**
     * 间隔,时间单位为毫秒。等价于300S（5分钟）
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
