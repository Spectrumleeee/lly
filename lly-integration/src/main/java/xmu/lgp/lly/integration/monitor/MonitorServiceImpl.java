package xmu.lgp.lly.integration.monitor;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;

import xmu.lgp.lly.common.monitor.MonitorService;
import xmu.lgp.lly.common.monitor.SystemStatus;

public class MonitorServiceImpl implements MonitorService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SystemStatus systemStatus;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        systemStatus = new SystemStatus();
        try {
            systemStatus.setIp(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            logger.warn("获取本机IP地址异常", e);
            systemStatus.setIp("unknown");
        }
        systemStatus.setProcessUser(System.getProperty("user.name"));
        ClassPathResource classPathResource = new ClassPathResource("lly.version");
        if(classPathResource.exists()) {
            Properties properties = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = classPathResource.getInputStream();
                properties.load(inputStream);
                systemStatus.setLlyVersion(properties.getProperty("lly.version"));
            } catch (Exception e) {
                logger.warn("读取lsf版本号异常", e);
                systemStatus.setIp("unknown");
            } finally {
              if (inputStream != null) {
                  inputStream.close();
              }
            }
        } else {
            systemStatus.setIp("unknown");
        }
        systemStatus.setJvmArgs(ManagementFactory.getRuntimeMXBean().getInputArguments());
    }

    @Override
    public SystemStatus getStatus() {
        return systemStatus;
    }

    @Override
    public String ping() {
        return "";
    }

}
