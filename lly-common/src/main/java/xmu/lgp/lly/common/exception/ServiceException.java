package xmu.lgp.lly.common.exception;

import java.text.MessageFormat;
import java.util.Date;

import xmu.lgp.lly.common.util.DateUtil;

public abstract class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private String errMsg;
    private String errDesc;
    private String thirdErrCode;
    private String innerErrCode;
    private String innerErrCodeCls;
    private transient ErrorCode errCode;
    private transient Object[] params;
    
    public ServiceException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }
    
    public ServiceException(String thirdErrCode, String errMsg) {
        this(thirdErrCode, errMsg, (Throwable)null);
    }
    
    public ServiceException(String thirdErrCode, String errMsg, Throwable e) {
        this.errDesc = errMsg;
        this.thirdErrCode = thirdErrCode;
        this.errMsg = '[' + thirdErrCode + ']' + errMsg;
        super.initCause(e);
    }
    
    public ServiceException(String thirdErrCode, ErrorCode innerCode, Object... paramList) {
        this.thirdErrCode = thirdErrCode;
        initParamList(paramList);
        initInnerErrorCode(innerCode);
    }
    
    public ServiceException(String thirdErrCode, String innerCode, Object... paramList) {
        this.thirdErrCode = thirdErrCode;
        this.innerErrCode = innerCode;
        initParamList(paramList);
    }
    
    public ServiceException(ErrorCode errorCode, Object... paramList) {
        initParamList(paramList);
        initInnerErrorCode(errorCode);
    }
    
    public ServiceException(ErrorCode errCode, Object param, Throwable cause) {
        this(errCode, new Object[]{param}, cause);
    }
    
    public ServiceException(ErrorCode errCode, Object[] params, Throwable cause) {
        this.params = params;
        initInnerErrorCode(errCode);
        super.initCause(cause);
    }
    
    private void initInnerErrorCode(ErrorCode errCode) {
        errCode = errCode;
        if (errCode != null) {
            innerErrCode = errCode.getCode();
            innerErrCodeCls = errCode.getClass().getCanonicalName();
            
            if(errMsg == null) {
                errMsg = translateMessage(getCode(), getInnerErrorCode());
            }
        }
    }
    
    private void initParamList(Object... paramList) {
        if (paramList != null && paramList.length > 0 && paramList[paramList.length - 1] instanceof Throwable) {
            Object[] newParam = new Object[paramList.length -1 ];
            System.arraycopy(paramList, 0, newParam, 0, newParam.length);
            params = newParam;
            super.initCause((Throwable)paramList[paramList.length - 1]);
        } else {
            params = paramList;
            super.initCause(null);
        }
    }
    
    public String getMessage() {
        if (errMsg == null) {
            errMsg = translateMessage(getCode(), getInnerErrorCode());
        }
        
        if (errMsg != null) {
            return errMsg;
        }
        
        return "“Ï≥£,¥ÌŒÛ¬Î=" + getInnerErrorCode();
    }
    
    private String formatMsg(String msgPattern, Object[] params) {
        if (params == null) {
            return MessageFormat.format(msgPattern, params);
        }
        
        Object[] formatedParams = new String[params.length];
        for (int i=0; i<params.length; i++) {
            if (params[i] instanceof Date) {
                formatedParams[i] = DateUtil.format((Date)params[i], "yyyy-MM-dd");
            } else {
                formatedParams[i] = params[i] == null ? "null" : params[i].toString();
            }
        }
        
        return MessageFormat.format(msgPattern, formatedParams);
    }
    
    private String translateMessage(String showErrCode, String msgErrCode) {
        if (msgErrCode == null) {
            if (params != null && params.length > 0) {
                StringBuilder sb = new StringBuilder(8);
                for (int i = 0; i < params.length ; i++) {
                    sb.append("param").append(i).append("={},");
                }
                sb.deleteCharAt(sb.length() - 1).append('.');
                errDesc = formatMsg(sb.toString(), params);
            } else {
                errDesc = "Œ¥÷™¥ÌŒÛ";
                params = null;
            }
            return "[" + showErrCode + "]" + errDesc;
        }
        
        ErrorMessageResource excepResource = ErrorMessageResource.getInstance();
        String msgPattern = excepResource.getMessage(msgErrCode);
        if (msgPattern != null) {
            try {
                errDesc = formatMsg(msgPattern, params);
                return "[" + showErrCode + "]" + errDesc;
            } catch (Exception e) {
                errDesc = "Œ¥÷™¥ÌŒÛ" + e.getMessage();
                return "¥ÌŒÛ¬Î[" + msgErrCode + "]Ω‚Œˆ¥ÌŒÛ£¨¥ÌŒÛœ˚œ¢∏Ò Ω≈‰÷√Œ™£∫" + msgPattern + ",±®¥Ì–≈œ¢" + e.getMessage();
            }
        }
        
        errDesc = "¥ÌŒÛ¬Î[" + msgErrCode + "]Œ¥ ∂±µƒ¥ÌŒÛ°£";
        return null;
    }
    
    public String getErrDesc() {
        return getPlainErrMsg();
    }
    
    public String getPlainErrMsg() {
        if (errDesc == null) {
            translateMessage(getCode(), getInnerErrorCode());
        }
        return errDesc;
    }
    
    public ErrorCode getErrorCode() {
        if (errCode != null) {
            return errCode;
        }
        
        if(innerErrCodeCls == null || innerErrCode == null) {
            return null;
        }
        
        ErrorCode[] codes = null;
        try {
            Class<?> o = Class.forName(innerErrCodeCls);
            Object[] t = o.getEnumConstants();
            if (t instanceof ErrorCode[]) {
                codes = (ErrorCode[])t;
            }
        } catch (Exception e) {
            return null;
        }
        
        if (codes == null) {
            return null;
        }
        
        for (ErrorCode e : codes) {
            if (e.getCode().equals(innerErrCode)) {
                errCode = e;
                break;
            }
        }
        return errCode;
    }
    
    public String getInnerErrorCode() {
        return innerErrCode;
    }
    
    public String getThirdErrCode() {
        return thirdErrCode;
    }
    
    public String getCode() {
        return thirdErrCode != null ? thirdErrCode : innerErrCode;
    }
    
}
