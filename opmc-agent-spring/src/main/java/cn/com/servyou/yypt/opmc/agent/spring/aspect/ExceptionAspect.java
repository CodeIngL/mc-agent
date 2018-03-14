package cn.com.servyou.yypt.opmc.agent.spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect.MonitorExceptionInLogAspectWeaver.monitorException;

/**
 * <p>Function: [功能模块：异常信息获取拦截器]</p>
 * <p>Description: [功能描述：包括从@ControllerAdvice注解下的异常统一处理类下获取异常]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
@Component
@Aspect
@DependsOn("opmcSpringClientInitManager")
public class ExceptionAspect {

    /**
     * 拦截ExceptionHandler注解下的异常,
     * 针对@ControllerAdvice注解下的异常统一处理类
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身抛出的异常
     */
    @Around("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public Object monitorExceptionHandler(ProceedingJoinPoint pjp) throws Throwable {
        return monitorException(pjp);
    }
}
