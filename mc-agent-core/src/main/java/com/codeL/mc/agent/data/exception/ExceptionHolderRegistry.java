package com.codeL.mc.agent.data.exception;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.conf.init.Initializer;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;

import java.util.concurrent.*;

import static com.codeL.mc.agent.constant.McConfigConstants.MC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE;

/**
 * <p>Function: [功能模块：抓取到的异常缓存]</p>
 * <p>Description: [功能描述：存储已抓取的异常并且判断是否需要发送,异常报告器会从待发送队列中获取异常进行发送]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
public class ExceptionHolderRegistry implements Initializer {

    private static final Log LOGGER = LogFactory.getLog(ExceptionHolderRegistry.class);

    /**
     * 待发送的异常队列的大小,定为1000
     */
    @ConfigAnnotation(name = MC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE)
    private int queueSize;

    /**
     * 待发送的异常队列
     */
    private LinkedBlockingQueue<ExceptionHolder> exceptionsCacheQueue;

    /**
     * 放入队列
     *
     * @param exceptionHolder 对象
     */
    public void put(ExceptionHolder exceptionHolder) {
        if (exceptionHolder != null) {
            exceptionsCacheQueue.offer(exceptionHolder);
        }
    }


    /**
     * 放入队列
     *
     * @param exceptionHolder 对象
     */
    public void putUpdate(ExceptionHolder exceptionHolder) {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            //ignore
        }
        if (exceptionHolder == null) {
            return;
        }
        exceptionHolder.setCounts(exceptionHolder.getCounts() + 1);
        put(exceptionHolder);
    }

    /**
     * 取
     *
     * @return 队列对象
     * @throws InterruptedException
     */
    public ExceptionHolder take() throws InterruptedException {
        return exceptionsCacheQueue.take();
    }

    @Override
    public void init() {
        exceptionsCacheQueue = new LinkedBlockingQueue<ExceptionHolder>(queueSize);
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

}
