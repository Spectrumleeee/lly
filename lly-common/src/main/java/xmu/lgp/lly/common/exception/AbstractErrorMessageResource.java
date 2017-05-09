package xmu.lgp.lly.common.exception;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractErrorMessageResource extends ErrorMessageResource implements InitializingBean, DisposableBean{
    
    private ErrorMessageResource oldInstance;
    private ErrorMessageResource parent;
    
    private boolean autoRegister = false;
    private boolean registered = false;
    
    protected abstract String doGetMessage(String paramString);
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if(autoRegister) {
            oldInstance = ErrorMessageResource.setGlobalInstance(this);
            registered = true;
        }
    }

    @Override
    public void destroy() throws Exception {
        if(registered) {
            ErrorMessageResource.setGlobalInstance(oldInstance);
            registered = false;
        }
    }
    
    @Override
    public String getMessage(ErrorCode errorCode) {
        return getMessage(errorCode.getCode());
    }
    
    @Override
    public String getMessage(String errorCode) {
        String message = doGetMessage(errorCode);
        if (message == null && parent != null) {
            message = parent.getMessage(errorCode);
        }
        
        return message;
    }

    public ErrorMessageResource getOldInstance() {
        return oldInstance;
    }

    public void setOldInstance(ErrorMessageResource oldInstance) {
        this.oldInstance = oldInstance;
    }

    public boolean isAutoRegister() {
        return autoRegister;
    }

    public void setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public ErrorMessageResource getParent() {
        return parent;
    }

    public void setParent(ErrorMessageResource parent) {
        this.parent = parent;
    }
    
    
}
