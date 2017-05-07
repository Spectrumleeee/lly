package xmu.lgp.lly.common.context;

import org.slf4j.MDC;

public class ContextSlf4jUtil {
    
    private static final String DEFAULT_MDC_VALUE = "NA";
    
    public static void addLogKey2MDC(ServiceContext context) {
        if (context == null) {
            return;
        }
        
        MDC.put(ContextHeaderKey.REQUEST_FLOWNO, context.getRequestNo() == null ? DEFAULT_MDC_VALUE : context.getRequestNo());
        MDC.put(ContextHeaderKey.CONSUMER_IP, context.getConsumerIp() == null ? DEFAULT_MDC_VALUE : context.getConsumerIp());
        MDC.put(ContextHeaderKey.CHANNEL_CODE, context.getChannelCode() == null ? DEFAULT_MDC_VALUE : context.getChannelCode());
        MDC.put(ContextHeaderKey.INVOCATION_INTERFACE, context.getInvocationInterface() == null ? DEFAULT_MDC_VALUE : context.getInvocationInterface());
        MDC.put(ContextHeaderKey.INVOCATION_METHOD, context.getInvocationMethod() == null ? DEFAULT_MDC_VALUE : context.getInvocationMethod());
    }

    public static void rmvLogKeyFromMDC() {
        MDC.remove(ContextHeaderKey.REQUEST_FLOWNO);
        MDC.remove(ContextHeaderKey.CONSUMER_IP);
        MDC.remove(ContextHeaderKey.CHANNEL_CODE);
        MDC.remove(ContextHeaderKey.INVOCATION_INTERFACE);
        MDC.remove(ContextHeaderKey.INVOCATION_METHOD);
    }
    
}
