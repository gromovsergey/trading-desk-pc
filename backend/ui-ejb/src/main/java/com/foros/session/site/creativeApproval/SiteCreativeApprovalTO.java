package com.foros.session.site.creativeApproval;

import com.foros.jaxb.adapters.TimestampXmlAdapter;
import com.foros.model.site.CreativeRejectReason;
import com.foros.model.site.SiteCreativeApprovalStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.Timestamp;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "siteCreativeApproval")
@XmlType(propOrder = {
        "creative",
        "approvalStatus",
        "rejectReason",
        "feedback",
        "version"
})
public class SiteCreativeApprovalTO {
    private CreativeForApprovalTO creative;
    private SiteCreativeApprovalStatus approvalStatus;
    private CreativeRejectReason rejectReason;
    private String feedback;
    private Timestamp version;

    public CreativeForApprovalTO getCreative() {
        return creative;
    }

    public void setCreative(CreativeForApprovalTO creative) {
        this.creative = creative;
    }

    @XmlElement(name = "status")
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

    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    @XmlElement(name = "updated")
    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
