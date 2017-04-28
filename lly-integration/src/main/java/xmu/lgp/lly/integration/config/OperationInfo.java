package xmu.lgp.lly.integration.config;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

public class OperationInfo {

    public OperationInfo(ServiceInfo serviceInfo, Method method) {
        if (method == null) {
            throw new IllegalArgumentException("method could't be null!");
        } else {
            this.serviceInfo = serviceInfo;
            this.operationMethod = method;
            this.operationName = method.getName();
            return;
        }
    }

    public QName getOperationQName() {
        return new QName(serviceInfo.getNamespaceURI(), operationName);
    }

    private ServiceInfo serviceInfo;
    private String operationName;
    private Method operationMethod;
    private int timeout;
    private boolean async;
    private boolean oneWay;

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Method getOperationMethod() {
        return operationMethod;
    }

    public void setOperationMethod(Method operationMethod) {
        this.operationMethod = operationMethod;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }
}
