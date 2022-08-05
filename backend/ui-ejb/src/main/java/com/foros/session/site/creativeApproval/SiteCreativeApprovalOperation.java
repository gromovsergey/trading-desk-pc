package com.foros.session.site.creativeApproval;

import com.foros.jaxb.adapters.CreativeApprovaloperationTypeXmlAdapter;
import com.foros.jaxb.adapters.CreativeRejectReasonXmlAdapter;
import com.foros.jaxb.adapters.IdNameTOLinkXmlAdapter;
import com.foros.model.site.CreativeRejectReason;
import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.session.bulk.IdNameTO;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlType(propOrder = {
        "creative",
        "rejectReason",
        "feedback",
        "type"
})
public class SiteCreativeApprovalOperation {
    private SiteCreativeApprovalOperationType type;
    private IdNameTO creative;
    private CreativeRejectReason rejectReason;
    private String feedback;
    private Timestamp version;
    private SiteCreativeApprovalStatus previousStatus;

    @XmlJavaTypeAdapter(IdNameTOLinkXmlAdapter.class)
    public IdNameTO getCreative() {
        return creative;
    }

    public void setCreative(IdNameTO creative) {
        this.creative = creative;
    }

    @XmlTransient
    public Long getCreativeId() {
        return creative == null ? null : creative.getId();
    }

    public void setCreativeId(Long id) {
        if (creative == null) {
            creative = new IdNameTO();
        }
        creative.setId(id);
    }

    @XmlJavaTypeAdapter(CreativeRejectReasonXmlAdapter.class)
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

    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CreativeApprovaloperationTypeXmlAdapter.class)
    public SiteCreativeApprovalOperationType getType() {
        return type;
    }

    public void setType(SiteCreativeApprovalOperationType type) {
        this.type = type;
    }

    @XmlTransient
    public SiteCreativeApprovalStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(SiteCreativeApprovalStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    @XmlTransient
    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }
}