package xmu.lgp.lly.integration.spring;

import java.util.Iterator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import xmu.lgp.lly.integration.ServiceProtocolComponent;
import xmu.lgp.lly.integration.ServiceProtocolComponentFactory;
import xmu.lgp.lly.integration.config.ServiceConfig;
import xmu.lgp.lly.integration.config.ServiceInfo;

public class ServiceProviderBeanDefParser extends AbstractServiceFactoryBeanDefParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        beanDef.setBeanClassName(ServiceConsumerFactoryBean.class.getName());
    }

    @Override
    protected void registerServiceBeans(ParserContext parserContext, ServiceConfig serviceConfig, String protocol, boolean tokenFlag) {
        ServiceProtocolComponent svcProtocolComp = ServiceProtocolComponentFactory.getServiceProtocolComponent(protocol);
        
        if (serviceConfig != null) {
            Iterator<ServiceInfo> serviceIt = serviceConfig.getServiceInfos().iterator();
            while(serviceIt.hasNext()) {
                svcProtocolComp.registerProviderService(parserContext.getRegistry(), (ServiceInfo)serviceIt.next(), tokenFlag);
            }
        }
    }

}
