package xmu.lgp.lly.common.context;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceContextTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Map<String, String> cloneHeaders;
    
    private boolean isGeneratenewFlowEachRun;
    
    public ServiceContextTask() {
        this(false);
    }
    
    public ServiceContextTask(boolean isGenerateNewFlowEachRun) {
        cloneHeaders = ServiceContext.getContext().getCloneHeaders();
        setGenerateNewFlowEachRun(isGenerateNewFlowEachRun);
    }
    
    @Override
    public void run() {
        try {
            preInit();
            runTask();
        } catch (Exception e) {
            logger.error("ִ��ServiceContextTask�����쳣", e);
        } finally {
            ContextSlf4jUtil.rmvLogKeyFromMDC();
            ServiceContext.removeContext();
        }
    }
    
    private final void preInit() {
        ServiceContext context = ServiceContext.getContext();
        context.clearHeader();
        context.addHeaders(cloneHeaders);
        if(isGenerateNewFlowEachRun()) {
            String oldFlowNo = context.getRequestNo();
            String newFlowNo = ServiceContext.getContext("ServiceContextTask-").getRequestNo();
            logger.info("������ˮ��ǿ�Ƹ���Ϊ��{} -> {}", oldFlowNo, newFlowNo);
        }
        ContextSlf4jUtil.addLogKey2MDC(context);
    }
    
    public abstract void runTask();
    
    public final boolean isGenerateNewFlowEachRun() {
        return isGeneratenewFlowEachRun;
    }
    
    public final void setGenerateNewFlowEachRun(boolean isGenerateNewFlowEachRun) {
        this.isGeneratenewFlowEachRun = isGenerateNewFlowEachRun;
    }
    
}
