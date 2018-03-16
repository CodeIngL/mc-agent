package cn.com.servyou.yypt.opmc.agent.task.reporter;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.yypt.opmc.agent.config.Configuration;
import cn.com.servyou.yypt.opmc.agent.constant.Constants;
import cn.com.servyou.yypt.opmc.agent.data.exception.ExceptionHolderRegistry;
import cn.com.servyou.yypt.opmc.agent.data.cache.SystemPropertiesRegistry;
import cn.com.servyou.yypt.opmc.agent.data.exception.ExceptionHolder;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.HttpUtils;
import cn.com.servyou.yypt.opmc.agent.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY;

/**
 * <p>Function: [功能模块：异常报告器]</p>
 * <p>Description: [功能描述：从异常抓取的缓存中,获取待发送的异常信息,然后通过http接口,向opmc-server报告异常消息]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class ExceptionReporter implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ExceptionReporter.class);

    /**
     * 异常报告rest接口的URL
     */
    private static final String EXCEPTION_REPORT_URL = "/exception/insert";

    /**
     * 抓取到的异常map的最大容量,定为100
     */
    private static final int EXCEPTION_CAUGHT_MAP_SIZE = 100;

    /**
     * 抓取到的异常map里记录的超时时间(毫秒),定为30秒
     */
    private static final Long EXCEPTION_CAUGHT_MAP_RECORD_EXPIRE_TIME_MILLS = 30000L;

    /**
     * 格式化
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 缓存
     */
    private Map<String, Long> exceptionCaughtMap = new HashMap<String, Long>();

    /**
     * 配置类
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_CONFIGURATION)
    private Configuration configuration;

    /**
     * 注册表
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY)
    private ExceptionHolderRegistry exceptionHolderRegistry;

    @Override
    public void run() {
        while (true) {
            if (!checkAvailableAndDelay()) {
                continue;
            }
            ExceptionHolderRegistry registry = exceptionHolderRegistry;
            clearExpireRecordsInExceptionCaughtMap();

            ExceptionHolder exceptionHolder = null;
            try {
                exceptionHolder = registry.take();
                if (!isExceptionNeedToSend(exceptionHolder.getThrowable())) {
                    continue;
                }
                String result = HttpUtils.postForm(configuration.getServerUrl() + EXCEPTION_REPORT_URL, formatException(exceptionHolder), "utf-8");
                if (StringUtils.isEmpty(result)) {
                    registry.putUpdate(exceptionHolder);
                }
            } catch (IOException e) {
                LOGGER.warn("IOException occurred on sending exception info to OPMC server. exception info will be put back to cache and wait several seconds.! ", e);
                registry.putUpdate(exceptionHolder);
            } catch (InterruptedException e) {
                LOGGER.warn("", e);
            } catch (Throwable e) {
                LOGGER.warn("Exception occurred on sending a exception ! ", e);
            }
        }
    }

    /**
     * 把异常信息组装后放入到待发送队列中
     *
     * @param exceptionHolder 异常信息
     */
    private Map<String, String> formatException(ExceptionHolder exceptionHolder) {
        Map<String, String> exceptionMap = new HashMap<String, String>();
        exceptionMap.put(Constants.EXCEPTION_KEY_CREATE_DATE, simpleDateFormat.format(exceptionHolder.getTimestamp()));
        exceptionMap.put(Constants.HOST_NAME, SystemPropertiesRegistry.getHostName());
        exceptionMap.put(Constants.EXCEPTION_KEY_IP, SystemPropertiesRegistry.getAllIp());
        exceptionMap.put(Constants.EXCEPTION_KEY_MESSAGE, packageExceptionMessage(exceptionHolder.getThrowable()));
        return exceptionMap;
    }

    /**
     * 读取异常信息,包装为要发送的格式
     *
     * @param throwable 异常实例
     * @return
     */
    private String packageExceptionMessage(Throwable throwable) {
        StringBuilder content = new StringBuilder();
        content.append("异常的类型为:" + throwable.getClass() + "\t\n");
        content.append("异常信息为:" + throwable.getMessage() + "\t\n");
        content.append("详细原因为:\t\n\n");
        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            content.append("\t  " + traceElement + "\t\n");
        }
        return content.toString();
    }

    /**
     * 判断该异常信息是否需要发送告警
     * 通过操作异常抓取缓存map来判断
     * 如果以异常名为key的记录已存在,则不需告警
     * 如果缓存map的大小达到了预设值,也不需告警
     * 其他情况将异常名作为key,系统当前毫秒数作为value放入map,返回需要告警
     *
     * @param throwable 异常(错误)具体类 这里使用Throwable而不是Exception是为了保持对Error的适配
     * @return 返回放置结果, true需要告警;false不需要
     */
    private boolean isExceptionNeedToSend(Throwable throwable) {
        String throwableName = throwable.getClass().getName();
        //如果异常在map中已经存在,那么返回false.
        if (exceptionCaughtMap.containsKey(throwableName)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("The Exception:[" + throwableName + "] has already exists in cache,it will not be put into cache this time.");
            }
            return false;
        } else {
            //如果异常并不存在,判断map的大小是否达到超过了预设值,如果达到甚至超过了预设值,则返回false.
            if (exceptionCaughtMap.size() >= EXCEPTION_CAUGHT_MAP_SIZE) {
                LOGGER.debug("The size of exception caught cache had exceeded default value,no exception will be put into it this time");
                return false;
            } else {
                exceptionCaughtMap.put(throwableName, System.currentTimeMillis());
                return true;
            }
        }
    }

    /**
     * 清除异常抓取缓存map里的超时记录
     * 循环开始之前,会取系统当前毫秒数,作为计算的基准毫秒数.
     * 遍历map,当记录的value值与超时时间之和小于基准毫秒数时,则说明该记录已超时,需要删除
     */
    private void clearExpireRecordsInExceptionCaughtMap() {
        //遍历异常抓取缓存map,删除已经超时的记录,即value值+超时间隔<系统当前毫秒数
        //使用迭代器Iterator来满足将要进行的删除操作,使用entrySet来一次获取key和value
        Iterator iterator = exceptionCaughtMap.entrySet().iterator();
        //获取当前系统毫秒数作为基准毫秒数,作为删除的计算依据
        Long baseTimeMills = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = (Map.Entry<String, Long>) iterator.next();
            //value值为存入时间的毫秒数,如果加上超时时间,仍旧小于基准毫秒数,则该记录已超时,需要删除.否则不做任何操作
            if (baseTimeMills >= entry.getValue() + EXCEPTION_CAUGHT_MAP_RECORD_EXPIRE_TIME_MILLS) {
                iterator.remove();
            }
        }
    }

    /**
     * 是否工作，并且延迟一段时间
     *
     * @return
     */
    private boolean checkAvailableAndDelay() {
        boolean enable = configuration.isEnable();
        if (enable) {
            return true;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //ignore
        }
        return false;
    }

    //------------get---------set----------

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ExceptionHolderRegistry getExceptionHolderRegistry() {
        return exceptionHolderRegistry;
    }

    public void setExceptionHolderRegistry(ExceptionHolderRegistry exceptionHolderRegistry) {
        this.exceptionHolderRegistry = exceptionHolderRegistry;
    }
}
