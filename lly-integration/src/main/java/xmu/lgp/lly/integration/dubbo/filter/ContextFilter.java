package xmu.lgp.lly.integration.dubbo.filter;

import xmu.lgp.lly.common.context.ContextSlf4jUtil;
import xmu.lgp.lly.common.context.ServiceContext;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

@Activate(group={"provider"}, order=-5000)
public class ContextFilter implements Filter {
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            ServiceContext context = ServiceContext.getContext();
            context.clearHeader();
            context.addHeaders(invocation.getAttachments());
            resetIp(context);
            context.setInvocationInterface(invoker.getInterface().getName());
            context.setInvocationMethod(invocation.getMethodName());
            ContextSlf4jUtil.addLogKey2MDC(context);
            return invoker.invoke(invocation);
        } finally {
            ServiceContext.removeContext();
            ContextSlf4jUtil.rmvLogKeyFromMDC();
        }
    }
    
    private void resetIp(ServiceContext context) {
        context.setLocalIp(RpcContext.getContext().getLocalHost());
        context.setConsumerIp(RpcContext.getContext().getRemoteHost());
    }

}
