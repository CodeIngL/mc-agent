package com.codeL.mc.agent.boot.spring.aspect;

import com.codeL.mc.agent.entity.DivideConfigInfo;
import com.codeL.mc.agent.fetch.annotation.getter.AnnotationPropertiesGetter;
import com.codeL.mc.agent.fetch.weaver.aspect.MonitorByAnnotationAspectWeaver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>Function: [功能模块：监控有对应注解的方法内容,针对spring下的类]</p>
 * <p>Description: [功能描述：方法加上对应的注解之后,agent会使用基于spring的aop对此方法进行相应的监控]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
@Aspect
public class MonitorByAnnotationAspect {

    static final String REQUEST_METHOD_DEFAULT_VALUE = "DEFAULT";

    static final String KEY_DIVIDE = ".";

    @Value("${mc.requestMappingEnabled:false}")
    boolean requestMappingEnabled;

    public static final AnnotationPropertiesGetter REQUESTMAPPING = new AnnotationPropertiesGetter<RequestMapping>() {

        @Override
        public String getValue(RequestMapping requestMapping) {
            //RequestMapping类型的value值是以value值+分隔符+RequestMethod的name值(如空则取常量默认值)作为返回的value
            //RequestMapping的value是一个数组,这里只取第一个值作为key的开头
            String[] values = requestMapping.value();
            if (values.length == 0) {
                return null;
            }
            //取value的第一个值作为开头.
            String value = values[0];
            //取RequestMethod的值作为key的结尾
            RequestMethod requestMethod[] = requestMapping.method();
            //key值的结尾默认值取常量默认值,如果RequestMethod不为空,则取RequestMethod的第一个值
            //取RequestMethod的原因是不同的RequestMethod在spring里算作是不同的API,RequestMapping的value值是可以相等的.
            //如果单纯的只判断RequestMapping的value,那么相同RequestMapping不同RequestMethod的API会被当做是重复统计项,这是不合理的.
            //因此,把该值也作为监控项key的一部分.
            String method = REQUEST_METHOD_DEFAULT_VALUE;
            if (requestMethod.length > 0) {
                method = requestMethod[0].name();
            }
            return value + KEY_DIVIDE + method;
        }

        @Override
        public DivideConfigInfo getDivideConfig(RequestMapping requestMapping) {
            return null;
        }

        @Override
        public Class<RequestMapping> getAnnotation() {
            return RequestMapping.class;
        }
    };

    /**
     * 监控RequestMapping注解的方法
     * 此方法会记录被MCTimer注解的目标方法的执行时间 执行次数和QPS信息
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object monitorRequestMapping(ProceedingJoinPoint pjp) throws Throwable {
        if(requestMappingEnabled) {
            return MonitorByAnnotationAspectWeaver.monitorTimer(pjp, REQUESTMAPPING);
        };
        return pjp.proceed();
    }


    /**
     * 监控MCTimer注解的方法
     * 此方法会记录被MCTimer注解的目标方法的执行时间 执行次数和QPS信息
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(com.codeL.mc.agent.fetch.annotation.define.MCTimer)")
    public Object monitorTimer(ProceedingJoinPoint pjp) throws Throwable {
        return MonitorByAnnotationAspectWeaver.monitorTimer(pjp, AnnotationPropertiesGetter.MCTIMER);
    }

    /**
     * 监控MCMeter注解的方法
     * 此方法会记录被执行次数
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(com.codeL.mc.agent.fetch.annotation.define.MCMeter)")
    public Object monitorMeter(ProceedingJoinPoint pjp) throws Throwable {
        return MonitorByAnnotationAspectWeaver.monitorMeter(pjp, AnnotationPropertiesGetter.MCMETER);
    }

    /**
     * 监控MCCounter注解的方法
     * 在进入被注解的方法时,Counter会增加,在被注解的方法执行完毕之后,Counter会减少.
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(com.codeL.mc.agent.fetch.annotation.define.MCCounter)")
    public Object monitorCounter(ProceedingJoinPoint pjp) throws Throwable {
        return MonitorByAnnotationAspectWeaver.monitorCounter(pjp, AnnotationPropertiesGetter.MCCOUNTER);
    }

    /**
     * 监控MCHistogram注解的方法
     * 此方法会记录方法执行耗时
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(com.codeL.mc.agent.fetch.annotation.define.MCHistogram)")
    public Object monitorHistogram(ProceedingJoinPoint pjp) throws Throwable {
        return MonitorByAnnotationAspectWeaver.monitorHistogram(pjp, AnnotationPropertiesGetter.MCHISTOGRAM);
    }

    /**
     * 监控MCHistogram注解的方法
     * 此方法会记录方法执行耗时
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("@annotation(com.codeL.mc.agent.fetch.annotation.define.MCGauge)")
    public Object monitorGauge(ProceedingJoinPoint pjp) throws Throwable {
        return MonitorByAnnotationAspectWeaver.monitorGauge(pjp, AnnotationPropertiesGetter.MCGAUGE);
    }

}


