package com.codeL.mc.agent.task;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.conf.context.ApplicationEvent;
import com.codeL.mc.agent.conf.context.ApplicationListener;
import com.codeL.mc.agent.event.AvailableEvent;
import com.codeL.mc.agent.task.reporter.ExceptionReporter;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_EXCEPTION_REPORTER;

/**
 * <p>Function: [功能模块：异常报告发送启动器]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
public class ExceptionReportStarter implements ApplicationListener {

    /**
     * 线程名字
     */
    private static final String EXCEPTION_REPORT_STARTER_THREAD_NAME = "mc-agent-exceptionReporter";

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
