package xmu.lgp.lly.integration.dubbo.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.container.Container;

public class LlyLogbackContainer implements Container {

    public static final String DEFAULT_ADAPTER = "slf4j";
    
    private static LlyLogbackContainer instance = new LlyLogbackContainer();
    
    private volatile boolean hasStart = false;
    
    private volatile Logger logger = null;
    
    public static LlyLogbackContainer getInstance() {
        return instance;
    }
    
    @Override
    public synchronized void start() {
        if(instance.hasStart) {
            return;
        }
        
        instance.hasStart = true;
        System.setProperty("dubbo.application.logger", DEFAULT_ADAPTER);
        
        logger = LoggerFactory.getLogger(LlyLogbackContainer.class);
        logger.warn("Contaner[" + getClass().getSimpleName() + "] started.");
    }

    @Override
    public synchronized void stop() {
        if(!instance.hasStart) {
            return;
        }
        
        instance.hasStart = false;
        logger.warn("Container[" + getClass().getSimpleName() + "] stopped.");
    }
    
    
    
}
