package cn.com.servyou.opmc.agent.conf.annotation;

import java.lang.annotation.*;

/**
 *
 * <p>Description: 系统配置注解</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigAnnotation {

    /**
     * 字段名内部
     */
    String name();

    /**
     * 字段值
     */
    String value() default "";

    /**
     * 是否必要
     */
    boolean required() default true;

}
