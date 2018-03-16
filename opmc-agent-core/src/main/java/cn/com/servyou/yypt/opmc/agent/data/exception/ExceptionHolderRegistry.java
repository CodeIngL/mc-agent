package cn.com.servyou.yypt.opmc.agent.data.exception;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.init.Initializer;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;

import java.util.concurrent.*;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.OPMC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE;

/**
 * <p>Function: [功能模块：抓取到的异常缓存]</p>
 * <p>Description: [功能描述：存储已抓取的异常并且判断是否需要发送,异常报告器会从待发送队列中获取异常进行发送]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class ExceptionHolderRegistry implements Initializer {

    private static final Log LOGGER = LogFactory.getLog(ExceptionHolderRegistry.class);

    /**
     * 待发送的异常队列的大小,定为1000
     */
    @ConfigAnnotation(name = OPMC_USER_CONFIG_EXCEPTION_HOLDER_QUEUE_SIZE)
    private int queueSize;

    private ThreadPoolExecutor executor;

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
        if (exceptionHolder == null) {
            return;
        }
        if (executor != null) {
            executor.submit(new ExceptionTask(exceptionHolder, exceptionsCacheQueue));
            return;
        }
        exceptionsCacheQueue.offer(exceptionHolder);
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
        executor = new ThreadPoolExecutor(3, 3, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    static class ExceptionTask implements Runnable {

        private static final Log LOGGER = LogFactory.getLog(ExceptionTask.class);

        /**
         * 移除
         */
        private ExceptionHolder exceptionHolder;

        /**
         * 队列
         */
        private LinkedBlockingQueue<ExceptionHolder> queue;

        public ExceptionTask(ExceptionHolder exceptionHolder, LinkedBlockingQueue<ExceptionHolder> queue) {
            this.exceptionHolder = exceptionHolder;
            this.queue = queue;
        }

        @Override
        public void run() {
            if (exceptionHolder == null) {
                queue = null;
                return;
            }
            if (queue == null) {
                return;
            }
            int count = exceptionHolder.getCounts();
            try {
                switch (count) {
                    case 1:
                        Thread.sleep(1000);
                        break;
                    case 2:
                        Thread.sleep(3000);
                        break;
                    case 3:
                        Thread.sleep(5000);
                        break;
                    case 4:
                        LOGGER.warn("exception send fail fourth,name is " +
                                exceptionHolder.getThrowable().getClass().getName());
                        break;
                }
                exceptionHolder.setCounts(count + 1);
                if (count < 4) {
                    queue.offer(exceptionHolder);
                }

            } catch (Exception e) {
            } finally {
                queue = null;
                exceptionHolder = null;
            }
        }
    }
}
