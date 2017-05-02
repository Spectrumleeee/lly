package xmu.lgp.lly.common.entity;

import java.util.List;

public class Hierarchy<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private T id;
    private T parentId;
    private List<Hierarchy<T>> children;
    private Hierarchy<T> parent;
    
    public T getId() {
        return id;
    }
    public void setId(T id) {
        this.id = id;
    }
    
    public T getParentId() {
        return parentId;
    }
    public void setParentId(T parentId) {
        this.parentId = parentId;
    }
    
    public List<Hierarchy<T>> getChildren() {
        return children;
    }
    public void setChildren(List<Hierarchy<T>> children) {
        this.children = children;
    }
    
    public Hierarchy<T> getParent() {
        return parent;
    }
    public void setParent(Hierarchy<T> parent) {
        this.parent = parent;
    }
    
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31*result + (id == null ? 0 : id.hashCode());
        return result;
    }
    
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        Hierarchy<T> other = (Hierarchy)obj;
        if(id == null) {
            return false;
        } else if (!id.equals(other.getId())) {
            return false;
        } else {
            return true;
        }
    }
    
}
