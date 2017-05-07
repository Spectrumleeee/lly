package xmu.lgp.lly.common.monitor;

import java.util.List;

import xmu.lgp.lly.common.entity.BaseEntity;

public class SystemStatus extends BaseEntity {

    private static final long serialVersionUID = 2265474855138583119L;
    private String ip;
    private String processUser;
    private String llyVersion;
    private List<String> jvmArgs;
    
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getProcessUser() {
        return processUser;
    }
    public void setProcessUser(String processUser) {
        this.processUser = processUser;
    }
    
    public String getLlyVersion() {
        return llyVersion;
    }
    public void setLlyVersion(String llyVersion) {
        this.llyVersion = llyVersion;
    }
    
    public List<String> getJvmArgs() {
        return jvmArgs;
    }
    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }
    
    
    
    
}
