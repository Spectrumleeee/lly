package xmu.lgp.lly.common.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContext {
    
    public static final String DEFAULT_REQUEST_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private static final ThreadLocal<ServiceContext> contexts = new InheritableThreadLocal<ServiceContext>(){
        
        public final ServiceContext childValue(ServiceContext parent) {
            ServiceContext c = new ServiceContext();
            if (parent != null) {
                c.initBy(parent);
            }
            return c;
        }
        
        protected ServiceContext initialValue() {
            return new ServiceContext();
        }
    };
    
    private static final String LLY_PREFIX = "k.";
    
    private Map<String, String> headers = new ConcurrentHashMap<>();
    
    public static ServiceContext getContext() {
        return (ServiceContext)contexts.get();
    }
    
    public static ServiceContext getContext(boolean initFlowNo) {
        ServiceContext c = (ServiceContext)contexts.get();
        if(!initFlowNo) {
            return c;
        }
        
        c.setRequestNo(getRandomFlowNo());
        return c;
    }

    public static ServiceContext getContext(String prefix) {
        ServiceContext c = (ServiceContext)contexts.get();
        if (prefix == null) {
            return c;
        }
        
        c.setRequestNo(prefix + getRandomFlowNo());
        return c;
    }
    
    public static String getRandomFlowNo() {
        int index = (int)(System.currentTimeMillis() % 13L);
        return UUID.randomUUID().toString().replaceAll("-", "").substring(index, index + 16);
    }
    
    public static void removeContext() {
        contexts.remove();
    }
    
    private void initBy(ServiceContext parent) {
        if(parent == null || parent == this) {
            return ;
        }
        headers.clear();
        headers.putAll(headers);
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void clearHeader() {
        headers.clear();
    }
    
    public Map<String, String> getCloneHeaders() {
        Map<String, String> m = new HashMap<>();
        Iterator<String> keys = headers.keySet().iterator();
        
        while(keys.hasNext()) {
            String key = (String)keys.next();
            m.put(LLY_PREFIX + key, headers.get(key));
        }
        
        return m;
    }
    
    public void removeHeader(String key) {
        if(headers.containsKey(key)) {
            headers.remove(key);
        }
    }
    
    public String getHeader(String key) {
        return (String)headers.get(key);
    }
    
    public void addHeader(String key, String value) {
        if(value == null) {
            headers.remove(key);
            return;
        }
        headers.put(key, value);
    }
    
    public void addHeaders(Map<String, String> headers) {
        for(Map.Entry<String, String> e : headers.entrySet()) {
            String key = (String)e.getKey();
            if(key != null && key.startsWith(LLY_PREFIX)) {
                addHeader(key.substring(LLY_PREFIX.length()), e.getValue());
            }
        }
    }
    
    public String getAppVersion() {
        return getHeader(ContextHeaderKey.APP_VERSION);
    }
    
    public void setAppVersion(String str) {
        addHeader(ContextHeaderKey.APP_VERSION, str);
    }
    
    public String getUserId() {
        return getHeader(ContextHeaderKey.USER_ID);
    }
    
    public void setUserId(String str) {
        addHeader(ContextHeaderKey.USER_ID, str);
    }
    
    public String getUserIP() {
        return getHeader(ContextHeaderKey.USER_IP);
    }
    
    public void setUserIP(String str) {
        addHeader(ContextHeaderKey.USER_IP, str);
    }
    
    public String getUserMac() {
        return getHeader(ContextHeaderKey.USER_MAC);
    }
    
    public void setUserMac(String str) {
        addHeader(ContextHeaderKey.USER_MAC, str);
    }
    
    public String getUserImei() {
        return getHeader(ContextHeaderKey.USER_IMEI);
    }
    
    public void setUserImei(String str) {
        addHeader(ContextHeaderKey.USER_IMEI, str);
    }
    
    public String getRequestNo() {
        return getHeader(ContextHeaderKey.REQUEST_FLOWNO);
    }
    
    public void setRequestNo(String str) {
        addHeader(ContextHeaderKey.REQUEST_FLOWNO, str);
    }
    
    public String getConsumerIp() {
        return getHeader(ContextHeaderKey.CONSUMER_IP);
    }
    
    public void setConsumerIp(String str) {
        addHeader(ContextHeaderKey.CONSUMER_IP, str);
    }
    
    public String getChannelCode() {
        return getHeader(ContextHeaderKey.CHANNEL_CODE);
    }
    
    public void setChannelCode(String str) {
        addHeader(ContextHeaderKey.CHANNEL_CODE, str);
    }
    
    public String getSessionId() {
        return getHeader(ContextHeaderKey.SESSION_ID);
    }
    
    public void setSessionId(String str) {
        addHeader(ContextHeaderKey.SESSION_ID, str);
    }
    
    public String getSessionKey() {
        return getHeader(ContextHeaderKey.SESSION_KEY);
    }
    
    public void setSessionKey(String str) {
        addHeader(ContextHeaderKey.SESSION_KEY, str);
    }
    
    public String getLocalIp() {
        return getHeader(ContextHeaderKey.LOCAL_IP);
    }
    
    public void setLocalIp(String str) {
        addHeader(ContextHeaderKey.LOCAL_IP, str);
    }
    
    public String getInvocationInterface() {
        return getHeader(ContextHeaderKey.INVOCATION_INTERFACE);
    }
    
    public void setInvocationInterface(String str) {
        addHeader(ContextHeaderKey.INVOCATION_INTERFACE, str);
    }
    
    public String getInvocationMethod() {
        return getHeader(ContextHeaderKey.INVOCATION_METHOD);
    }
    
    public void setInvocationMethod(String str) {
        addHeader(ContextHeaderKey.INVOCATION_METHOD, str);
    }
    
    public String getRequestDateTime() {
        return getHeader(ContextHeaderKey.REQUEST_DATE_TIME);
    }
    
    public void setRequestDateTime(String str) {
        addHeader(ContextHeaderKey.REQUEST_DATE_TIME, str);
    }
    
    public String getOrganizationNumber() {
        return getHeader(ContextHeaderKey.ORGANIZATION_NUMBER);
    }
    
    public void setOrganizationNumber(String str) {
        addHeader(ContextHeaderKey.ORGANIZATION_NUMBER, str);
    }
    
    public String getBusType() {
        return getHeader(ContextHeaderKey.BUS_TYPE);
    }
    
    public void setBusType(String str) {
        addHeader(ContextHeaderKey.BUS_TYPE, str);
    }

}
