package cn.com.servyou.yypt.opmc.agent.fetch.helper.struts;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.DivideParamGetType;
import cn.com.servyou.yypt.opmc.agent.fetch.helper.web.WebAspectHelper;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.MethodUtil;
import org.apache.struts.action.ActionForm;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/2/9
 */
public class StrutsWebAspectHelper extends WebAspectHelper {

    private static final Log LOGGER = LogFactory.getLog(StrutsWebAspectHelper.class);

    @Override
    protected String doGetArgStringByDivideParam(ProceedingJoinPoint pjp, DivideConfigInfo info) {
        DivideParamGetType divideParamGetType = info.getDivideParamGetType()[0];
        switch (divideParamGetType) {
            case FORM:
                ActionForm form = getActionFormArg(pjp);
                if (form == null) {
                    break;
                }
                try {
                    //form取值使用反射取值,要求form类有对应的get方法
                    return MethodUtil.invokeGetMethod(form, info.getDivideParamName()).toString();
                } catch (Exception e) {
                    LOGGER.error("Exception occurred on getting filed's value from ActionForm, name of ActionForm is :[" + form.toString() + "], name of filed is :[" + info.getDivideParamName() + "]", e);
                }
                break;
            default:
                break;
        }
        return super.doGetArgStringByDivideParam(pjp, info);
    }

    /**
     * 获取类型为ActionForm的入参
     *
     * @param pjp
     * @return ActionForm
     */
    private ActionForm getActionFormArg(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof ActionForm) {
                    return (ActionForm) arg;
                }
            }
        }
        return null;
    }

}
