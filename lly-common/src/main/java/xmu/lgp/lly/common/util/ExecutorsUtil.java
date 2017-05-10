package xmu.lgp.lly.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorsUtil {
    
    public void safeShutdownExecutors(ExecutorService executors, int timeout) {
        if (!executors.isShutdown()) {
            executors.shutdown();
        }
        try {
            executors.awaitTermination(timeout, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
