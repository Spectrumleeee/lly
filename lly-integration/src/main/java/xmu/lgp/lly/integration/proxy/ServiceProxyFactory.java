package xmu.lgp.lly.integration.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory<T> {
    
    public T createProxy(Class<T> serviceClazz, ServiceInvokingProxy serviceInvokingProxy) {
        ServiceInvocationHandler serviceInvocationHandler = new ServiceInvocationHandler();
        serviceInvocationHandler.setServiceInvokingProxy(serviceInvokingProxy);
        serviceInvocationHandler.setInterfaceClass(serviceClazz.getName());
        Class<?>[] interfaces = {serviceClazz};
        
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, serviceInvocationHandler);
    }
    
}
