package cn.com.servyou.yypt.opmc.agent.entity;

import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.DivideParamGetType;
import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;

/**
 * <p>Function: [功能模块：明细统计的配置项实体]</p>
 * <p>Description: [功能描述：作为按参数明细统计配置的实体,配置项在注解里配置,读取注解后组装.
 * 按参数明细统计功能的作用是在某方法原有统计项的基础上,增加按参数的实际值作为key后缀的统计项.
 * 例如原有统计项为MCTimer.class.method,按参数明细统计后,它除了原有统计项之外,还会增加若干按参数和值命名的统计项.
 * 如:MCTimer.class.method.param.1,MCTimer.class.method.param.2,MCTimer.class.method.param.3 ...
 * 统计项的数量基于参数实际的值,每个实际值生成一个统计项.]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class DivideConfigInfo {

    /**
     * 按参数明细统计的参数名
     */
    private String divideParamName;

    /**
     * 参数获取方式.有URL FORM PARSE三类
     */
    private DivideParamGetType[] divideParamGetType;

    /**
     * 参数获取的实现类,如果参数获取方式配置的是PARSE,就必须配置此参数.
     * 该类必须实现cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser接口.
     */
    private Class<? extends DivideParamParser>[] divideParamParserClass;

    public DivideConfigInfo(String divideName, DivideParamGetType[] divideParamGetType, Class<? extends DivideParamParser>[] divideParamParserClass) {
        this.divideParamName = divideName;
        this.divideParamGetType = divideParamGetType;
        this.divideParamParserClass = divideParamParserClass;
    }

    public String getDivideParamName() {
        return divideParamName;
    }

    public DivideParamGetType[] getDivideParamGetType() {
        return divideParamGetType;
    }

    public Class<? extends DivideParamParser>[] getDivideParamParserClass() {
        return divideParamParserClass;
    }

}
