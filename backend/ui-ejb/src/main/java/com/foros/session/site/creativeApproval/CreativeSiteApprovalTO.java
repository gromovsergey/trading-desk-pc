package com.foros.session.site.creativeApproval;

import com.foros.model.DisplayStatus;
import com.foros.model.IdNameEntity;
import com.foros.model.site.CreativeRejectReason;
import com.foros.model.site.SiteCreativeApprovalStatus;

public class CreativeSiteApprovalTO {
    private IdNameEntity publisher;
    private DisplayStatus publisherDisplayStatus;
    private IdNameEntity site;
    private DisplayStatus siteDisplayStatus;
    private SiteCreativeApprovalStatus approvalStatus;
    private CreativeRejectReason rejectReason;
    private String feedback;

    public IdNameEntity getPublisher() {
        return publisher;
    }

    public void setPublisher(IdNameEntity publisher) {
        this.publisher = publisher;
    }

    public DisplayStatus getPublisherDisplayStatus() {
        return publisherDisplayStatus;
    }

    public void setPublisherDisplayStatus(DisplayStatus publisherDisplayStatus) {
        this.publisherDisplayStatus = publisherDisplayStatus;
    }

    public IdNameEntity getSite() {
        return site;
    }

    public void setSite(IdNameEntity site) {
        this.site = site;
    }

    public DisplayStatus getSiteDisplayStatus() {
        return siteDisplayStatus;
    }

    public void setSiteDisplayStatus(DisplayStatus siteDisplayStatus) {
        this.siteDisplayStatus = siteDisplayStatus;
    }

    public SiteCreativeApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(SiteCreativeApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public CreativeRejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(CreativeRejectReason rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
