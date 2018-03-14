package cn.com.servyou.yypt.opmc.agent.fetch.divide.defaultparser;

import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;

/**
 * <p>Function: [功能模块：默认转置器，适用于按参数明细统计功能，直接返回首个入参的值（String类型）]</p>
 * <p>Description: [功能描述：将方法的首个入参转置为String后直接返回，参数为基础类型的可以使用此默认转置器]</p>
 * <p>Copyright: Copyright(c) 2009-2028 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 * @date 17/12/5
 */
public class FirstInputParamParser implements DivideParamParser {
    @Override
    public String parse(Object... params) {
        //转为String，支持入参为String类型以及基础变量类型，如果是具体bean的，还请自行实现DivideParamParser接口
        return String.valueOf(params[0]);
    }
}
