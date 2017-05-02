package xmu.lgp.lly.integration.dubbo;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import xmu.lgp.lly.integration.ServiceProtocolComponent;
import xmu.lgp.lly.integration.config.ServiceInfo;

public class DubboProtocolComponent implements ServiceProtocolComponent {

    @Override
    public void registerConsumerService(BeanDefinitionRegistry beanDefRegistry, ServiceInfo serviceInfo) {
        // TODO 
    }

    @Override
    public void registerProviderService(BeanDefinitionRegistry beanDefRegistry, ServiceInfo serviceInfo,
            boolean tokenFlag) {
        // TODO 
    }

}
