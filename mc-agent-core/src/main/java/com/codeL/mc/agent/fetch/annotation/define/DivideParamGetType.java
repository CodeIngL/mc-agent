package com.codeL.mc.agent.fetch.annotation.define;

/**
 * <p>Function: [功能模块：按参数明细统计的参数获取方式enum]</p>
 * <p>Description: [功能描述：用于在注解里声明按参数统计时的参数获取方式
 * URL:参数包含在URL中,插件将会从HttpServletRequest中获取名称为divideParamName值的参数
 * FORM:参数包含在form中,插件将会从form中获取名称为divideParamName值的参数,需要form里有该参数的get方法
 * PARSE:参数不能被直接取到,需要进行转换.此方式下,需要使用者提供一个实现了DivideParamParser接口的转置类,并且在注解里声明该类的class.
 * 插件将会执行该类的parse方法来获取转置后的参数.]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 */
public enum DivideParamGetType {
    /**
     * web专用
     */
    URL,
    /**
     * struts专用
     */
    FORM,
    /**
     * 动态解析策略
     */
    PARSE
}
