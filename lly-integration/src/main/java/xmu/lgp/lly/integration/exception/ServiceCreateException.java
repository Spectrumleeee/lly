package xmu.lgp.lly.integration.exception;

public class ServiceCreateException extends RuntimeException {

    private static final long serialVersionUID = 6433621502099399418L;

    public ServiceCreateException() {

    }

    public ServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceCreateException(String message) {
        super(message);
    }

    public ServiceCreateException(Throwable cause) {
        super(cause);
    }
}
