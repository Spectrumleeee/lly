package xmu.lgp.lly.integration.spring;

import java.util.Iterator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import xmu.lgp.lly.integration.ServiceProtocolComponent;
import xmu.lgp.lly.integration.ServiceProtocolComponentFactory;
import xmu.lgp.lly.integration.config.ServiceConfig;
import xmu.lgp.lly.integration.config.ServiceInfo;

public class ServiceConsumerBeanDefParser extends AbstractServiceFactoryBeanDefParser {

    @SuppressWarnings("deprecation")
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        beanDef.setBeanClassName(ServiceConsumerFactoryBean.class.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void registerServiceBeans(ParserContext parserContext, ServiceConfig serviceConfig, String protocol, boolean tokenFlag) {
        ServiceProtocolComponent svcProtocolComp = ServiceProtocolComponentFactory.getServiceProtocolComponent(protocol);
        
        if (svcProtocolComp == null) {
            throw new IllegalArgumentException("未知的服务协议:" + protocol);
        }
        
        if (serviceConfig != null) {
            Iterator<ServiceInfo> serviceIt = (Iterator<ServiceInfo>) serviceConfig.getServiceInfos().iterator();
            while(serviceIt.hasNext()) {
                svcProtocolComp.registerConsumerService(parserContext.getRegistry(), (ServiceInfo)serviceIt.next());
            }
        }
    }

}
