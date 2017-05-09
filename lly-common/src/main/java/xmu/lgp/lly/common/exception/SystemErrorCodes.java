package xmu.lgp.lly.common.exception;

public enum SystemErrorCodes implements ErrorCode {
    
    SYSTEM_UNKOWN_ERROR("SSYS001"),
    RESOURCE_NOT_EXISTE("SSYS002"),
    CONCURRENT_REQUEST_OVERLIMIT("SSYS003"),
    SEQUENCE_NOT_EXIST("SSYS004"),
    ILLEGAL_ENUM_VALUE("SSYS005"),
    INVALID_PARAM_VALUE("SSYS006"),
    INVALID_PARAM_VALUE2("SSYS007"),
    ENCRYPT_ERROR("SSYS008"),
    REDIS_CACHE_ERROR("SSYS009");
    
    private String errorCode;
    
    private SystemErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }
    
    @Override
    public String getCode() {
        return errorCode;
    }

}
