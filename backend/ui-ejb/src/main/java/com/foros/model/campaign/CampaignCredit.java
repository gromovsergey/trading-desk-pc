package com.foros.model.campaign;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.OwnedEntity;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "CAMPAIGNCREDIT")
public class CampaignCredit extends VersionEntityBase implements Identifiable, OwnedEntity {
    public static final BigDecimal AMOUNT_MAX = new BigDecimal("1000000000");

    @SequenceGenerator(name = "CampaignCreditGen", sequenceName = "CAMPAIGNCREDIT_CAMPAIGN_CREDIT_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignCreditGen")
    @Column(name = "CAMPAIGN_CREDIT_ID", nullable = false)
    @IdConstraint
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", nullable = false, updatable = false)
    @ManyToOne
    private Account account;

    @JoinColumn(name = "ADV_ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    private AdvertiserAccount advertiser;

    @Enumerated(EnumType.STRING)
    @Column(name = "PURPOSE", nullable = false)
    @RequiredConstraint
    private CampaignCreditPurpose purpose;

    @RequiredConstraint
    @Column(name = "DESCRIPTION", nullable = false)
    @StringSizeConstraint(size = 4000)
    private String description;

    @RequiredConstraint
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "campaignCredit", fetch = FetchType.LAZY)
    private Set<CampaignCreditAllocation> allocations = new LinkedHashSet<CampaignCreditAllocation>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        registerChange("id");
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        registerChange("account");
    }

    public AdvertiserAccount getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(AdvertiserAccount advertiserAccount) {
        this.advertiser = advertiserAccount;
        registerChange("advertiser");
    }

    public CampaignCreditPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CampaignCreditPurpose purpose) {
        this.purpose = purpose;
        this.registerChange("purpose");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        registerChange("description");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        registerChange("amount");
    }

    @XmlTransient
    public Set<CampaignCreditAllocation> getAllocations() {
        return new ChangesSupportSet<CampaignCreditAllocation>(this, "allocations", allocations);
    }

    public void setAllocations(Set<CampaignCreditAllocation> allocations) {
        this.allocations = allocations;
        this.registerChange("allocations");
    }
}
