package xmu.lgp.lly.integration.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.RegistryConfig;

public class RegistryBeanDefParser implements BeanDefinitionParser {

    private static Logger logger = LoggerFactory.getLogger(RegistryBeanDefParser.class);
    
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class.getName());
        BeanDefinition beanDef = builder.getRawBeanDefinition();
        
        NodeList nodes = element.getChildNodes();
        for ( int i=0; i< nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (childNode.getNodeType() == 1) {
                parseChildElement((Element)childNode, parserContext, beanDef);
            }
        }
        
        String beanId = getNodeAttrValue(element, "id", false);
        String address = getNodeAttrValue(element, "address", false);
        
        logger.info("Registering Registry id:{}, addres:{}", beanId, address);
        
        beanDef.setLazyInit(false);
        beanDef.setScope("singleton");
        beanDef.getPropertyValues().add("address", address);
        
        parserContext.getRegistry().registerBeanDefinition(beanId, beanDef);
        
        logger.info("Registering Registry Done");
        return beanDef;
    }

    protected void parseChildElement(Element element, ParserContext parserContext, BeanDefinition beanDef) {
        String elemName = element.getLocalName();
        if (elemName.equals("property")) {
            parserContext.getDelegate().parsePropertyElement(element, beanDef);
        }
    }
    
    private String getNodeAttrValue(Node tagNode, String attrName, boolean nullable) {
        String val = null;
        Node node = tagNode.getAttributes().getNamedItem(attrName);
        
        if (node != null) {
            val = node.getNodeValue();
        }
        
        if(!nullable) {
            Assert.isTrue(!StringUtils.isEmpty(val), "数据拆分 - 数据源属性" + attrName + "不能为空");
        }
        return val;
    }
    
}
