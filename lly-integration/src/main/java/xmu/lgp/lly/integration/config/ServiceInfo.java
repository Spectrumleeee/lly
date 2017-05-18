package xmu.lgp.lly.integration.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;

public class ServiceInfo {

    private QName serviceQName;
    private Class<?> serviceClass;
    private String implementor;
    private HashMap<String, OperationInfo> operations;
    private HashMap<Method, OperationInfo> methodMap;
    private int timeout;
    private boolean validation;
    private String version;
    private String proxy;
    private String group;
    private String registry;
    private String url;
    private int executes;
    private int actives;
    private boolean sensitive;
    private Boolean register;
    
    public ServiceInfo(Class<?> serviceClass, QName serviceQName, String implementor) {
        operations = new HashMap<String, OperationInfo>();
        methodMap = new HashMap<Method, OperationInfo>();
        validation = false;
        sensitive = false;
        if (serviceClass == null) {
            throw new IllegalArgumentException("Service class could't be null!");
        }
        if (serviceQName == null) {
            serviceQName = new QName(getNamespaceURI(serviceClass), getServiceName(serviceClass));
        }
        String localName = serviceQName.getLocalPart();
        String namespaceURI = serviceQName.getNamespaceURI();
        boolean changeFlag = false;
        if (localName == null || localName.isEmpty()) {
            localName = getServiceName(serviceClass);
            changeFlag = true;
        }
        if (namespaceURI == null || "".equals(namespaceURI)) {
            namespaceURI = getNamespaceURI(serviceClass);
            changeFlag = true;
        }
        if (changeFlag) {
            serviceQName = new QName(namespaceURI, localName);
        }
        if (implementor == null || implementor.isEmpty()) {
            this.implementor = localName + "Impl";
        } else {
            this.implementor = implementor;
        }
        this.serviceClass = serviceClass;
        this.serviceQName = serviceQName;

        generateOperationInfos(serviceClass);
    }

    private void generateOperationInfos(Class<?> clazz) {
        Method methods[] = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            int mod = methods[i].getModifiers();
            if (!Modifier.isPublic(mod) || Modifier.isStatic(mod) || methods[i].isSynthetic()) {
                continue;
            }
            WebMethod publishAnnotation = (WebMethod) methods[i].getAnnotation(javax.jws.WebMethod.class);
            if (publishAnnotation != null && publishAnnotation.exclude()) {
                continue;
            }
            OperationInfo operInfo = generateOperationInfo(methods[i]);
            if (!operations.containsKey(operInfo.getOperationName())) {
                operations.put(operInfo.getOperationName(), operInfo);
                methodMap.put(methods[i], operInfo);
            }
        }

        if (clazz.isInterface()) {
            Class<?> superIntfs[] = clazz.getInterfaces();
            for (int i = 0; i < superIntfs.length; i++) {
                generateOperationInfos(superIntfs[i]);
            }
        } else {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != java.lang.Object.class) {
                generateOperationInfos(superClass);
            }
        }
    }

    protected String getNamespaceURI(Class<?> clazz) {
        WebService webService = (WebService) clazz.getAnnotation(javax.jws.WebService.class);
        if (webService != null) {
            return webService.targetNamespace();
        }
        String className = clazz.getName();
        String parts[] = className.split("\\.");
        StringBuffer namespaceURI = new StringBuffer();
        namespaceURI.append("http://");
        for (int i = parts.length - 2; i >= 0; i--) {
            namespaceURI.append(parts[i]);
            if (i != 0) {
                namespaceURI.append(".");
            }
        }

        namespaceURI.append("/");
        return namespaceURI.toString();
    }

    protected String getServiceName(Class<?> clazz) {
        WebService webService = (WebService) clazz.getAnnotation(javax.jws.WebService.class);
        if (webService != null) {
            return webService.name();
        } else {
            String className = clazz.getSimpleName();
            return className.substring(0, 1).toLowerCase() + className.substring(1);
        }
    }

    public QName getServiceQName() {
        return serviceQName;
    }

    public void setServiceQName(QName serviceQName) {
        this.serviceQName = serviceQName;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getImplementor() {
        return implementor;
    }

    public void setImplementor(String implementor) {
        this.implementor = implementor;
    }

    public HashMap<String, OperationInfo> getOperations() {
        return operations;
    }

    public void setOperations(HashMap<String, OperationInfo> operations) {
        this.operations = operations;
    }

    public HashMap<Method, OperationInfo> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(HashMap<Method, OperationInfo> methodMap) {
        this.methodMap = methodMap;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getExecutes() {
        return executes;
    }

    public void setExecutes(int executes) {
        this.executes = executes;
    }

    public int getActives() {
        return actives;
    }

    public void setActives(int actives) {
        this.actives = actives;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getNamespaceURI() {
        return serviceQName.getNamespaceURI();
    }

    private OperationInfo generateOperationInfo(Method m) {
        return new OperationInfo(this, m);
    }

    public String getServiceName() {
        return serviceQName.getLocalPart();
    }
    
    public OperationInfo getOperationInfo(String operationName) {
        return (OperationInfo) operations.get(operationName);
    }

    public Iterator<OperationInfo> getOperationIterator() {
        return operations.values().iterator();
    }

    public boolean isRegister() {
        return register;
    }
}
