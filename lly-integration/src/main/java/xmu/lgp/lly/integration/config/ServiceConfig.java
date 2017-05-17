package xmu.lgp.lly.integration.config;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface ServiceConfig {

    public abstract Collection<?> getServiceInfos();

    public abstract ServiceInfo getServiceInfo(QName qName);

    public abstract ServiceInfo getServiceInfo(Class<?> clazz);
    
}
