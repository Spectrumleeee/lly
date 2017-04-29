package xmu.lgp.lly.integration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import xmu.lgp.lly.common.util.StringUtil;
import xmu.lgp.lly.common.util.XmlUtil;
import xmu.lgp.lly.integration.exception.ServiceParseException;

public class XmlServicesConfigurator extends AbstractServiceConfig implements InitializingBean {

    public XmlServicesConfigurator() {
    }

    protected void loadConfigFile(Resource configResource) throws Exception {
        File file; 
        
        try {
            file = configResource.getFile();
        } catch (Exception e) {
            logger.warn("file path '{}' is not a single file.", configResource.getURL());
            file = null;
        }
        
        if (file != null && file.isDirectory()) {
            if (autoDiscover) {
                logger.info(configResource.getURL() + " is directory, auto search config file in directory!");
                autoLoadConfig(file);
            } else {
                logger.info(configResource.getURL() + " is directory, skip it!");
            }
        } else {
            logger.info("loading config " + configResource.getDescription());
            loadConfig(configResource.getInputStream());
        }
    }
    
    protected void autoLoadConfig(File configDir) throws Exception {
        if(!configDir.isDirectory()) {
            return;
        }
        File[] childFiles = configDir.listFiles();
        String suffix = serviceFileSuffix != null ? serviceFileSuffix : "services.xml";
        for (int i=0; i < childFiles.length; i++) {
            File childFile = childFiles[i];
            if(childFile.isDirectory()) {
                autoLoadConfig(childFile);
                continue;
            }
            if(!childFile.getName().endsWith(suffix)) {
                continue;
            }
            logger.info("auto loading config " + childFile.getAbsolutePath());
            try {
                loadConfig(new FileInputStream(childFile));
            } catch(Exception e) {
                logger.error("failed to load config file " + childFile.getPath(), e);
            }
        }
    }
    
    protected void loadConfig(InputStream configStream) throws Exception {
        Document configDoc = XmlUtil.doc(configStream);
        Element servicesElem = configDoc.getDocumentElement();
        if(!"services".equals(servicesElem.getLocalName())) {
            throw new ServiceParseException("unrecognized element " + servicesElem.getLocalName());
        }
        boolean usePkgNamespace = false;
        String defaultNamespace = servicesElem.getAttribute("namespace");
        String usePkgNamespaceAttr = servicesElem.getAttribute("usepkgnamespace");
        if(usePkgNamespaceAttr != null && "true".equalsIgnoreCase(usePkgNamespaceAttr)) {
            usePkgNamespace = true;
        }
        NodeList elementList = servicesElem.getElementsByTagName("service");
        if (elementList != null) {
            for(int i=0; i<elementList.getLength(); i++) {
                try {
                    parseServiceInfo(usePkgNamespace, defaultNamespace, (Element)elementList.item(i));
                } catch(Exception e) {
                    logger.error(((Element)elementList.item(i)).getAttribute("name"));
                }
            }
        }
        configStream.close();
    }
    
    private void parseServiceInfo(boolean usePkgNamespace, String defaultNamespace, Element serviceElem) throws ServiceParseException {
        String serviceName = serviceElem.getAttribute("name");
        String serviceInterfaceName = serviceElem.getAttribute("interface");
        String serviceImplementor = serviceElem.getAttribute("implementor");
        String version = serviceElem.getAttribute("version");
        String proxy = serviceElem.getAttribute("proxy");
        String group = serviceElem.getAttribute("group");
        String registry = serviceElem.getAttribute("registry");
        String url = serviceElem.getAttribute("url");
        String sensitive = serviceElem.getAttribute("sensitive");
        String register = serviceElem.getAttribute("register");
       
        try {
            Class serviceClass = getClass().getClassLoader().loadClass(serviceInterfaceName);
            ServiceInfo serviceInfo;
            if (usePkgNamespace) {
                serviceInfo = new ServiceInfo(serviceClass, new QName(null, serviceName), serviceImplementor);
            } else {
                serviceInfo = new ServiceInfo(serviceClass, new QName(defaultNamespace, serviceName), serviceImplementor);
            }
            String timeout = serviceElem.getAttribute("timeout");
            if(!StringUtil.isEmpty(timeout)) {
                serviceInfo.setTimeout(Integer.parseInt(timeout));
            }
            String executes = serviceElem.getAttribute("executes");
            if(!StringUtil.isEmpty(executes)) {
                serviceInfo.setExecutes(Integer.parseInt(executes));
            }
            String actives = serviceElem.getAttribute("actives");
            if(!StringUtil.isEmpty(actives)) {
                serviceInfo.setActives(Integer.parseInt(actives));
            }
            String validation = serviceElem.getAttribute("validation");
            if(!StringUtil.isEmpty(validation)) {
                serviceInfo.setValidation(Boolean.valueOf(validation).booleanValue());
            }
            if(!StringUtil.isEmpty(version)) {
                serviceInfo.setVersion(version);
            }
            if(!StringUtil.isEmpty(proxy)) {
                serviceInfo.setProxy(proxy);
            }
            if(!StringUtil.isEmpty(group)) {
                serviceInfo.setGroup(group);
            }
            if(!StringUtil.isEmpty(registry)) {
                serviceInfo.setRegistry(registry);
            }
            if(!StringUtil.isEmpty(url)) {
                serviceInfo.setUrl(url);
            }
            if(!StringUtil.isEmpty(sensitive)) {
                serviceInfo.setSensitive(Boolean.valueOf(sensitive).booleanValue());
            }
            if(!StringUtil.isEmpty(register)) {
                serviceInfo.setRegister(Boolean.valueOf(register));
            }
            addService(serviceInfo);
            parseOperationInfos(serviceElem, serviceInfo);
            
        } catch (ClassNotFoundException e) {
            logger.warn("can't find class {} ", serviceInterfaceName);
            throw new ServiceParseException(e.getMessage(), e);
        }
    }
    
    private void parseOperationInfos(Element serviceElem, ServiceInfo serviceInfo) {
        NodeList operationList = serviceElem.getElementsByTagName("operation");
        if(operationList != null) {
            for(int i=0; i<operationList.getLength(); i++) {
                Element operationElem = (Element)operationList.item(i);
                String operName = operationElem.getAttribute("name");
                OperationInfo operInfo = serviceInfo.getOperationInfo(operName);
                if(operInfo == null) {
                    logger.error(serviceInfo.getServiceClass().getName() + " " +operName);
                    continue;
                }
                String timeout = operationElem.getAttribute("timeout");
                if(!StringUtil.isEmpty(timeout)) {
                    operInfo.setTimeout(Integer.parseInt(timeout));
                }
                String async = operationElem.getAttribute("async");
                if("true".equalsIgnoreCase(async) || "1".equals(async)) {
                    operInfo.setAsync(true);
                }
                String oneway = operationElem.getAttribute("oneway");
                if("true".equalsIgnoreCase(oneway) || "1".equals(oneway)) {
                    operInfo.setOneWay(true);
                }
            }
        }
    }
    
    public void loadConfigs() {
        if(configResources == null) {
            throw new ServiceParseException("Must set property configResources before call method loadConfig()!");
        }
        for(int i=0; i<configResources.length; i++) {
            try {
                loadConfigFile(configResources[i]);
            } catch(Exception e) {
                logger.error("failed to load config file " + configResources[i].getFilename(), e);
            }
        }
    }
    
    public void setConfigResources(Resource[] configResources) {
        this.configResources = configResources;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
    }

    private static final Logger logger = LoggerFactory.getLogger(XmlServicesConfigurator.class);
    private static final String SERVICES_CONFIG_FILE_SUFFIX = "services.xml";
    private static final String SERVICES_ELEM_NAME = "services";
    private static final String SERVICE_ELEM_NAME = "service";
    private static final String OPERATION_ELEM_NAME = "operation";
    private Resource[] configResources;
    private String serviceFileSuffix;
    private boolean autoDiscover = true;
}
