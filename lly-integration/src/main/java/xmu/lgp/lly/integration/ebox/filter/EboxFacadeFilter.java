package xmu.lgp.lly.integration.ebox.filter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.context.ServiceContext;

public class EboxFacadeFilter implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(EboxFacadeFilter.class);
    
    private static Object target;
    
    @SuppressWarnings("unchecked")
    public static <T> T bind(T target) {
        EboxFacadeFilter filter = new EboxFacadeFilter();
        EboxFacadeFilter.target = target;
        Class<?> clazz = target.getClass();
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), filter);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String flowNo = ServiceContext.getContext().getRequestNo();
        if (flowNo == null) {
            flowNo = ServiceContext.getRandomFlowNo();
        }
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        logger.info("[EBOX-BEGIN][服务:{}.{}][流水号:{}]", new Object[]{className, methodName, flowNo});
        long startMills = System.currentTimeMillis();
        Object result = null;
        try {
            result = method.invoke(EboxFacadeFilter.target, args);
        } finally {
            logger.info("[EBOX-END][流水号:{}][耗时：time={} ms]", flowNo, Long.valueOf(System.currentTimeMillis() - startMills));
        }
        
        return result;
    }
    
}
