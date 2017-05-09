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
            logger.info("��ʼ���ش�������Ϣ�����ļ���" + propFileName);
            Properties props = new Properties();
            try {
                props.load(propResource.getInputStream());
                for(Object errorCode : props.keySet()) {
                    if (allProps.containsKey(errorCode)) {
                        logger.warn("�ļ�{}�еĴ�����[{}]��Ϣ�����ظ���������Ϣ����[{}],", new Object[]{propFileName, errorCode, allProps.get(errorCode)});
                    } else {
                        allProps.put(errorCode, props.get(errorCode));
                    }
                }
            } catch (Exception e) {
                logger.error("���ش�������Ϣ�����ļ�ʧ�ܣ�" + propResource.getFilename(), e);
            }
        
            logger.info("��������Ϣ�����ļ���" + propFileName + " �������");
        } catch (IOException e) {}
    }
}
