package xmu.lgp.lly.common.security;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xmu.lgp.lly.common.annotation.Encrypt;

public interface Encryptor {

    public <T> T encrypt(T paramT, Map<String, String> paramMap);
    
    public <T> T decrypt(T paramT, Map<String, String> paramMap);
    
    public Set<Class<?>> getBasicTypes();
    
    public void setBasicTypes(List<String> paramList);
    
    public Map<String, String> getEncryptContext(Field paramField);
    
    public boolean isDebug();
    
    public Map<String, String> getEncryptContext(Encrypt paramEncrypt, String paramString);
    
}
