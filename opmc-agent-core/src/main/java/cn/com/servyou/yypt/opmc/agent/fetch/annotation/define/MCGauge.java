package cn.com.servyou.yypt.opmc.agent.fetch.annotation.define;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

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
public @interface MCGauge {
    /**
     * 监控指标名
     *
     * @return String
     */
    String value() default "";
}
