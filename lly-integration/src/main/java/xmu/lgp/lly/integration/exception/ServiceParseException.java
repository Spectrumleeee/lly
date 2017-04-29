package xmu.lgp.lly.integration.exception;

public class ServiceParseException extends RuntimeException {

    private static final long serialVersionUID = -4849124243378050226L;
    
    public ServiceParseException() {
        
    }
    
    public ServiceParseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceParseException(String message) {
        super(message);
    }
    
    public ServiceParseException(Throwable cause) {
        super(cause);
    }
    
}
