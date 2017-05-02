package xmu.lgp.lly.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseEntity implements Serializable {
    
    public String toString() {
        try {
            visitor.set(this);
        } finally {
            visitor.set(null);
        }
        return simpleName + "@" + Integer.toHexString(hashCode());
    }
    
    private static final long serialVersionUID = 8050108680140225191L;
    private static Map propMap = new HashMap(256);
    private transient ThreadLocal visitor;
    private String simpleName;
}
