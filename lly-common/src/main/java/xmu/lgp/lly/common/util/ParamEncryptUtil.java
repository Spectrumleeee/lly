package xmu.lgp.lly.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.annotation.Encrypt;
import xmu.lgp.lly.common.entity.BaseEntity;
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
    
    public static <T> void decrypt(T arg, Encryptor encryptor) {
        if (arg != null) {
            processSet(arg, encryptor, 1, arg.getClass().getName());
        }
    }
    
    public static <T> T encrypt(T arg, Encryptor encryptor, Map<String, String> encryptContext) {
        return (T)process(arg, encryptor, encryptContext, 0);
    }
    
    public static <T> T decrypt(T arg, Encryptor encryptor, Map<String, String> encryptContext) {
        return (T)process(arg, encryptor, encryptContext, 1);
    }
    
    private static <T> T process(T arg, Encryptor encryptor, Map<String, String> encryptContext, int type) {
        if (arg != null) {
            return (T)processAnnotationArg(arg, encryptor, type, encryptContext, new LinkedList());
        }
        return null;
    }
    
    public static <T> Object encrypt(Method method, T arg, Encryptor encryptor) {
        return processMethodRet(method, arg, encryptor, 0);
    }
    
    public static <T> Object decrypt(Method method, T arg, Encryptor encryptor) {
        return processMethodRet(method, arg, encryptor, 1);
    }
    
    public static <T> T processMethodRet(Method method, T arg, Encryptor encryptor, int processType) {
        Encrypt encryptAnn = (Encrypt)method.getAnnotation(Encrypt.class);
        String contextKey = method.getDeclaringClass() + "." + method.getName() + "#return";
        contextKey = removeInterfacePrefix(contextKey);
        Map<String, String> encryptContext = encryptor.getEncryptContext(encryptAnn, contextKey);
        
        return (T)processAnnotationArg(arg, encryptor, processType, encryptContext, new LinkedList());
    }
    
    private static String removeInterfacePrefix(String contextKey) {
        return contextKey.substring("interface ".length());
    }
    
    public static <T> void encrypt(T[] args, Encryptor encryptor) {
        if (args != null) {
            for (int i=0; i < args.length; i++) {
                if (args[i] != null) {
                    args[i] = processSet(args[i], encryptor, 0, args[i].getClass().getName());
                }
            }
        }
    }
    
    public static <T> void decrypt(T[] args, Encryptor encryptor) {
        if (args != null) {
            for (int i=0; i< args.length; i++) {
                if (args[i] != null) {
                    args[i] = processSet(args[i], encryptor, 1, args[i].getClass().getName());
                }
            }
        }
    }
    
    public static <T> void encrypt(Method method, T[] args, Encryptor encryptor) {
        processMethodArgs(method, args, encryptor, 0);
    }
    
    public static <T> void decrypt(Method method, T[] args, Encryptor encryptor) {
        processMethodArgs(method, args, encryptor, 1);
    }
    
    private static <T> void processMethodArgs(Method method, T[] args, Encryptor encryptor, int processType) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Object> processedObjects = new LinkedList();
        if (args != null) {
            for (int i=0; i<args.length; i++) {
                if (args[i] != null) {
                    boolean isEncryptAnnotationPresent = false;
                    for (Annotation annotation : parameterAnnotations[i]) {
                        if (annotation instanceof Encrypt) {
                            isEncryptAnnotationPresent = true;
                            String contextKey = method.getDeclaringClass() + "." + method.getName() + "#arg" + i;
                            contextKey = removeInterfacePrefix(contextKey);
                            Map<String, String> encryptContext = encryptor.getEncryptContext((Encrypt)annotation, contextKey);
                            args[i] = processAnnotationArg(args[i], encryptor, processType, encryptContext, processedObjects);
                        }
                    }
                    if (!isEncryptAnnotationPresent) {
                        String contextKey = method.getDeclaringClass() + "." + method.getName() + "#arg" + i;
                        contextKey = removeInterfacePrefix(contextKey);
                        Map<String, String> encryptContext = encryptor.getEncryptContext(null, contextKey);
                        args[i] = processAnnotationArg(args[i], encryptor, processType, encryptContext, processedObjects);
                    }
                }
            }
        }
    }
    
    public static <T> T processDTO(T arg, Encryptor encryptor, int type, List<Object> processedObjects) {
        for (Class<?> clazz = arg.getClass(); clazz != BaseEntity.class && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                String contextKey = field.getDeclaringClass() + "#" + field.getName();
                contextKey = removeClassPrefix(contextKey);
                Map<String, String> encryptContext = encryptor.getEncryptContext((Encrypt)field.getAnnotation(Encrypt.class), contextKey);
                
                if (encryptContext != null || !isBasicType(field.getType(), encryptor)) {
                    try {
                        Object subArg = PropertyUtils.getSimpleProperty(arg, field.getName());
                        if (subArg != null) {
                            if (type == 1 && encryptor.isDebug()) {
                                logger.debug("加密插件 -- 对{}字段解密，解密前值为{}，使用context={}", new Object[]{contextKey, subArg, encryptContext});
                            }
                            subArg = processAnnotationArg(subArg, encryptor, type, encryptContext, processedObjects);
                            PropertyUtils.setSimpleProperty(arg,  field.getName(), subArg);
                            if (type == 0 && encryptor.isDebug()) {
                                logger.debug("加密插件 -- 对{}字段加密，加密后值为{}", new Object[]{contextKey, subArg, contextKey});
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        logger.warn("加密插件 -- 字段{} 没有getter或者setter方法，跳过不处理", contextKey);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return arg;
    }
    
    private static <T> T processAnnotationArg(T arg, Encryptor encryptor, int type, Map<String, String> encryptContext, List<Object> processedObjects) {
        boolean isBasicType = isBasicType(arg.getClass(), encryptor);
        if (!isBasicType) {
            for (Object obj : processedObjects) {
                if (obj == arg) {
                    return arg;
                }
            }
            processedObjects.add(arg);
        }
        if (encryptContext != null) {
            if (isBasicType) {
                if (encryptor.isDebug() && type == 1) {
                    logger.debug("使用context={} 解密字段，解密前结果为{}", encryptContext, arg);
                }
                T ret = processBasicType(arg, encryptor, type, encryptContext);
                if (encryptor.isDebug() && type == 0) {
                    logger.debug("使用context={} 加密字段，加密后结果为{}", encryptContext, ret);
                }
                return ret;
            }
            if (arg instanceof Collection) {
                return (T) processCollection((Collection)arg, encryptor, type, encryptContext, processedObjects);
            }
            if (arg.getClass().isArray()) {
                return (T)processArray(arg, encryptor, type, encryptContext, processedObjects);
            }
            if (arg instanceof Map) {
                return (T) processMap((Map)arg, encryptor, type, encryptContext, processedObjects);
            }
            return (T)processDTO(arg, encryptor, type, processedObjects);
        }
        
        if (isBasicType) {
            return arg;
        }
        if (arg instanceof Collection) {
            return (T) processCollection((Collection)arg, encryptor, type, encryptContext, processedObjects);
        }
        if (arg.getClass().isArray()) {
            return (T)processArray(arg, encryptor, type, encryptContext, processedObjects);
        }
        if (arg instanceof Map) {
            return (T) processMap((Map)arg, encryptor, type, encryptContext, processedObjects);
        }
        return (T)processDTO(arg, encryptor, type, processedObjects);
    }
    
    private static <T> T processBasicType(T arg, Encryptor encryptor, int type, Map<String, String> encryptContext) {
        if (0 == type) {
            try {
                return (T)encryptor.encrypt(arg, encryptContext);
            } catch (Exception e) {
                throw new RuntimeException("加密处理异常，处理前值为：" + arg + ", encryptContext为：" + encryptContext, e);
            }
        }
        try {
            return (T)encryptor.decrypt(arg, encryptContext);
        } catch (Exception e) {
            throw new RuntimeException("解密处理异常，处理前值为：" + arg + ", encryptContext为" + encryptContext, e);
        }
    }
    
    private static <E> Collection<E> processCollection(Collection<E> arg, Encryptor encryptor, int type, Map<String, String> encryptContext, List<Object> processedObjects) {
        if (arg.isEmpty()) {
            return arg;
        }
        
        Collection<E> newArg = null;
        boolean isFirstElement = true;
        for (E subArg : arg) {
            if (null != subArg) {
                if (isFirstElement) {
                    isFirstElement = false;
                    if (encryptContext == null && isBasicType(subArg.getClass(), encryptor)) {
                        return arg;
                    }
                }
                if (newArg == null) {
                    try {
                        newArg = (Collection)arg.getClass().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("加密插件 - 实例化对象失败", e);
                    }
                }
                newArg.add(processAnnotationArg(subArg, encryptor, type, encryptContext, processedObjects));
            }
        }
        return newArg;
    }
    
    private static Object processArray(Object arg, Encryptor encryptor, int type, Map<String, String> encryptContext, List<Object> processedObjects) {
        if (Array.getLength(arg) <= 0) {
            return arg;
        }
        
        boolean isFirstElement = true;
        for (int i=0; i<Array.getLength(arg); i++) {
            Object value = Array.get(arg, i);
            if (null != value) {
                if (isFirstElement) {
                    isFirstElement = false;
                    if (encryptContext == null && isBasicType(value.getClass(), encryptor)) {
                        return arg;
                    }
                }
                Array.set(arg, i, processAnnotationArg(value, encryptor, type, encryptContext, processedObjects));
            }
        }
        return arg;
    }
    
    private static <E, S> Map<E, S> processMap(Map<E, S> arg, Encryptor encryptor, int type, Map<String, String> encryptContext, List<Object> processedObjects) {
        if (arg.isEmpty()) {
            return arg;
        }
        
        boolean isFirstElement = true;
        for (Map.Entry<E, S> entry : arg.entrySet()) {
            S subArg = entry.getValue();
            if (null != subArg) {
                if (isFirstElement) {
                    isFirstElement = false;
                    if (encryptContext == null && isBasicType(subArg.getClass(), encryptor)) {
                        return arg;
                    }
                }
                arg.put(entry.getKey(), processAnnotationArg(subArg, encryptor, type, encryptContext, processedObjects));
            }
        }
        return arg;
    }
    
    private static String removeClassPrefix(String contextKey) {
        return contextKey.substring("class ".length());
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
                subArg = itr.next();
                if (subArg != null) {
                    if (isBasicType(subArg.getClass(), encryptor)) {
                        return arg;
                    }
                    ((Collection)newArg).add(processSet(subArg, encryptor, type, deep + "." + subArg.getClass().getName()));
                }
            }
            return newArg;
        }
        
        if (arg.getClass().isArray()) {
            if (Array.getLength(arg) < 0) {
                return arg;
            }
            
            Object[] values = new Object[Array.getLength(arg)];
            for (int i = 0; i < Array.getLength(arg); i++) {
                if (Array.get(arg, i) != null) {
                    if (isBasicType(Array.get(arg, i).getClass(), encryptor)) {
                        return arg;
                    }
                    values[i] = processSet(Array.get(arg, i), encryptor, type, deep + "." + Array.get(arg, i).getClass().getName());
                }
            }
            return (T) values;
        }
        
        if (arg instanceof Map) {
            Iterator<?> keyItr = ((Map)arg).keySet().iterator();
            if (!keyItr.hasNext()) {
                return arg;
            }
            
            T newArg = null;
            try {
                newArg = (T) arg.getClass().newInstance();
            } catch (Exception e) {
                logger.error("加密插件 - 实例化对象失败", e);
            }
            while (keyItr.hasNext()) {
                Object key = keyItr.next();
                Object subArg = ((Map)arg).get(key);
                
                if (subArg != null) {
                    ((Map)newArg).put(key, processSet(subArg, encryptor, type, deep + "." + subArg.getClass().getName()));
                }
            }
            return newArg;
        }
        
        for (Class<?> clazz = arg.getClass(); clazz != BaseEntity.class && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (isBasicType(field.getType(), encryptor)) {
                    processField(field, arg, encryptor, type, deep + "." + field.getName());
                } else {
                    try {
                        processSet(PropertyUtils.getSimpleProperty(arg, field.getName()), encryptor, type, deep + "." + field.getType().getName());
                    } catch (NoSuchMethodException e) {
                        logger.warn("加密插件 - Field没有getter，跳过，Name:{}", field.getName());
                    } catch (Exception e) {
                        logger.error("加密插件 - 遍历参数异常", e);
                    }
                }
            }
        }
        
        return arg;
    }

    private static <T> void processField(Field field, T fieldObject, Encryptor encryptor, int type, String deep) {
        boolean isEncryptField = field.isAnnotationPresent(Encrypt.class);
        boolean isAdditionalEncryptField = encryptor.getEncryptContext(field) != null;
        
        if (isEncryptField || isAdditionalEncryptField) {
            try {
                Map<String, String> encryptContext = encryptor.getEncryptContext(field);
                if (encryptor.isDebug()) {
                    logger.debug("加密插件 - 检查到需加密字段：{}", deep);
                }
                Object val = PropertyUtils.getSimpleProperty(fieldObject, field.getName());
                if (val == null) {
                    return;
                }
                if (isBasicType(field.getType(), encryptor)) {
                    if (0 == type) {
                        try {
                            val = encryptor.encrypt(val, encryptContext);
                        } catch (Exception e) {
                            throw new RuntimeException("加密处理异常，处理前值为：" + val + "，encryptorContext为：" + encryptContext, e);
                        }
                        if (encryptor.isDebug()) {
                            logger.debug("加密插件 - 敏感数据加密后，Name：{}， Value：{}", deep, val);
                        }
                    } else {
                        if (encryptor.isDebug()) {
                            logger.debug("加密插件 - 敏感数据解密前，Name：{}，value：{}", deep, val);
                        }
                        try {
                            val = encryptor.decrypt(val, encryptContext);
                        } catch (Exception e) {
                            throw new RuntimeException("解密处理异常，处理前值为：" + val + ", encryptContext为：" + encryptContext, e);
                        }
                    }
                    PropertyUtils.setSimpleProperty(fieldObject, field.getName(), val);
                    return;
                }
            } catch (NoSuchMethodException e) {
                logger.warn("加密插件 - Field没有getter, 跳过, Name:{}", field.getName());
            } catch (Exception e) {
                logger.error("加密插件 - 数据加密异常，数据加密失败", e);
            }
        }
        
        if (encryptor.isDebug()) {
            logger.debug("加密插件 - 检查到非加密字段：{}", deep);
        }
    }
    
    // TODO
    public static void encrypt(Method method, Object[] args, FilterEncryptor encryptor) {
    }

}
