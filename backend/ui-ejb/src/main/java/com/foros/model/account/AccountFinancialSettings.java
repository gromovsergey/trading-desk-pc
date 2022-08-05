package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.security.User;
import com.foros.util.NumberUtil;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.annotation.security.DenyAll;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ACCOUNTFINANCIALSETTINGS")
@Inheritance
@DiscriminatorColumn(name = "TYPE")
public abstract class AccountFinancialSettings extends VersionEntityBase implements Serializable, Identifiable {
    @RequiredConstraint
    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @HasIdConstraint
    @ChangesInspection(type = InspectionType.NONE)
    @OneToOne(mappedBy = "financialSettings")
    @Cascade(CascadeType.DETACH)
    private ExternalAccount account;

    @ByteLengthConstraint(length = 20)
    @Column(name = "TAX_NUMBER")
    private String taxNumber;

    @Column(name = "TAX_RATE")
    private BigDecimal taxRate;

    @StringSizeConstraint(size = 100)
    @Column(name = "TAX_NOTES")
    private String taxNotes;

    @StringSizeConstraint(size = 30)
    @Column(name = "INSURANCE_NUMBER")
    private String insuranceNumber;

    @Column(name = "COMMISSION", nullable = false)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(name = "MEDIA_HANDLING_FEE", nullable = false)
    private BigDecimal mediaHandlingFee = BigDecimal.ZERO;

    @JoinColumn(name = "DEFAULT_BILL_TO_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    private User defaultBillToUser;

    public AccountFinancialSettings() {
    }

    public Long getAccountId() {
        return this.accountId;
    }

    @Override
    public Long getId() {
        return getAccountId();
    }

    @Override
    public void setId(Long id) {
        this.accountId = id;
        this.registerChange("accountId");
    }

    @DenyAll
    public void setAccountId(Long id) {
        setId(id);
    }

    public Account getAccount() {
        return this.account;
    }

    protected void setAccount(Account account) {
        this.account = (ExternalAccount) account;
        this.registerChange("account");
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
        this.registerChange("taxNumber");
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(3)
    public BigDecimal getTaxRatePercent() {
        return NumberUtil.toPercents(getTaxRate());
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
        this.registerChange("taxRate");
        this.registerChange("taxRatePercent");
    }

    public void setTaxRatePercent(BigDecimal taxRate) {
        setTaxRate(NumberUtil.fromPercents(taxRate));
    }

    public String getTaxNotes() {
        return taxNotes;
    }

    public void setTaxNotes(String taxNotes) {
        this.taxNotes = taxNotes;
        this.registerChange("taxNotes");
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
        this.registerChange("insuranceNumber");
    }

    public BigDecimal getCommission() {
        return commission;
    }

    @RangeConstraint(min = "0", max = "99.99")
    @FractionDigitsConstraint(2)
    public BigDecimal getCommissionPercent() {
        return NumberUtil.toPercents(getCommission());
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
        this.registerChange("commission");
        this.registerChange("commissionPercent");
    }

    public void setCommissionPercent(BigDecimal commission) {
        setCommission(NumberUtil.fromPercents(commission));
    }

    public BigDecimal getMediaHandlingFee() {
        return mediaHandlingFee;
    }

    @RangeConstraint(min = "0", max = "99.999")
    @FractionDigitsConstraint(3)
    public BigDecimal getMediaHandlingFeePercent() {
        return NumberUtil.toPercents(getMediaHandlingFee());
    }

    public void setMediaHandlingFee(BigDecimal mediaHandlingFee) {
        this.mediaHandlingFee = mediaHandlingFee;
        this.registerChange("mediaHandlingFee");
        this.registerChange("mediaHandlingFeePercent");
    }

    public void setMediaHandlingFeePercent(BigDecimal mediaHandlingFee) {
        setMediaHandlingFee(NumberUtil.fromPercents(mediaHandlingFee));
    }

    public User getDefaultBillToUser() {
        return defaultBillToUser;
    }

    public void setDefaultBillToUser(User user) {
        this.defaultBillToUser = user;
        this.registerChange("defaultBillToUser");
    }

    @Override
    public String toString() {
        return "com.foros.model.account.AccountFinancialSettings[id=" + getAccountId() + "]";
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountFinancialSettings)) {
            return false;
        }

        AccountFinancialSettings other = (AccountFinancialSettings)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
}
