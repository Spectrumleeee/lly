package xmu.lgp.lly.integration.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {

    public NamespaceHandler() {

    }

    @Override
    public void init() {
        registerBeanDefinitionParser("registry", new RegistryBeanDefParser());
        registerBeanDefinitionParser("serviceprovider", new ServiceProviderBeanDefParser());
        registerBeanDefinitionParser("serviceconsumer", new ServiceConsumerBeanDefParser());
    }

}
