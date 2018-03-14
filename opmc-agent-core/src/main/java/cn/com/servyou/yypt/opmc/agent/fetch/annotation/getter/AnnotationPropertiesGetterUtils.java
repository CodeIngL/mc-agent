package cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>
 * Function: [功能模块：注解处理器]
 * </p>
 * <p>
 * Description: [功能描述：获取监控项的key值;初始化Gauge类型监控项的绑定;获取注解的对应属性;]
 * </p>
 * <p>
 * Copyright: Copyright(c) 2009-2018 税友集团
 * </p>
 * <p>
 * Company: 税友软件集团有限公司
 * </p>
 *
 * @author linj
 * @version 1.0
 * @date 2017/6/28
 */
public class AnnotationPropertiesGetterUtils {

    /**
     * 获取注解的value值
     *
     * @param annotation 注解实例
     * @param getter     注解属性获取器实例
     * @return 获取注解的value值
     */
    public static String getAnnotationValue(Annotation annotation, AnnotationPropertiesGetter getter) {
        return getter.getValue(annotation);
    }

    /**
     * 获取方法对应注解的value值
     *
     * @param method 方法实例
     * @param getter 注解属性获取器实例
     * @return 获取方法对应注解的value值
     */
    public static String getAnnotationValue(Method method, AnnotationPropertiesGetter getter) {
        //获取方法所带的指定类型的注解
        Annotation annotation = method.getAnnotation(getter.getAnnotation());
        if (annotation != null) {
            //尝试获取注解的value值
            return getter.getValue(annotation);
        }
        return null;
    }

    /**
     * 获取注解的按参数明细统计的配置项实体
     *
     * @param annotation 注解实例
     * @param getter     注解属性获取器实例
     * @return
     */
    public static DivideConfigInfo getAnnotationDivideConfig(Annotation annotation, AnnotationPropertiesGetter getter) {
        return getter.getDivideConfig(annotation);
    }

    /**
     * 获取方法对应注解的按参数明细统计的配置项实体
     *
     * @param method 方法实例
     * @param getter 注解属性获取器实例
     * @return 获取方法对应注解的按参数明细统计的配置项实体
     */
    public static DivideConfigInfo getAnnotationDivideConfig(Method method, AnnotationPropertiesGetter getter) {
        //获取方法所带的指定类型的注解
        Annotation annotation = method.getAnnotation(getter.getAnnotation());
        if (annotation != null) {
            //尝试获取注解的value值
            return getAnnotationDivideConfig(annotation, getter);
        }
        return null;
    }

}
