package xmu.lgp.lly.common.exception;

public abstract class ErrorMessageResource {
    
    private static volatile ErrorMessageResource instance = null;
    
    public static synchronized ErrorMessageResource getInstance() {
        if (instance == null) {
            instance = new DefaultErrorMessageResource();
        }
        
        return instance;
    }
    
    public static ErrorMessageResource setGlobalInstance(ErrorMessageResource resource) {
        ErrorMessageResource oldInstance = instance;
        instance = resource;
        return oldInstance;
    }
    
    public abstract String getMessage(ErrorCode errorCode);
    
    public abstract String getMessage(String errorCode);
}
