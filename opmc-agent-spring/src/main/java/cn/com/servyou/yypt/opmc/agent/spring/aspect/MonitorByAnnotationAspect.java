package cn.com.servyou.yypt.opmc.agent.spring.aspect;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter.AnnotationPropertiesGetter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;

import static cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect.MonitorByAnnotationAspectWeaver.monitorTimer;

/**
 * <p>Function: [功能模块：监控有对应注解的方法内容,针对spring下的类]</p>
 * <p>Description: [功能描述：方法加上对应的注解之后,agent会使用基于spring的aop对此方法进行相应的监控]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
@Component
@Aspect
@DependsOn("opmcSpringClientInitManager")
public class MonitorByAnnotationAspect {

    static final String REQUEST_METHOD_DEFAULT_VALUE = "DEFAULT";

    static final String KEY_DIVIDE = ".";

    public static final AnnotationPropertiesGetter REQUESTMAPPING = new AnnotationPropertiesGetter<RequestMapping>() {

        @Override
        public String getValue(RequestMapping requestMapping) {
            //RequestMapping类型的value值是以value值+分隔符+RequestMethod的name值(如空则取常量默认值)作为返回的value
            //RequestMapping的value是一个数组,这里只取第一个值作为key的开头
            String[] values = requestMapping.value();
            if (values == null || values.length == 0) {
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
        return monitorTimer(pjp, REQUESTMAPPING);
    }
}


