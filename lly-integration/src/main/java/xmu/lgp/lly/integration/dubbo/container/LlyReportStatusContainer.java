package xmu.lgp.lly.integration.dubbo.container;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.container.Container;

public class LlyReportStatusContainer implements Container {

    private static LlyReportStatusContainer instance = new LlyReportStatusContainer();
    
    private static final Logger logger = LoggerFactory.getLogger(LlyReportStatusContainer.class);
    
    private volatile boolean hasStart = false;
    
    private String reportFilePath = "";
    
    public static LlyReportStatusContainer getInstance() {
        return instance;
    }
    
    public void setReportFilePath(String path) {
        this.reportFilePath = path;
    }
    
    @Override
    public synchronized void start() {
        if (instance.hasStart) {
            logger.info("This container has started.");
            return;
        }
        
        instance.hasStart = true;
        if (reportFilePath == null || reportFilePath.isEmpty()) {
            logger.info("Report file path is empty.");
            return;
        }
        
        File f = new File(reportFilePath);
        try {
            f.createNewFile();
        } catch(IOException e) {
            logger.warn("Create report file failed: " + f.getAbsolutePath(), e);
        }
        
        logger.warn("Conainer[" + getClass().getSimpleName() + "] started.");
    }
    
    @Override
    public synchronized void stop() {
        instance.hasStart = false;
        logger.warn("Container[" + getClass().getSimpleName() + "] stopped.");
    }
}
