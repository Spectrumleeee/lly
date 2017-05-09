package xmu.lgp.lly.common.exception;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class SpringErrorMessageResources extends AbstractErrorMessageResource {

    private static Logger logger = LoggerFactory.getLogger(SpringErrorMessageResources.class);
    
    private Properties allProps = new Properties();
    private Resource[] messageProperties = null;
    
    @Override
    protected String doGetMessage(String errorCode) {
        return allProps.getProperty(errorCode);
    }

    public Resource[] getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(Resource[] messageProperties) {
        this.messageProperties = messageProperties;
        
        allProps.clear();
        if (messageProperties != null) {
            for(Resource propResource : messageProperties) {
                loadProperties(propResource);
            }
        }
    }

    private void loadProperties(Resource propResource) {
        try {
            String propFileName = propResource.getURL().toString();
            logger.info("开始加载错误码消息配置文件：" + propFileName);
            Properties props = new Properties();
            try {
                props.load(propResource.getInputStream());
                for(Object errorCode : props.keySet()) {
                    if (allProps.containsKey(errorCode)) {
                        logger.warn("文件{}中的错误码[{}]消息配置重复，已有消息配置[{}],", new Object[]{propFileName, errorCode, allProps.get(errorCode)});
                    } else {
                        allProps.put(errorCode, props.get(errorCode));
                    }
                }
            } catch (Exception e) {
                logger.error("加载错误码消息配置文件失败：" + propResource.getFilename(), e);
            }
        
            logger.info("错误码消息配置文件：" + propFileName + " 加载完成");
        } catch (IOException e) {}
    }
}
