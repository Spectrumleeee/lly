package xmu.lgp.lly.integration.dubbo.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LlyReportStatusContainer {

    
    
    public synchronized void stop() {
        instance.hasStart = false;
        logger.warn("Container[");
    }
    
    private static LlyReportStatusContainer instance = new LlyReportStatusContainer();
    private static final Logger logger = LoggerFactory.getLogger(LlyReportStatusContainer.class);
    private volatile boolean hasStart;
    private String reportFilePath;
}
