package com.codeL.mc.agent.data.exception;

import com.codeL.mc.agent.conf.annotation.ConfigAnnotation;
import com.codeL.mc.agent.config.Configuration;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.*;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_CONFIGURATION;
import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY;

/**
 * <p>Function: [功能模块：异常抓取的工具类]</p>
 * <p>Description: [功能描述：异常抓取,异常判断,异常信息组装,异常放置至缓存]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 *          2017/7/31
 */
public class ExceptionHolderDelegate {

    private static final Log LOGGER = LogFactory.getLog(ExceptionHolderDelegate.class);

    /**
     * 异常持有注册表
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_EXCEPTION_HOLDER_REGISTRY)
    private ExceptionHolderRegistry exceptionHolderRegistry;

    /**
     * 配置项
     */
    @ConfigAnnotation(name = CLASS_INTERNAL_CONFIGURATION)
    private Configuration configuration;

    /**
     * 从ProceedingJoinPoint切片实例中抓取异常信息
     *
     * @param pjp 切片信息
     */
    public void catchExceptionFromProceedingJoinPoint(ProceedingJoinPoint pjp) {
        //获取异常拦截器的参数,循环其参数,获取其Exception类型的参数,进行下一步的操作
        Object[] args = pjp.getArgs();
        Object[] nestedArgs = null;
        for (int i = args.length - 1; i > 0; i--) {
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            if (i == args.length - 1 && arg instanceof Object[]) {
                nestedArgs = (Object[]) arg;
                break;
            }
            if (arg instanceof Throwable) {
                //如果参数是异常类型的,处理这个异常
                if (exceptionMatchedInRule(arg.getClass().getSimpleName())) {
                    exceptionHolderRegistry.put(new ExceptionHolder((Throwable) arg, System.currentTimeMillis()));
                }
                return;
            }
        }
        if (nestedArgs == null || nestedArgs.length == 0) {
            return;
        }
        for (int i = nestedArgs.length - 1; i > 0; i--) {
            Object arg = nestedArgs[i];
            if (arg == null) {
                continue;
            }
            if (arg instanceof Throwable) {
                //如果参数是异常类型的,处理这个异常
                if (exceptionMatchedInRule(arg.getClass().getSimpleName())) {
                    exceptionHolderRegistry.put(new ExceptionHolder((Throwable) arg, System.currentTimeMillis()));
                }
                return;
            }
        }
    }

    /**
     * 放入异常
     *
     * @param throwable 异常
     */
    public void catchExceptionFrom(Throwable throwable) {
        //如果参数是异常类型的,处理这个异常
        if (!exceptionMatchedInRule(throwable.getClass().getSimpleName())) {
            return;
        }
        exceptionHolderRegistry.put(new ExceptionHolder(throwable, System.currentTimeMillis()));
    }


    /**
     * 判断异常是否符合include和exclude规则
     *
     * @param exceptionName 异常名
     * @return
     */
    private boolean exceptionMatchedInRule(String exceptionName) {
        if (!configuration.isEnable()) {
            return false;
        }
        if (configuration.isCatchAll()) {
            Collection<String> excludes = configuration.getExceptionExcludes();
            if (excludes != null && excludes.size() > 0) {
                return !excludes.contains(exceptionName);
            }
            return true;
        }
        //include优先级较高,如果配置了include,那么只有在include里的才符合需求
        Collection<String> includes = configuration.getExceptionIncludes();
        if (includes != null && includes.size() > 0) {
            return includes.contains(exceptionName);
        }
        //都没配置的话,通过
        return true;
    }

    //------------get---------set----------

    public ExceptionHolderRegistry getExceptionHolderRegistry() {
        return exceptionHolderRegistry;
    }

    public void setExceptionHolderRegistry(ExceptionHolderRegistry exceptionHolderRegistry) {
        this.exceptionHolderRegistry = exceptionHolderRegistry;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
