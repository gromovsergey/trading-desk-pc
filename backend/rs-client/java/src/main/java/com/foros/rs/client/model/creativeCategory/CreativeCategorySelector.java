package com.foros.rs.client.model.creativeCategory;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class CreativeCategorySelector implements PagingSelectorContainer {

    @QueryParameter("ids")
    private List<Long> ids;

    @QueryParameter("type")
    private CreativeCategoryType type;

    @QueryParameter("paging")
    private PagingSelector paging;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public CreativeCategoryType getType() {
        return type;
    }

    public void setType(CreativeCategoryType type) {
        this.type = type;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}
