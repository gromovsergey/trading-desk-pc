package com.foros.session.site.creativeApproval;

import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.sql.Timestamp;
import java.util.Set;

public class CreativeExclusionBySiteSelector implements Selector<SiteCreativeApprovalTO> {
    private Paging paging = new Paging();
    private Long siteId;
    private String destinationUrl;
    private Set<SiteCreativeApprovalStatus> approvals;
    private Set<Long> creativeIds;
    private Long sizeId;
    private Timestamp minCreativeVersion;
    private Boolean pendingThirdPartyApproval;
    private Boolean hasThirdPartyId;

    public CreativeExclusionBySiteSelector() {
    }

    public CreativeExclusionBySiteSelector(Long siteId) {
        this.siteId = siteId;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public Set<SiteCreativeApprovalStatus> getApprovals() {
        return approvals;
    }

    public void setApprovals(Set<SiteCreativeApprovalStatus> approvals) {
        this.approvals = approvals;
    }

    public Set<Long> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(Set<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Timestamp getMinCreativeVersion() {
        return minCreativeVersion;
    }

    public void setMinCreativeVersion(Timestamp minCreativeVersion) {
        this.minCreativeVersion = minCreativeVersion;
    }

    public Boolean getHasThirdPartyId() {
        return hasThirdPartyId;
    }

    public void setHasThirdPartyId(Boolean hasThirdPartyId) {
        this.hasThirdPartyId = hasThirdPartyId;
    }

    public Boolean getPendingThirdPartyApproval() {
        return pendingThirdPartyApproval;
    }

    public void setPendingThirdPartyApproval(Boolean pendingThirdPartyApproval) {
        this.pendingThirdPartyApproval = pendingThirdPartyApproval;
    }
}
