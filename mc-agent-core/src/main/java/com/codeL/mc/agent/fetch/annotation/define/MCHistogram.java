package com.codeL.mc.agent.fetch.annotation.define;

import com.codeL.mc.agent.fetch.divide.DivideParamParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * <p>Function: [功能模块：]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 * @date 2017/7/4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCHistogram {
    /**
     * 监控指标名
     *
     * @return String
     */
    String value() default "";

    /**
     * 按参数明细统计的参数名
     *
     * @return String
     */
    String divideParamName() default "";

    /**
     * 按参数明细统计的参数获取方式.有URL FORM PARSE三类
     *
     * @return DivideParamGetType
     */
    DivideParamGetType[] divideParamGetType() default {};

    /**
     * 参数获取的实现类,如果参数获取方式配置的是PARSE,就必须配置此参数.
     * 该类必须实现com.codeL.mc.agent.fetch.divide.DivideParamParser接口.
     *
     * @return Class
     */
    Class<? extends DivideParamParser>[] divideParamParserClass() default {};
}
