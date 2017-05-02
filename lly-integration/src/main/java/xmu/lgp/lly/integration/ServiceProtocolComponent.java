package xmu.lgp.lly.integration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import xmu.lgp.lly.integration.config.ServiceInfo;

public interface ServiceProtocolComponent {
    
    public abstract void registerConsumerService(BeanDefinitionRegistry beanDefRegistry, ServiceInfo serviceInfo);
    
    public abstract void registerProviderService(BeanDefinitionRegistry beanDefRegistry, ServiceInfo serviceInfo, boolean tokenFlag);
    
}
