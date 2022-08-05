package com.foros.model.campaign;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.Account;
import com.foros.model.security.OwnedEntity;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@NamedQueries({
        @NamedQuery(name = "CampaignCreditAllocation.findByCampaignCreditId", query =
                "SELECT a FROM CampaignCreditAllocation a " +
                "WHERE a.campaignCredit.id = :campaignCreditId " +
                "ORDER BY a.version DESC"),
        @NamedQuery(name = "CampaignCreditAllocation.findByCampaignId", query =
                "SELECT a FROM CampaignCreditAllocation a " +
                        "WHERE a.campaign.id = :campaignId " +
                        "AND a.allocatedAmount - a.usedAmount > 0"),
        @NamedQuery(name = "CampaignCreditAllocation.countByCampaignId", query =
        "SELECT count(a) FROM CampaignCreditAllocation a " +
                "WHERE a.campaign.id = :campaignId")
})
@Entity
@Table(name = "CAMPAIGNCREDITALLOCATION")
@SecondaryTable(name="CAMPAIGNCREDITALLOCATIONUSAGE", pkJoinColumns={
        @PrimaryKeyJoinColumn(name="CAMP_CREDIT_ALLOC_ID", referencedColumnName="CAMP_CREDIT_ALLOC_ID")
})
public class CampaignCreditAllocation extends VersionEntityBase implements Identifiable, OwnedEntity {

    @SequenceGenerator(name = "CampaignCreditAllocationGen", sequenceName = "CAMPAIGNCREDITALLOCATION_CAMP_CREDIT_ALLOC_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignCreditAllocationGen")
    @Column(name = "CAMP_CREDIT_ALLOC_ID", nullable = false)
    @IdConstraint
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "CAMPAIGN_CREDIT_ID", referencedColumnName = "CAMPAIGN_CREDIT_ID", nullable = false, updatable = false)
    @ManyToOne
    private CampaignCredit campaignCredit;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID", nullable = false, updatable = false)
    @ManyToOne
    private Campaign campaign;

    @RequiredConstraint
    @Column(name = "ALLOCATED_AMOUNT", nullable = false)
    private BigDecimal allocatedAmount;

    @Column(table = "CAMPAIGNCREDITALLOCATIONUSAGE", name = "USED_AMOUNT", nullable = false)
    private BigDecimal usedAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        registerChange("id");
    }

    public CampaignCredit getCampaignCredit() {
        return campaignCredit;
    }

    public void setCampaignCredit(CampaignCredit campaignCredit) {
        this.campaignCredit = campaignCredit;
        registerChange("campaignCredit");
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        registerChange("campaign");
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
        registerChange("allocatedAmount");
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
        registerChange("usedAmount");
    }

    @Override
    public Account getAccount() {
        return campaignCredit.getAccount();
    }
}
