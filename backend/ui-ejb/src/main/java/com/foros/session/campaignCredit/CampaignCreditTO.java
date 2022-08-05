package com.foros.session.campaignCredit;

import com.foros.model.campaign.CampaignCreditPurpose;
import com.foros.session.IdentifiableTO;
import com.foros.session.NamedTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CampaignCreditTO extends IdentifiableTO implements Serializable {
    private Date version;
    private CampaignCreditPurpose purpose;
    private String description;
    private NamedTO advertiser;
    private BigDecimal amount;
    private BigDecimal balance;
    private boolean hasAllocations;

    public CampaignCreditTO() {
    }

    public CampaignCreditTO(Long id, Date version, CampaignCreditPurpose purpose, String description, Long advertiserId,
                            String advertiserName, BigDecimal amount, BigDecimal balance, boolean hasAllocations) {
        super(id);
        this.version = version;
        this.purpose = purpose;
        this.description = description;
        this.advertiser = new NamedTO(advertiserId, advertiserName);
        this.amount = amount;
        this.balance = balance;
        this.hasAllocations = hasAllocations;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public CampaignCreditPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CampaignCreditPurpose purpose) {
        this.purpose = purpose;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NamedTO getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(NamedTO advertiserAccount) {
        this.advertiser = advertiserAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isHasAllocations() {
        return hasAllocations;
    }

    public void setHasAllocations(boolean hasAllocations) {
        this.hasAllocations = hasAllocations;
    }
}
