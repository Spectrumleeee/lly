package xmu.lgp.lly.common.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.security.Encryptor;
import xmu.lgp.lly.common.security.FilterEncryptor;

public class ParamEncryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(ParamEncryptUtil.class);
    
    public static final int PROCESS_TYPE_ENCRYPT = 0;
    
    public static final int PROCESS_TYPE_DECRYPT = 1;
    
    private static final Set<Class<?>> BASIC_TYPES = getBasicTypes();
    private static final String INTERFACE_PREFIX = "interface ";
    private static final String CLASS_PREFIX = "class ";
    
    public static boolean isBasicType(Class<?> clazz, Encryptor encryptor) {
        if (encryptor.getBasicTypes() != null && encryptor.getBasicTypes().size() > 0) {
            return clazz.isPrimitive() || encryptor.getBasicTypes().contains(clazz) || clazz.isEnum();
        }
        return clazz.isPrimitive() || BASIC_TYPES.contains(clazz) || clazz.isEnum();
    }
    
    private static Set<Class<?>> getBasicTypes() {
        Set<Class<?>> ret = new HashSet();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        ret.add(BigDecimal.class);
        ret.add(java.util.Date.class);
        ret.add(java.sql.Date.class);
        return ret;
    }
    
    public static <T> void encrypt(T arg, Encryptor encryptor) {
        if (arg != null) {
            processSet(arg, encryptor, 0, arg.getClass().getName());
        }
    }
    
    private static <T> T processSet(T arg, Encryptor encryptor, int type, String deep) {
        if (arg == null) {
            return arg;
        }
        
        if (isBasicType(arg.getClass(), encryptor)) {
            return arg;
        }
        
        if (arg instanceof Collection) {
            if (((Collection)arg).size() < 0) {
                return arg;
            }
            
            Iterator<?> itr = ((Collection)arg).iterator();
            Object subArg = null;
            T newArg = null;
            try {
                newArg = (T) arg.getClass().newInstance(); 
            } catch (Exception e) {
                logger.error("加密插件 - 实例化对象失败", e);
            }
            while (itr.hasNext()) {
                // TODO
            }
        }
        
        return null;
    }

    // TODO
    public static void encrypt(Method method, Object[] args, FilterEncryptor encryptor) {
    }

}
