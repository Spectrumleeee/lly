package xmu.lgp.lly.common.exception;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultErrorMessageResource extends AbstractErrorMessageResource {
    
    private static Logger logger = LoggerFactory.getLogger(DefaultErrorMessageResource.class);
    
    private static final String DEFAULT_ERROR_MESSAGE_FILE = "/errorcodes/message.properties";
    
    private String resourceFileName;
    
    private Properties properties = new Properties();
    
    public DefaultErrorMessageResource() {
        this(DEFAULT_ERROR_MESSAGE_FILE);
    }
    
    public DefaultErrorMessageResource(String errorMessageFile) {
        resourceFileName = errorMessageFile;
        loadProperties();
    }
    
    private void loadProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader != null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        
        properties.clear();
        try {
            if (resourceFileName != null ) {
                if (resourceFileName.startsWith("/")) {
                    resourceFileName = resourceFileName.substring(1);
                }
                properties.load(classLoader.getResourceAsStream(resourceFileName));
            }
        } catch (Exception e) {
            logger.error("加载异常消息资源文件失败：" + resourceFileName, e);
        }
    }
    
    protected String doGetMessage(String errorCode) {
        return properties.getProperty(errorCode);
    }
    
}
