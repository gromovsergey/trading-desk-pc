package com.foros.model.campaign;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.opportunity.Opportunity;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CAMPAIGNALLOCATION")
public class CampaignAllocation extends VersionEntityBase implements Identifiable {
    @SequenceGenerator(name = "CampaignAllocationGen", sequenceName = "CAMPAIGNALLOCATION_CAMPAIGN_ALLOCATION_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignAllocationGen")
    @Column(name = "CAMPAIGN_ALLOCATION_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @ChangesInspection(type = InspectionType.NONE)
    @HasIdConstraint
    @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID", updatable = false)
    @ManyToOne
    private Campaign campaign;

    @RequiredConstraint
    @JoinColumn(name = "IO_ID", referencedColumnName = "IO_ID", updatable = false)
    @ManyToOne
    private Opportunity opportunity;

    @RequiredConstraint
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "UTILIZED_AMOUNT", updatable = false, insertable = false)
    private BigDecimal utilizedAmount;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.campaign.CampaignAllocationStatus"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter") })
    private CampaignAllocationStatus status;

    @RequiredConstraint
    @Column(name = "POSITION")
    private Long order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        registerChange("id");
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        registerChange("startDate");
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        registerChange("endDate");
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        registerChange("campaign");
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
        registerChange("opportunity");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        registerChange("amount");
    }

    public BigDecimal getUtilizedAmount() {
        return utilizedAmount;
    }

    public void setUtilizedAmount(BigDecimal utilizedAmount) {
        this.utilizedAmount = utilizedAmount;
        registerChange("utilizedAmount");
    }

    public CampaignAllocationStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignAllocationStatus status) {
        this.status = status;
        registerChange("status");
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
        registerChange("order");
    }
}
