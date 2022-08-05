package com.foros.rs.client.model.publishing;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class SiteSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("site.ids")
    private List<Long> siteIds;

    @QueryParameter("account.ids")
    private List<Long> publisherIds;

    @QueryParameter("site.statuses")
    private List<Status> siteStatuses;

    @Override
    public PagingSelector getPaging() {
        return paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Long> getPublisherIds() {
        return publisherIds;
    }

    public void setPublisherIds(List<Long> publisherIds) {
        this.publisherIds = publisherIds;
    }

    public List<Status> getSiteStatuses() {
        return siteStatuses;
    }

    public void setSiteStatuses(List<Status> siteStatuses) {
        this.siteStatuses = siteStatuses;
    }
}