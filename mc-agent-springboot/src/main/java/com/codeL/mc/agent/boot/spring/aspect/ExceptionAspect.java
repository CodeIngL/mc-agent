package com.codeL.mc.agent.boot.spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import static com.codeL.mc.agent.fetch.weaver.aspect.MonitorExceptionInLogAspectWeaver.monitorException;

/**
 * @author laihj
 *         2018/7/10
 */
@Aspect
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
