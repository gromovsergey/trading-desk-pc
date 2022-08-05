package com.foros.rs.client.model.siteCreative;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.lang.Boolean;
import java.lang.Long;
import java.util.List;


@QueryEntity
public class ThirdPartyCreativeSelector implements PagingSelectorContainer {

    @QueryParameter("site.id")
    private Long siteId;

    @QueryParameter("pendingThirdPartyApproval")
    private Boolean pendingThirdPartyApproval;

    @QueryParameter("hasThirdPartyId")
    private Boolean hasThirdPartyId;

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("status")
    private List<SiteCreativeApprovalStatus> statuses;

    @QueryParameter("creative.id")
    private List<Long> creativeIds;

    public Long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Boolean getPendingThirdPartyApproval() {
        return this.pendingThirdPartyApproval;
    }

    public void setPendingThirdPartyApproval(Boolean pendingThirdPartyApproval) {
        this.pendingThirdPartyApproval = pendingThirdPartyApproval;
    }

    public Boolean getHasThirdPartyId() {
        return this.hasThirdPartyId;
    }

    public void setHasThirdPartyId(Boolean hasThirdPartyId) {
        this.hasThirdPartyId = hasThirdPartyId;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<SiteCreativeApprovalStatus> getStatuses() {
        return this.statuses;
    }

    public void setStatuses(List<SiteCreativeApprovalStatus> statuses) {
        this.statuses = statuses;
    }

    public List<Long> getCreativeIds() {
        return this.creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }
}