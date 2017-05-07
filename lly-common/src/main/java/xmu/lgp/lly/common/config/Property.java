package xmu.lgp.lly.common.config;

import java.util.Properties;

public class Property {
    
    private static volatile Properties property;
    
    static void init(Properties props) {
        property = props;
    }
    
    public static String getProperty(String key) {
        if(key == null) {
            return null;
        }
        return property.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return property.getProperty(key, defaultValue);
    }
}

