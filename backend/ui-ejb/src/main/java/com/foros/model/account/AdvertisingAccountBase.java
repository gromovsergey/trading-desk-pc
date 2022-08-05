package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.security.TextAdservingMode;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.ValuesConstraint;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public abstract class AdvertisingAccountBase extends ExternalAccount<AdvertisingFinancialSettings> {
    @ValuesConstraint(values = {"M", "A", "O"})
    @Column(name = "TEXT_ADSERVING")
    private Character textAdservingMode;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<CampaignCredit> campaignCredits = new LinkedHashSet<>();

    @StringSizeConstraint(size = 200)
    @Column(name = "CONTRACT_NUMBER")
    private String contractNumber;

    @Column(name = "CONTRACT_DATE")
    @Temporal(TemporalType.DATE)
    private Date contractDate;

    public AdvertisingAccountBase() {
        setFinancialSettings(new AdvertisingFinancialSettings());
    }

    public AdvertisingAccountBase(Long accountId) {
        super(accountId);
        setFinancialSettings(new AdvertisingFinancialSettings());
    }

    public AdvertisingAccountBase(Long id, String name) {
        super(id, name);
        setFinancialSettings(new AdvertisingFinancialSettings());
    }

    public TextAdservingMode getTextAdservingMode() {
        if (textAdservingMode != null) {
            return TextAdservingMode.valueOf(textAdservingMode);
        }
        return null;
    }

    public void setTextAdservingMode(TextAdservingMode textAdservingMode) {
        this.textAdservingMode = (textAdservingMode == null) ? null : textAdservingMode.getLetter();
        this.registerChange("textAdservingMode");
    }

    /**
     * Is account part of another account.
     * @return true = standalone.
     */
    public abstract boolean isStandalone();

    public abstract boolean isFinancialFieldsPresent();

    public Set<CampaignCredit> getCampaignCredits() {
        return new ChangesSupportSet<CampaignCredit>(this, "campaignCredits", campaignCredits);
    }

    public void setCampaignCredits(Set<CampaignCredit> campaignCredits) {
        this.campaignCredits = campaignCredits;
        this.registerChange("campaignCredits");
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
        this.registerChange("contractNumber");
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
        this.registerChange("contractDate");
    }
}
