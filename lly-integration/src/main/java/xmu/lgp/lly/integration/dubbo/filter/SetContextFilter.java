package xmu.lgp.lly.integration.dubbo.filter;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.context.ContextHeaderKey;
import xmu.lgp.lly.common.context.ContextSlf4jUtil;
import xmu.lgp.lly.common.context.ServiceContext;
import xmu.lgp.lly.common.util.DateUtil;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

@Activate(group={"consumer"}, order=-5000)
public class SetContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SetContextFilter.class);
    protected static final AtomicInteger requestingCounter = new AtomicInteger(0);
    
    public static int getRequestingCounter() {
        return requestingCounter.get();
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        requestingCounter.incrementAndGet();
        ServiceContext context = ServiceContext.getContext();
        boolean genFlowFlag = false;
        long startTime = System.currentTimeMillis();
        String flowNo = null;
        
        try {
            flowNo = context.getRequestNo();
            if (flowNo == null || flowNo.isEmpty()) {
                flowNo = ServiceContext.getRandomFlowNo();
                context.addHeader(ContextHeaderKey.REQUEST_FLOWNO, flowNo);
                genFlowFlag = true;
            }
            
            String localIp = RpcContext.getContext().getLocalHost();
            String remoteIp = RpcContext.getContext().getRemoteHost();
            int port = RpcContext.getContext().getRemotePort();
            
            logger.info("[RQ-BEGIN][本地IP:{}][远程IP:{}:{}]", new Object[]{localIp, remoteIp, Integer.valueOf(port)});
            context.setRequestDateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            
            context.setInvocationInterface(invoker.getInterface().getName());
            context.setInvocationMethod(invocation.getMethodName());
            
            invocation.getAttachments().putAll(context.getCloneHeaders());
            return invoker.invoke(invocation);
        } finally {
            long endTime = System.currentTimeMillis();
            context.setInvocationInterface(invoker.getInterface().getName());
            context.setInvocationMethod(invocation.getMethodName());
            ContextSlf4jUtil.addLogKey2MDC(context);
            logger.info("{}|[RQ-END]", Long.valueOf(endTime - startTime));
            if (genFlowFlag) {
                context.removeHeader(ContextHeaderKey.REQUEST_FLOWNO);
            }
            context.removeHeader(ContextHeaderKey.INVOCATION_INTERFACE);
            context.removeHeader(ContextHeaderKey.INVOCATION_METHOD);
            ContextSlf4jUtil.addLogKey2MDC(context);
            requestingCounter.decrementAndGet();
        }
    }
    
}
