package com.foros.rs.client.model.operation;

import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.lang.Long;


@QueryEntity
public class PagingSelector {

    
    @QueryParameter("paging.first")
    private Long first;

    
    @QueryParameter("paging.count")
    private Long count;


    
    public Long getFirst() {
        return this.first;
    }

    public void setFirst(Long first) {
        this.first = first;
    }

    
    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}