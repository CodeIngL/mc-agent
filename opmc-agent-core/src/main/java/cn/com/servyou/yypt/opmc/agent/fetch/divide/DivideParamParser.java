package cn.com.servyou.yypt.opmc.agent.fetch.divide;

/**
 * <p>Function: [功能模块：按参数明细统计功能中,参数值转置的接口]</p>
 * <p>Description: [功能描述：需要使用明细统计功能的方法,在注解里配置divideParamType为PARSE时,
 * 必须增加一个实现本接口的转置类,并且在注解里配置divideParamBeanClass为转置类的class.]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public interface DivideParamParser {
    /**
     * 转置方法
     * @param params 参数
     */
    String parse(Object... params);
}
