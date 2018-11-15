package com.codeL.mc.agent.dubbo.filter;

import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.data.exception.ExceptionHolderDelegate;
import com.codeL.mc.agent.log.Log;
import com.codeL.mc.agent.log.LogFactory;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.service.GenericService;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE;

/**
 * <p>Function: [功能模块：used to catch dubbo exception]</p>
 * <p>Description: [功能描述：get dubbo exception include server and consumer.
 * add filter on dubbo config xml like
 * <dubbo:consumer check="false" filter="expFilter"/>
 * when used on a consumer]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 * @date 2017/7/28
 * @see
 */
//group配置成提供者和消费者都有效.linjingb
@Activate(
        group = {"provider", "consumer"}
)
public class DubboExceptionFilter implements Filter {


    private static final Log LOGGER = LogFactory.getLog(DubboExceptionFilter.class);

    private ExceptionHolderDelegate getExceptionHolderDelegate() {
        ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
        ExceptionHolderDelegate holderDelegate = (ExceptionHolderDelegate) stateHolder.getBean(CLASS_INTERNAL_EXCEPTION_HOLDER_DELEGATE);
        return holderDelegate;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result e = invoker.invoke(invocation);
            if (e.hasException() && GenericService.class != invoker.getInterface()) {
                try {
                    Throwable exception = e.getException();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Dubbo exception:" + exception.getMessage());
                    }
                    ExceptionHolderDelegate holderDelegate = getExceptionHolderDelegate();
                    if (holderDelegate == null) {
                        return e;
                    }
                    holderDelegate.catchExceptionFrom(exception);
                    return e;
                } catch (Throwable var12) {
                    LOGGER.warn("异常过滤失败: " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + var12.getClass().getName() + ": " + var12.getMessage(), var12);
                    return e;
                }
            } else {
                return e;
            }
        } catch (RuntimeException var13) {
            LOGGER.warn("出现了未声明的异常: " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + var13.getClass().getName() + ": " + var13.getMessage(), var13);
            throw var13;
        }
    }

}
