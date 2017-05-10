package xmu.lgp.lly.common.query;

import java.util.List;

import xmu.lgp.lly.common.entity.BaseEntity;

public class PagedResult<T> extends BaseEntity {

    private static final long serialVersionUID = -1001384566434809178L;
    private List<T> dataList;
    private long total = 0L;
    
    private long pageSize;
    private long pageNo;
    
    public PagedResult() {}
    
    public PagedResult(List<T> dataList, long total) {
        this.dataList = dataList;
        this.total = total;
    }
    
    public List<T> getDataList() {
        return dataList;
    }
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
    
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    
    public long getPageSize() {
        return pageSize;
    }
    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
    
    public long getPageNo() {
        return pageNo;
    }
    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }
    
    public long getTotalPage() {
        if(pageSize <= 0L) {
            return total > 0L ? 1L : 0L;
        }
        return (total + pageSize - 1L) / pageSize;
    }
    
}
