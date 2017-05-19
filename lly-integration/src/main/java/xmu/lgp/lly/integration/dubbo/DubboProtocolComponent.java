package xmu.lgp.lly.integration.dubbo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;

import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;

import xmu.lgp.lly.common.util.StringUtil;
import xmu.lgp.lly.integration.ServiceProtocolComponent;
import xmu.lgp.lly.integration.config.OperationInfo;
import xmu.lgp.lly.integration.config.ServiceInfo;
import xmu.lgp.lly.integration.dubbo.container.LlyLogbackContainer;
import xmu.lgp.lly.integration.exception.ServiceCreateException;
import xmu.lgp.lly.integration.proxy.ServiceProxyFactory;

public class DubboProtocolComponent implements ServiceProtocolComponent {

    private static Logger logger;

    private static final String PROXY_FACTORY_BEAN_ID = "LLY_PROXY_FACTORY_BEAN";

    private static final String PROXY_FACTORY_METHOD = "createProxy";

    private static final Random random = new Random();

    public DubboProtocolComponent() {
        LlyLogbackContainer.getInstance().start();
        logger = LoggerFactory.getLogger(DubboProtocolComponent.class);
    }

    @Override
    public void registerConsumerService(BeanDefinitionRegistry registry, ServiceInfo serviceInfo) {
        logger.debug("register consumer service:" + serviceInfo.getServiceName() + " to dubbo");
        
        if (StringUtil.isEmpty(serviceInfo.getProxy())) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceBean.class);
            BeanDefinition beanDef = builder.getRawBeanDefinition();
            String beanId = serviceInfo.getServiceName();
            
            beanDef.setLazyInit(false);
            beanDef.setScope("singleton");
            beanDef.getPropertyValues().add("interfaceClass", serviceInfo.getServiceClass());
            
            beanDef.getPropertyValues().add("cluster", "failfast");
            beanDef.getPropertyValues().add("check", "false");
            if (serviceInfo.getTimeout() > 0) {
                beanDef.getPropertyValues().add("timeout", Integer.valueOf(serviceInfo.getTimeout() * 1000));
            }
            
            if (serviceInfo.getVersion() != null) {
                beanDef.getPropertyValues().add("version", serviceInfo.getVersion());
            }
            
            if (serviceInfo.getGroup() != null) {
                beanDef.getPropertyValues().add("group", serviceInfo.getGroup());
            }
            
            if (serviceInfo.getRegistry() != null) {
                if (serviceInfo.getRegistry().indexOf(",") > 0) {
                    parseMultiRef("registries", serviceInfo.getRegistry(), beanDef);
                } else {
                    beanDef.getPropertyValues().addPropertyValue("registry", new RuntimeBeanReference(serviceInfo.getRegistry()));
                }
            }
            
            if (serviceInfo.getUrl() != null) {
                if (serviceInfo.getUrl().indexOf(",") > 0) {
                    beanDef.getPropertyValues().add("urls", UrlUtils.parseURLs(serviceInfo.getUrl(), null));
                } else {
                    beanDef.getPropertyValues().add("url", serviceInfo.getUrl());
                }
            }
            
            List<MethodConfig> methodConfigList = generateMethodConfig(serviceInfo);
            beanDef.getPropertyValues().add("methods", methodConfigList);
            
            if (serviceInfo.getActives() > 0) {
                beanDef.getPropertyValues().add("actives", Integer.valueOf(serviceInfo.getActives()));
            }
            
            registry.registerBeanDefinition(beanId, beanDef);
        } else {
            try {
                if (!registry.containsBeanDefinition(PROXY_FACTORY_BEAN_ID)) {
                    GenericBeanDefinition proxyBeanFactoryDef = new GenericBeanDefinition();
                    proxyBeanFactoryDef.setBeanClass(ServiceProxyFactory.class);
                    proxyBeanFactoryDef.setLazyInit(false);
                    proxyBeanFactoryDef.setScope("singleton");
                    registry.registerBeanDefinition(PROXY_FACTORY_BEAN_ID, proxyBeanFactoryDef);
                }
                
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(serviceInfo.getServiceClass());
                
                AbstractBeanDefinition beanDef = builder.getRawBeanDefinition();
                String beanId = serviceInfo.getServiceName();
                beanDef.setLazyInit(false);
                beanDef.setScope("singleton");
                beanDef.setFactoryBeanName(PROXY_FACTORY_BEAN_ID);
                beanDef.setFactoryBeanName(PROXY_FACTORY_METHOD);
                
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                constructorArgumentValues.addGenericArgumentValue(serviceInfo.getServiceClass());
                constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference(serviceInfo.getProxy()));
                beanDef.setConstructorArgumentValues(constructorArgumentValues);
                
                registry.registerBeanDefinition(beanId, beanDef);
            } catch (Exception e) {
                throw new ServiceCreateException("找不到代理对象，BeanId=" + serviceInfo.getProxy(), e);
            }
        }
    }

    @Override
    public void registerProviderService(BeanDefinitionRegistry registry, ServiceInfo serviceInfo, boolean tokenFlag) {
        logger.debug("register provider service:" + serviceInfo.getServiceName() + " to dubbo");
        
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ServiceBean.class);
        BeanDefinition beanDef = builder.getRawBeanDefinition();
        String beanId = serviceInfo.getServiceName();
        
        beanDef.setLazyInit(false);
        beanDef.setScope("singleton");
        beanDef.getPropertyValues().add("interfaceClass", serviceInfo.getServiceClass());
        beanDef.getPropertyValues().add("cluster", "failfast");
        
        if (serviceInfo.getVersion() != null) {
            beanDef.getPropertyValues().add("version", serviceInfo.getVersion());
        }
        
        if (serviceInfo.getGroup() != null) {
            beanDef.getPropertyValues().add("group", serviceInfo.getGroup());
        }
        
        if (serviceInfo.getRegistry() != null) {
            if (serviceInfo.getRegistry().indexOf(",") > 0) {
                parseMultiRef("registries", serviceInfo.getRegistry(), beanDef);
            } else {
                beanDef.getPropertyValues().addPropertyValue("registry", new RuntimeBeanReference(serviceInfo.getRegistry()));
            }
        }
        
        if (serviceInfo.isRegister() != null) {
            beanDef.getPropertyValues().add("register", serviceInfo.isRegister());
        }
        
        beanDef.getPropertyValues().add("ref", new RuntimeBeanReference(serviceInfo.getImplementor()));
        if (tokenFlag) {
            beanDef.getPropertyValues().add("token", newToken());
        }
        
        List<MethodConfig> methodConfigList = generateMethodConfig(serviceInfo);
        beanDef.getPropertyValues().add("methods", methodConfigList);
        if (serviceInfo.isValidation()) {
            beanDef.getPropertyValues().add("validation", "true");
        }
        
        if (serviceInfo.getActives() > 0) {
            beanDef.getPropertyValues().add("actives", Integer.valueOf(serviceInfo.getActives()));
        }
        
        if (serviceInfo.getExecutes() > 0) {
            beanDef.getPropertyValues().add("executes", Integer.valueOf(serviceInfo.getExecutes()));
        }
        
        registry.registerBeanDefinition(beanId, beanDef);
    }
    
    private static void parseMultiRef(String property, String value, BeanDefinition beanDefinition) {
        String[] values = value.split("\\s*[,]+\\s*");
        ManagedList<BeanReference> list = null;
        
        for (int i=0; i<values.length; i++) {
            String v = values[i];
            if (v != null && v.length() > 0) {
                if (list == null) {
                    list = new ManagedList<BeanReference>();
                }
                list.add(new RuntimeBeanReference(v));
            }
        }
        beanDefinition.getPropertyValues().addPropertyValue(property, list);
    }

    private List<MethodConfig> generateMethodConfig(ServiceInfo serviceInfo) {
        Iterator<OperationInfo> operIt = serviceInfo.getOperationIterator();
        ArrayList<MethodConfig> methodConfigList = new ArrayList<>();
        while (operIt.hasNext()) {
            OperationInfo operInfo = (OperationInfo)operIt.next();
            MethodConfig methodConfig = new MethodConfig();
            
            methodConfig.setName(operInfo.getOperationName());
            if (operInfo.isAsync()) {
                methodConfig.setAsync(Boolean.TRUE);
            }
            
            if (operInfo.isOneWay()) {
                methodConfig.setReturn(Boolean.FALSE);
            } else {
                methodConfig.setReturn(Boolean.TRUE);
            }
            
            if (operInfo.getTimeout() > 0) {
                methodConfig.setTimeout(Integer.valueOf(operInfo.getTimeout()));
            }
            
            methodConfigList.add(methodConfig);
        }
        
        return methodConfigList;
    }
    
    private String newToken() {
        return String.valueOf(Math.abs(random.nextInt()));
    }
    
}
