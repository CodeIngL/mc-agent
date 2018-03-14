package cn.com.servyou.yypt.opmc.agent.fetch.helper.web;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.DivideParamGetType;
import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;
import cn.com.servyou.yypt.opmc.agent.fetch.helper.AspectHelper;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/9
 */
public class WebAspectHelper extends AspectHelper {

    private static final Log LOGGER = LogFactory.getLog(WebAspectHelper.class);

    @Override
    protected String doGetArgStringByDivideParam(ProceedingJoinPoint pjp, DivideConfigInfo info) {
        DivideParamGetType divideParamGetType = info.getDivideParamGetType()[0];
        //根据divideParamType的不同类型来进行不同的取数逻辑
        //如果是url,就从http request里取值
        switch (divideParamGetType) {
            case URL:
                HttpServletRequest request = getHttpServletRequestArg(pjp);
                if (request == null) {
                    break;
                }
                return request.getParameter(info.getDivideParamName());
            default:
                break;
        }
        return super.doGetArgStringByDivideParam(pjp, info);
    }


    /**
     * 获取类型为HttpServletRequest的入参
     *
     * @param pjp
     * @return HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequestArg(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    return (HttpServletRequest) arg;
                }
            }
        }
        return null;
    }
}
