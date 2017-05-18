package xmu.lgp.lly.integration.dubbo;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;

import com.alibaba.dubbo.common.utils.ConfigUtils;

public class PropertiesConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurer.class);
    
    private String file;
    
    private boolean ignoreNoResource = false;
    
    public void init() {
        logger.warn("PropertiesLoader init start.");
        
        Properties p = ConfigUtils.loadProperties(file);
        if (!ignoreNoResource && p.isEmpty()) {
            throw new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE2, new Object[] {"dubbo.properties文件不存在，或属性文件内容为空。", "文件路径：" + file});
        }
        
        ConfigUtils.setProperties(p);
        logger.warn("PropertiesLoader init OK.");
    }
    
    public boolean isIgnoredNoResource() {
        return ignoreNoResource;
    }
    
    public void setIgnoreNoResource(boolean ignoreNoResource) {
        this.ignoreNoResource = ignoreNoResource;
    }
    
    public String getFile() {
        return file;
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
    
}
