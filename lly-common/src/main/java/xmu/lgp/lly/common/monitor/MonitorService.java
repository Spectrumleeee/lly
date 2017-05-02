package xmu.lgp.lly.common.monitor;

public interface MonitorService {
    
    public abstract SystemStatus getStatus();
    
    public abstract String ping();
    
}
