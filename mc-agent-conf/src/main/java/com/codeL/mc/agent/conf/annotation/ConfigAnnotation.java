package com.codeL.mc.agent.conf.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Description: 系统配置注解</p>
 * <p></p>
 *
 * @author laihj
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigAnnotation {

    /**
     * @return 字段名内部
     */
    String name();

    /**
     * @return 字段值
     */
    String value() default "";

    /**
     * @return 是否必要
     */
    boolean required() default true;

}
