package xmu.lgp.lly.common.security;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import xmu.lgp.lly.common.annotation.Encrypt;
import xmu.lgp.lly.common.annotation.NameValue;

public abstract class FilterEncryptor implements Encryptor {

    protected Map<String, String> additionEncryptContext = new HashMap<>();
    
    protected Map<String, Map<String, String>> contextMap = new ConcurrentHashMap<>();
    
    protected Set<Class<?>> basicTypes = new HashSet<>();
    
    public Set<Class<?>> getBasicTypes() {
        return basicTypes.size() == 0 ? null : basicTypes;
    }
    
    public void setBasicTypes(List<String> classList) {
        basicTypes.clear();
        for (String className : classList) {
            try {
                Class<?> clazz = Class.forName(className);
                basicTypes.add(clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Map<String, String> getAdditionalEncryptContext() {
        return additionEncryptContext;
    }
    
    public void setAdditionalEncryptContext(Map<String, String> additionEncryptContext) {
        this.additionEncryptContext = additionEncryptContext;
        contextMap.clear();
    }
    
    @Override
    public Map<String, String> getEncryptContext(Field field) {
        if (additionEncryptContext == null || field == null) {
            return null;
        }
        
        String contextKey = field.getDeclaringClass().getName() + "#" + field.getName();
        
        return getEncryptContext((Encrypt)field.getAnnotation(Encrypt.class), contextKey);
    }
    
    @Override
    public Map<String, String> getEncryptContext(Encrypt encryptAnn, String contextKey) {
        Map<String, String> encryptContext = null;
        if (encryptAnn != null) {
            encryptContext = new HashMap<>();
            for (NameValue nv : encryptAnn.context()) {
                if (nv.name() != null) {
                    encryptContext.put(nv.name(), nv.value());
                }
            }
        } else {
            encryptContext = (Map<String, String>)contextMap.get(contextKey);
            if (encryptContext != null) {
                return encryptContext;
            }
            
            String ctx = (String)additionEncryptContext.get(contextKey);
            if (ctx != null) {
                ctx = ctx.trim().replace("{", "").replace("}", "").replace("\"", "").replace("'", "");
                String[] ctxArr = ctx.split(",");
                encryptContext = new HashMap<>();
                for (int i=0; i< ctxArr.length; i++) {
                    String[] kv = ctxArr[i].split(":");
                    if (kv.length == 2) {
                        encryptContext.put(kv[0].trim(), kv[1].trim());
                    } else {
                        throw new IllegalArgumentException("加密插件 - 额外加密字段加密上下文格式错误");
                    }
                }
                contextMap.put(contextKey, encryptContext);
                return encryptContext;
            }
            return null;
        }
        return encryptContext;
    }

    public abstract boolean shouldServerEncryptRequest();
    
    public abstract boolean shouldClientDecryptRequest();
    
    public abstract boolean shouldClientEncryptResponse();
    
    public abstract boolean shouldServerDecryptResponse();
    
}
