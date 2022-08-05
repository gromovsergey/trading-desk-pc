package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AUCTIONSETTINGS")
public class AccountAuctionSettings extends VersionEntityBase implements Serializable, Identifiable {

    private static final BigDecimal MAX_ECPM_SHARE = BigDecimal.valueOf(95);
    private static final BigDecimal PROP_PROBABILITY_SHARE = BigDecimal.ZERO;
    private static final BigDecimal RANDOM_SHARE = BigDecimal.valueOf(5);
    private static final BigDecimal MAX_RANDOM_CPM = BigDecimal.ZERO;

    @RequiredConstraint
    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ACCOUNT_ID")
    @ChangesInspection(type = InspectionType.NONE)
    private Account account;

    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "MAX_ECPM_SHARE", nullable = false)
    private BigDecimal maxEcpmShare;

    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "PROP_PROBABILITY_SHARE", nullable = false)
    private BigDecimal propProbabilityShare;

    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "RANDOM_SHARE", nullable = false)
    private BigDecimal randomShare;

    @RequiredConstraint
    @Column(name = "MAX_RANDOM_CPM", nullable = false)
    private BigDecimal maxRandomCpm;

    public AccountAuctionSettings() {
    }

    public AccountAuctionSettings(Long id, boolean defaultValues) {
        this.id = id;
        if (defaultValues) {
            setMaxEcpmShare(MAX_ECPM_SHARE);
            setPropProbabilityShare(PROP_PROBABILITY_SHARE);
            setRandomShare(RANDOM_SHARE);
            setMaxRandomCpm(MAX_RANDOM_CPM);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getMaxEcpmShare() {
        return maxEcpmShare;
    }

    public void setMaxEcpmShare(BigDecimal maxEcpmShare) {
        this.maxEcpmShare = maxEcpmShare;
        this.registerChange("maxEcpmShare");
    }

    public BigDecimal getPropProbabilityShare() {
        return propProbabilityShare;
    }

    public void setPropProbabilityShare(BigDecimal propProbabilityShare) {
        this.propProbabilityShare = propProbabilityShare;
        this.registerChange("propProbabilityShare");
    }

    public BigDecimal getRandomShare() {
        return randomShare;
    }

    public void setRandomShare(BigDecimal randomShare) {
        this.randomShare = randomShare;
        this.registerChange("randomShare");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountAuctionSettings that = (AccountAuctionSettings) o;

        if (id == null || !id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[accountId=%d]", getClass().getName(), id);
    }

    public BigDecimal getMaxRandomCpm() {
        return maxRandomCpm;
    }

    public void setMaxRandomCpm(BigDecimal maxRandomCpm) {
        this.maxRandomCpm = maxRandomCpm;
        this.registerChange("maxRandomCpm");
    }
}
