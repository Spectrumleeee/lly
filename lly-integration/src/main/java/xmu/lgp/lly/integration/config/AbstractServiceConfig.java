package xmu.lgp.lly.integration.config;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.namespace.QName;

import xmu.lgp.lly.common.util.StringUtil;
import xmu.lgp.lly.integration.exception.ServiceCreateException;

public abstract class AbstractServiceConfig implements ServiceConfig {

    public AbstractServiceConfig() {
        name2Services = new HashMap<String, ServiceInfo>();
        clss2Services = new HashMap<Class<?>, ServiceInfo>();
    }

    public ServiceInfo getServiceInfo(QName serviceName) {
        return (ServiceInfo) name2Services.get(serviceName);
    }

    public ServiceInfo getServiceInfo(Class<?> serviceClass) {
        return (ServiceInfo) clss2Services.get(serviceClass);
    }

    public Collection<?> getServiceInfos() {
        return name2Services.values();
    }

    public void addService(ServiceInfo serviceInfo) {
        if (name2Services.containsKey(serviceInfo.getServiceQName())) {
            throw new ServiceCreateException("service " + serviceInfo.getServiceName() + " has existed!");
        }
        if (clss2Services.containsKey(serviceInfo.getServiceClass())
                && StringUtil.isEmpty(serviceInfo.getServiceName())) {
            throw new ServiceCreateException("service class " + serviceInfo.getServiceClass().toString()
                    + " has existed!");
        }
        name2Services.put(serviceInfo.getServiceName(), serviceInfo);
        clss2Services.put(serviceInfo.getServiceClass(), serviceInfo);
        if (serviceInfo.isSensitive()) {
            synchronized (sensitiveServices) {
                sensitiveServices.put(serviceInfo.getServiceClass(), Boolean.TRUE);
            }
        }
    }

    public void addService(Class<?> serviceClass) {
        addService(new ServiceInfo(serviceClass, null, null));
    }

    private HashMap<String, ServiceInfo> name2Services;
    private HashMap<Class<?>, ServiceInfo> clss2Services;
    protected static HashMap<Class<?>, Boolean> sensitiveServices = new HashMap<Class<?>, Boolean>();
}
