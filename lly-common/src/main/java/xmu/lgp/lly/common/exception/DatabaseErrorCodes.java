package xmu.lgp.lly.common.exception;

public enum DatabaseErrorCodes implements ErrorCode {
    
    DUPLICATE_KEY("SDBE001"),
    UNKNOWN_ERROR("SDBE002"),
    CANT_GET_CONNECTION("SDBE003"),
    CONNECTION_TIMEOUT("SDBE004");
    
    private String code;
    
    private DatabaseErrorCodes(String code) {
        this.code = code;
    }
    
    @Override
    public String getCode() {
        return code;
    }

}
