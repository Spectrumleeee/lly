package xmu.lgp.lly.common.exception;

public class SystemException extends ServiceException {

    private static final long serialVersionUID = 8453399318754393944L;

    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public SystemException(String errCode, String errMsg, Throwable e) {
        super(errCode, errMsg, e);
    }
    
    public SystemException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }
    
    public SystemException(String thirdErrCode, ErrorCode innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }
    
    public SystemException(ErrorCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }
    
    public SystemException(ErrorCode errCode, Object... paramList) {
        super(errCode, paramList);
    }
    
    public SystemException(ErrorCode errCode, Object[] params, Throwable cause) {
        super(errCode, params, cause);
    }
    
    public SystemException(String thirdErrCode, String innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }

}
