package com.foros.rs.client.model.publishing;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class TagSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("tag.ids")
    private List<Long> tagIds;

    @QueryParameter("site.ids")
    private List<Long> siteIds;

    @QueryParameter("tag.statuses")
    private List<Status> tagStatuses;

    public PagingSelector getPaging() {
        return paging;
    }

    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Long> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Status> getTagStatuses() {
        return tagStatuses;
    }

    public void setTagStatuses(List<Status> tagStatuses) {
        this.tagStatuses = tagStatuses;
    }
}
