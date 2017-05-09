package xmu.lgp.lly.common.exception;

public class BusinessTimeoutException extends BusinessException {

    private static final long serialVersionUID = -8982722437889108360L;

    public BusinessTimeoutException(String errCode, String errMsg, Throwable e) {
        super(errCode, errMsg, e);
    }

}
