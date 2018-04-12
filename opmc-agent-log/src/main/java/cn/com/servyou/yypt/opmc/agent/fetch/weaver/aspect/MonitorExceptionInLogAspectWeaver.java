package cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect;

import cn.com.servyou.yypt.opmc.agent.config.ConfigurationStateHolder;
import cn.com.servyou.yypt.opmc.agent.data.exception.ExceptionHolderDelegate;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static cn.com.servyou.yypt.opmc.agent.constant.OpmcConfigConstants.CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE;

/**
 * <p>Function: [功能模块：监控日志输出异常的拦截器]</p>
 * <p>Description: [功能描述：拦截日志的异常输出,并且进入统一的异常处理.]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 *          17/11/20
 */
@Aspect
public abstract class MonitorExceptionInLogAspectWeaver {

    private static final Log LOGGER = LogFactory.getLog(MonitorExceptionInLogAspectWeaver.class);

    /**
     * 拦截条件
     */
    @Pointcut
    public abstract void point();

    /**
     * 拦截条件定义的拦截点,然后交给异常处理方法去捕捉和处理异常
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 被拦截的方法本身的异常
     */
    @Around("point()")
    public Object aspectLogOutPutException(ProceedingJoinPoint pjp) throws Throwable {
        return monitorException(pjp);
    }

    /**
     * 异常捕获切面逻辑，无论其如何捕获异常
     *
     * @param pjp
     * @throws Throwable
     */
    public static Object monitorException(ProceedingJoinPoint pjp) throws Throwable {
        ExceptionHolderDelegate delegate = fetchExceptionHolderDelegate();
        if (delegate == null) {
            return pjp.proceed();
        }
        try {
            delegate.catchExceptionFromProceedingJoinPoint(pjp);
        } catch (Throwable e) {
            //在插件的其他地方,仍旧可以使用error级别输出,因为那些异常和错误是需要进行异常报告的.
            LOGGER.error("Error occurred on aspecting log output to catch exception.", e);
        }
        return pjp.proceed();
    }

    /**
     * @return
     */
    private static ExceptionHolderDelegate fetchExceptionHolderDelegate() {
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        return (ExceptionHolderDelegate) stateHolder.getBean(CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE);
    }

}
