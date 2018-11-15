package com.codeL.mc.agent.spring.aspect;

import com.codeL.mc.agent.fetch.weaver.aspect.MonitorExceptionInLogAspectWeaver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * <p>Function: [功能模块：异常信息获取拦截器]</p>
 * <p>Description: [功能描述：包括从@ControllerAdvice注解下的异常统一处理类下获取异常]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
@Component
@Aspect
@DependsOn("mcSpringClientInitManager")
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
        return MonitorExceptionInLogAspectWeaver.monitorException(pjp);
    }
}
