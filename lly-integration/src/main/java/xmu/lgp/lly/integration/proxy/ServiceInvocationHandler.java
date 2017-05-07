package xmu.lgp.lly.integration.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler implements InvocationHandler {
    
    private ServiceInvokingProxy serviceInvokingProxy = null;
    private String interfaceClass = "";
    
    public ServiceInvokingProxy getServiceInvokingProxy() {
        return serviceInvokingProxy;
    }
    
    public void setServiceInvokingProxy(ServiceInvokingProxy serviceInvokingProxy) {
        this.serviceInvokingProxy = serviceInvokingProxy;
    }
    
    public String getInterfaceClass() {
        return interfaceClass;
    }
    
    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] argTypes = method.getParameterTypes();
        Object result = serviceInvokingProxy.invoke(interfaceClass, method.getName(), argTypes, args);
        return result;
    }

}
