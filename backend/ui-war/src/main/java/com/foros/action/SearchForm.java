package com.foros.action;

public class SearchForm {

    private Long page = 1L;
    private int pageSize = 100;
    private Long total = 0L;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstResultCount(){
        return page == 1 ? 0 : (page.intValue() - 1) * pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
