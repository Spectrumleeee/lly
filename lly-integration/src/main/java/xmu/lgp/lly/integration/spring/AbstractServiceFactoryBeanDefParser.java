package xmu.lgp.lly.integration.spring;

import java.io.IOException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.*;

import xmu.lgp.lly.integration.config.ServiceConfig;
import xmu.lgp.lly.integration.config.XmlServicesConfigurator;

public abstract class AbstractServiceFactoryBeanDefParser implements BeanDefinitionParser {

    public AbstractServiceFactoryBeanDefParser() {
    }

    protected void parseChildElement(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        String elemName = element.getLocalName();
        if ("property".equals(elemName)) {
            parserContext.getDelegate().parsePropertyElement(element, beanDef);
        }
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        GenericBeanDefinition beanDef = new GenericBeanDefinition();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (childNode.getNodeType() == 1) {
                parseChildElement((Element) childNode, parserContext, beanDef);
            }
        }

        XmlServicesConfigurator xmlServiceConfig = new XmlServicesConfigurator();
        String protocol = "dubbo";
        boolean tokenFlag = false;
        NamedNodeMap attrs = element.getAttributes();
        Attr protocolAttr = (Attr)attrs.getNamedItem("protocol");
        if(protocolAttr != null) {
            protocol = protocolAttr.getValue();
        }
        Attr tokenAttr = (Attr)attrs.getNamedItem("tokenflag");
        if(tokenAttr != null) {
            tokenFlag = Boolean.parseBoolean(tokenAttr.getValue());
        }
        Attr configAttr = (Attr)attrs.getNamedItem("config");
        if(configAttr != null) {
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(parserContext.getReaderContext().getResourceLoader());
            try {
                Resource[] configResources = resourcePatternResolver.getResources(configAttr.getValue());
                xmlServiceConfig.setConfigResources(configResources);
                xmlServiceConfig.loadConfigs();
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to find config resource");
            }
        }
        beanDef.getPropertyValues().addPropertyValue("protocol", protocol);
        beanDef.getPropertyValues().addPropertyValue("serviceConfig", xmlServiceConfig);
        doParse(element, parserContext, beanDef);
        beanDef.setLazyInit(false);
        beanDef.setScope("singleton");
        String id = parserContext.getReaderContext().generateBeanName(beanDef);
        parserContext.getRegistry().registerBeanDefinition(id, beanDef);
        registerServiceBeans(parserContext, xmlServiceConfig, protocol, tokenFlag);
        return beanDef;
    }

    protected abstract void doParse(Element element, ParserContext parserContext, BeanDefinition beandefinition);

    protected abstract void registerServiceBeans(ParserContext parserContext, ServiceConfig serviceConfig, String s,
            boolean flag);
}
