package xmu.lgp.lly.common.exception;

public interface ErrorCode {
    
    public static final String CTS_SUCCESS_CODE = "CTS00000";
    public static final String CIF_SUCCESS_CODE = "COM00000";
    public static final String TFC_SUCCESS_CODE = "SUC00000";
    public static final String CTS_SYS_ERROR_CODE = "CTS99999";
    public static final String CIF_SYS_ERROR_CODE = "COM99999";
    public static final String TFC_SYS_ERROR_CODE = "TFC01000|TFC999XX|TFC998XX|COM99999";
    public static final String NOTIFY_SUCCESS_CODE = "COM00000";
    public static final String NOTIFY_SYS_ERROR_CODE = "COM99999";
    public static final String HUNTER_SUCCESS_CODE = "000000";
    public static final String HUNTER_SYS_ERROR_CODE = "999999";
    public static final String ZX_SUCCESS_CODE = "000000";
    
    public abstract String getCode();
}
