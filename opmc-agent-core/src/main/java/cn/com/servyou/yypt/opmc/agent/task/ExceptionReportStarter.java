package cn.com.servyou.yypt.opmc.agent.task;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import cn.com.servyou.yypt.opmc.agent.task.reporter.ExceptionReporter;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_EXCEPTION_REPORTER;

/**
 * <p>Function: [功能模块：异常报告发送启动器]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class ExceptionReportStarter implements ApplicationListener {

    /**
     * 线程名字
     */
    private static final String EXCEPTION_REPORT_STARTER_THREAD_NAME = "opmc-agent-exceptionReporter";

    /**
     * 异常报告器实例
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_EXCEPTION_REPORTER)
    private ExceptionReporter exceptionReporter;

    /**
     * @param event 事件
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AvailableEvent) {
            init();
        }
    }

    /**
     * 初始化的时候启动异常报告线程.
     */
    public void init() {
        new Thread(exceptionReporter, EXCEPTION_REPORT_STARTER_THREAD_NAME).start();
    }

    //------------get---------set----------

    public ExceptionReporter getExceptionReporter() {
        return exceptionReporter;
    }

    public void setExceptionReporter(ExceptionReporter exceptionReporter) {
        this.exceptionReporter = exceptionReporter;
    }

}
