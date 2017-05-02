package xmu.lgp.lly.integration;

import xmu.lgp.lly.integration.dubbo.DubboProtocolComponent;

public class ServiceProtocolComponentFactory {
    
    public static final String DUBBO_PROTOCOL = "dubbo";
    
    public static final String EBOX_PROTOCOL = "ebox";
    
    private static DubboProtocolComponent dubboProtocolComp = new DubboProtocolComponent();
    
    public static ServiceProtocolComponent getServiceProtocolComponent(String protocol) {
        if ("dubbo".equalsIgnoreCase(protocol)) {
            return dubboProtocolComp;
        }
        if ("ebox".equalsIgnoreCase(protocol)) {
            throw new RuntimeException("此版本LSF已经不再提供ebox支持");
        }
        
        return null;
    }
}
