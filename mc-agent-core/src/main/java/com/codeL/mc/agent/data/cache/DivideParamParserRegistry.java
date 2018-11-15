package com.codeL.mc.agent.data.cache;

import com.codeL.mc.agent.fetch.divide.DivideParamParser;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Function: [功能模块：按参数明细统计的Bean缓存器]</p>
 * <p>Description: [功能描述：存储在按参数明细统计功能里,参数类型为PARSE的情况下,对指定的bean创建的实例.
 * 省去每次拦截时都需要newInstance的开销.
 * 在该功能里,如果参数类型是PARSE的话,插件就需要执行一把该bean的对应parse方法来获取经过处理后的作为统计的参数值.
 * 接口的方法无法声明为static,所以需要实例化实现类bean才能进行方法的执行.]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
public class DivideParamParserRegistry {

    /**
     * 缓存转置类实例的map
     * 转置类必须实现DivideParamParser接口
     */
    private final Map<String, DivideParamParser> beanMap = new HashMap<String, DivideParamParser>();

    /**
     * 获取转置类实例
     *
     * @param divideParamParserClass 转置类的class
     * @return DivideParamParser
     * @throws ClassNotFoundException 类不存在异常
     * @throws IllegalAccessException 无效连接异常
     * @throws InstantiationException 初始化异常
     */
    public DivideParamParser getBean(Class divideParamParserClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        DivideParamParser bean = beanMap.get(divideParamParserClass.getName());
        if (bean == null) {
            bean = (DivideParamParser) divideParamParserClass.newInstance();
            beanMap.put(divideParamParserClass.getName(), bean);
        }
        return bean;
    }

}
