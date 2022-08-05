package com.foros.model.site;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.EntityBase;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "SITECREATIVEAPPROVAL")
public class SiteCreativeApproval extends EntityBase implements Serializable {
    @EmbeddedId
    @ChangesInspection(type = InspectionType.NONE)
    private SiteCreativePK id;

    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.site.SiteCreativeApprovalStatus"),
            @Parameter(name = "identifierMethod", value = "getLetter") })
    @Column(name = "APPROVAL", nullable = false)
    private SiteCreativeApprovalStatus approval;

    @Column(name = "APPROVAL_DATE")
    @Version
    private Timestamp approvalDate;

    @Column(name = "REJECT_REASON_ID")
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClass", value = "com.foros.model.site.CreativeRejectReason"),
            @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getId")
    })
    private CreativeRejectReason rejectReason;

    @Column(name = "FEEDBACK")
    private String feedback;

    public SiteCreativeApproval() {
    }

    public SiteCreativeApproval(SiteCreativePK id) {
        this.id = id;
    }

    public SiteCreativePK getId() {
        return this.id;
    }

    public void setId(SiteCreativePK id) {
        this.id = id;
        this.registerChange("siteCreativeApprovalPK");
    }

    public SiteCreativeApprovalStatus getApproval() {
        return this.approval;
    }

    public void setApproval(SiteCreativeApprovalStatus approval) {
        this.approval = approval;
        this.registerChange("approval");
    }

    public Timestamp getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Timestamp approvalDate) {
        this.approvalDate = approvalDate;
        this.registerChange("approvalDate");
    }

    public CreativeRejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(CreativeRejectReason rejectReason) {
        this.rejectReason = rejectReason;
        this.registerChange("rejectReason");
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.registerChange("feedback");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiteCreativeApproval)) {
            return false;
        }
        SiteCreativeApproval other = (SiteCreativeApproval)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.site.SiteCreativeApproval[SiteCreativePK=" + getId() + "]";
    }
}
