package xmu.lgp.lly.common.notify;

public interface NotifyApi {
    
    public String sendSms(String param1, String param2, String param3, String param4, Object[] objs);
    
    public String sendEmail(String param1, String param2, String param3, String param4, Object[] objs);
    
}
