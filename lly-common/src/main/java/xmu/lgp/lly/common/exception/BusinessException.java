package xmu.lgp.lly.common.exception;

public class BusinessException extends ServiceException {
    
    public BusinessException(ErrorCode errCode) {
        super(errCode);
    }
    
    public BusinessException(String errCode, String errMsg, Throwable e) {
        super(errCode, errMsg, e);
    }
    
    public BusinessException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }
    
    public BusinessException(ErrorCode errCode, Object param, Throwable cause) {
        super(errCode, param, cause);
    }
    
    public BusinessException(ErrorCode errCode, Object[] param, Throwable cause) {
        super(errCode, param, cause);
    }
    
    public BusinessException(String thirdErrCode, ErrorCode innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }
    
    public BusinessException(String thirdErrCode, String innerCode, Object... paramList) {
        super(thirdErrCode, innerCode, paramList);
    }
}
