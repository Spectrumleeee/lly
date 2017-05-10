package xmu.lgp.lly.common.query;

import java.util.List;

import xmu.lgp.lly.common.entity.BaseEntity;

public class PagedQueryParam<T> extends BaseEntity  {

    private static final long serialVersionUID = 6256057229510458903L;
    public static final int MAX_PAGE_SIZE = 100;
    
    private int pageSize = 0;
    private int pageNo = 0;
    private long total = 0L;
    private T queryParam;
    private List<SortField> sortFields;
    
    public int getPageSize() {
        return pageSize <= 0 ? 100 : pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public T getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(T queryParam) {
        this.queryParam = queryParam;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    
    public boolean needPage() {
        return pageNo > 0;
    }
    
    public boolean needGetTotal(int currPageRealSize) {
        if (pageNo <= 0) {
            return false;
        }
        
        if (total <= 0L) {
            if (pageNo == 1 && currPageRealSize < pageSize) {
                total = currPageRealSize;
                return false;
            }
            return true;
        }
        
        long totalPage = total / pageSize;
        if (total % pageSize != 0L) {
            totalPage += 1L;
        }
        
        if (pageNo < totalPage) {
            return currPageRealSize != pageSize;
        }
        if (pageNo == totalPage) {
            if (currPageRealSize == 0) {
                return true;
            }
            long realSize = (totalPage - 1L) * pageSize + currPageRealSize;
            if (realSize != total) {
                return true;
            }
            
            return currPageRealSize == pageSize;
        }
        
        return true;
    }
    
}
