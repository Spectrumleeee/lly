package xmu.lgp.lly.common.exception;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractErrorMessageResource extends ErrorMessageResource implements InitializingBean, DisposableBean{

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void destroy() throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getMessage(ErrorCode errorCode) {
        return null;
    }
    
    @Override
    public String getMessage(String errorCode) {
        return null;
    }
    
    
}
