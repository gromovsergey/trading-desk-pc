package com.foros.rs.client.model.advertising.campaign;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class YandexCreativeSelector implements PagingSelectorContainer {
    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("creative.ids")
    private List<Long> creativeIds;

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getCreativeIds() {
        return this.creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }
}
