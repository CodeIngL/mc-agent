package cn.com.servyou.yypt.opmc.agent.task;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.opmc.agent.conf.init.Initializer;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import cn.com.servyou.yypt.opmc.agent.task.reporter.HeartBeatReporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_HEART_BEAT_REPORTER;

/**
 * <p>Function: [功能模块：心跳发送启动器]</p>
 * <p>Description: [功能描述：启动心跳线程]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 * @date 2017/7/28
 */
public class HeartBeatReportStarter implements ApplicationListener {

    /**
     * 心跳发送启动时的初始化延迟时间,时间单位为毫秒。等价于5S
     */
    private static final long HEART_BEAT_INIT_DELAY_MS = 5000;

    /**
     * 心跳发送间隔,时间单位为毫秒。等价于300S（5分钟）
     */
    private static final long HEART_BEAT_BETWEEN_PERIOD_MS = 300000;

    /**
     * 执行器
     */
    private static ScheduledExecutorService schedualService = Executors.newScheduledThreadPool(1);

    /**
     * 心跳报告器实例
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_HEART_BEAT_REPORTER)
    private HeartBeatReporter heartBeatReporter;

    /**
     *
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
        schedualService.scheduleAtFixedRate(heartBeatReporter, HEART_BEAT_INIT_DELAY_MS, HEART_BEAT_BETWEEN_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    //------------get---------set----------

    public HeartBeatReporter getHeartBeatReporter() {
        return heartBeatReporter;
    }

    public void setHeartBeatReporter(HeartBeatReporter heartBeatReporter) {
        this.heartBeatReporter = heartBeatReporter;
    }

}
