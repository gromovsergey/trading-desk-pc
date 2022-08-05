package com.foros.action.site;

import com.foros.action.SearchForm;
import com.foros.model.site.SiteCreativeApprovalStatus;

import java.util.HashSet;
import java.util.Set;

public class SiteCreativesApprovalSearchForm extends SearchForm {
    private Long sizeId;
    private String destinationUrl;
    private Set<SiteCreativeApprovalStatus> approvalStatuses = new HashSet<SiteCreativeApprovalStatus>();

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public Set<SiteCreativeApprovalStatus> getApprovalStatuses() {
        return approvalStatuses;
    }

    public void setApprovalStatuses(Set<SiteCreativeApprovalStatus> approvalStatuses) {
        this.approvalStatuses = approvalStatuses;
    }

    @Override
    public String toString() {
        return "SiteCreativesApprovalSearchForm[sizeId=" + sizeId + ", destinationUrl=" + destinationUrl + "], approvalStatuses=" + approvalStatuses;
    }
}
