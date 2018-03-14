package cn.com.servyou.yypt.opmc.agent.fetch.annotation.define;

import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;

import java.lang.annotation.*;

/**
 * <p>Function: [功能模块：]</p>
 * <p>Description: [功能描述：]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
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
     * 该类必须实现cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser接口.
     *
     * @return Class
     */
    Class<? extends DivideParamParser>[] divideParamParserClass() default {};
}
