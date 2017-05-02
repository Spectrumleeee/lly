package xmu.lgp.lly.common.entity;

public class LlyDataEntity extends BaseEntity implements ILlyDataEntity {
    
    private static final long serialVersionUID = 4294112934761376885L;

    private String fromRuleKey;
    
    @Override
    public void setFromRuleKey(String fromRuleKey) {
        this.fromRuleKey = fromRuleKey;
    }

    @Override
    public String getFromRuleKey() {
        return fromRuleKey;
    }

}
