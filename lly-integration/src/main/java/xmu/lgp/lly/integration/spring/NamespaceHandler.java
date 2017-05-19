package xmu.lgp.lly.integration.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 注册自定义Spring标签lly:registry、lly:serviceprovider、lly:serviceconsumer的解析器
 * 
 * @author liguangpu
 * @date 2017-5-19 上午9:45:45
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

    public NamespaceHandler() {}

    @Override
    public void init() {
        registerBeanDefinitionParser("registry", new RegistryBeanDefParser());
        registerBeanDefinitionParser("serviceprovider", new ServiceProviderBeanDefParser());
        registerBeanDefinitionParser("serviceconsumer", new ServiceConsumerBeanDefParser());
    }

}
