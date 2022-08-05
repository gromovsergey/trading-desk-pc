package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;


@Entity
public class CampaignAllocation extends VersionEntityBase<Long> {
    @SequenceGenerator(name = "CampaignAllocationGen", sequenceName = "CAMPAIGNALLOCATION_CAMPAIGN_ALLOCATION_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignAllocationGen")
    @Column(name = "CAMPAIGN_ALLOCATION_ID", nullable = false)
    private Long id;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "CAMPAIGN_ID", updatable = false)
    private Long campaignId;

    @JoinColumn(name = "IO_ID", referencedColumnName = "IO_ID", updatable = false)
    @ManyToOne(cascade = CascadeType.REFRESH)
    private Opportunity opportunity;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "UTILIZED_AMOUNT", updatable = false, insertable = false)
    private BigDecimal utilizedAmount;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignAllocationStatus status;

    @Column(name = "POSITION")
    private Long order;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUtilizedAmount() {
        return utilizedAmount;
    }

    public void setUtilizedAmount(BigDecimal utilizedAmount) {
        this.utilizedAmount = utilizedAmount;
    }

    public CampaignAllocationStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignAllocationStatus status) {
        this.status = status;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
