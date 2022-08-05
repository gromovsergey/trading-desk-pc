package com.foros.rs.client.model;

import com.foros.rs.client.model.operation.PagingSelector;

public interface PagingSelectorContainer {
    PagingSelector getPaging();
    void setPaging(PagingSelector paging);
}
