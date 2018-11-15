package com.codeL.mc.agent.fetch.annotation.define;

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
public @interface MCGauge {
    /**
     * 监控指标名
     *
     * @return String
     */
    String value() default "";
}
