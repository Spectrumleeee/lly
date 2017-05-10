package xmu.lgp.lly.common.query;

import xmu.lgp.lly.common.entity.BaseEntity;

public class SortField extends BaseEntity {

    private static final long serialVersionUID = -8078797709379420187L;
    private String fieldName;
    private boolean asc = true;
    
    public SortField(){}
    
    public SortField(String fieldName, boolean asc) {
        this.fieldName = fieldName;
        this.asc = asc;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public boolean isAsc() {
        return asc;
    }
    
    public void setAsc(boolean asc) {
        this.asc = asc;
    }
    
}
