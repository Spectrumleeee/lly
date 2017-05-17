package xmu.lgp.lly.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.integration.dubbo.filter.ExceptionFilter;
import xmu.lgp.lly.integration.dubbo.filter.SetContextFilter;

@SafelyStopOrder(order=-10)
public class RequestSafelyStoper implements SafeLifecycle {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestSafelyStoper.class);

    @Override
    public void safelyStop() {
        
        logger.info("开始执行请求计数关闭过程...");
        logger.info("还剩下{}个请求没有处理完成, {}个请求结果未返回, 开始等待处理完成", Integer.valueOf(ExceptionFilter.getProcessingRequestCounter()), Integer.valueOf(SetContextFilter.getRequestingCounter()));
        
        while(ExceptionFilter.getProcessingRequestCounter() > 0 && SetContextFilter.getRequestingCounter() > 0) {
            if (Thread.currentThread().isInterrupted()) {
                logger.warn("等待请求处理完成过程超时!还有" + ExceptionFilter.getProcessingRequestCounter() + "个请求未处理完成，" + SetContextFilter.getRequestingCounter() + "请求结果未返回");
                break;
            }
            Thread.yield();
        }
        
        if (!Thread.currentThread().isInterrupted()) {
            logger.info("所有请求处理完成");
        }
        
    }
}
