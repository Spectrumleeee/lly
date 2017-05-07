package xmu.lgp.lly.integration.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class AbstractServiceInvokingProxy implements ServiceInvokingProxy {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    protected ApplicationContext applicationContext = null;
    
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public Object invoke(String interfaceName, String methodName, Class<?>[] argsType, Object[] argsValue) throws ClassNotFoundException, 
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        logger.debug("Proxy Invoking interfaceName={}, methodName={}", interfaceName, methodName);
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> serviceCls = classLoader.loadClass(interfaceName);
        Method method = serviceCls.getDeclaredMethod(methodName, argsType);
        
        Object targetService = applicationContext.getBean(serviceCls);
        Object rtnObj = method.invoke(targetService, argsValue);
        
        logger.debug("Proxy Invoking Done");
        return rtnObj;
    }
    

}
