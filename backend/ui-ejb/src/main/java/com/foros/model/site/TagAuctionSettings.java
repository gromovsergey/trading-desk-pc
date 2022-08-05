package com.foros.model.site;

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
@Table(name = "TAGAUCTIONSETTINGS")
public class TagAuctionSettings extends VersionEntityBase implements Serializable, Identifiable {

    @RequiredConstraint
    @Id
    @Column(name = "TAG_ID", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "TAG_ID")
    @ChangesInspection(type = InspectionType.NONE)
    private Tag tag;

    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "MAX_ECPM_SHARE", nullable = false)
    private BigDecimal maxEcpmShare;

    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "PROP_PROBABILITY_SHARE", nullable = false)
    private BigDecimal propProbabilityShare;

    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(value = 2)
    @Column(name = "RANDOM_SHARE", nullable = false)
    private BigDecimal randomShare;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Tag getTag() {
        return tag;
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

    public boolean isAllAllocationsNull() {
        return maxEcpmShare == null && propProbabilityShare == null && randomShare == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagAuctionSettings that = (TagAuctionSettings) o;

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
        return String.format("%s[tagId=%d]", getClass().getName(), id);
    }
}
