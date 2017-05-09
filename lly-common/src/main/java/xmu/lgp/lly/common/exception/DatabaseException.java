package xmu.lgp.lly.common.exception;

public class DatabaseException extends SystemException {

    private static final long serialVersionUID = -4859091566483146791L;
    
    public DatabaseException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public DatabaseException(ErrorCode errCode, Object... paramList) {
        super(errCode, paramList);
    }
    
    public DatabaseException(ErrorCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }
    
    public DatabaseException(ErrorCode errCode, Object[] params, Throwable cause) {
        super(errCode, params, cause);
    }
    
}
