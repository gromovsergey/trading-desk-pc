package com.foros.rs.client.model.device;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

@QueryEntity
public class PlatformSelector implements PagingSelectorContainer {

    public PlatformSelector() {
    }

    public PlatformSelector(PagingSelector paging) {
        this.paging = paging;
    }

    @QueryParameter("paging")
    private PagingSelector paging;

    @Override
    public PagingSelector getPaging() {
        return paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}
