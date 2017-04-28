package xmu.lgp.lly.integration.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.*;
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
        /**
         * TODO
         */

        return null;
    }

    protected abstract void doParse(Element element, ParserContext parserContext, BeanDefinition beandefinition);

    protected abstract void registerServiceBean(ParserContext parserContext, ServiceConfig serviceConfig, String s,
            boolean flag);
}
