package xmu.lgp.lly.integration.proxy;

import java.lang.reflect.InvocationTargetException;

public interface ServiceInvokingProxy {
    
    public Object invoke(String arg1, String arg2, Class<?>[] clazz, Object[] obj) throws ClassNotFoundException, NoSuchMethodException, 
        SecurityException,IllegalAccessException, IllegalArgumentException,  InvocationTargetException;
    
}
