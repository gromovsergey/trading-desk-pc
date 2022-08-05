package com.foros.model.admin;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "FRAUDCONDITION")
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@NamedQueries({
    @NamedQuery(name = "FraudCondition.findAll", query = "SELECT fc FROM FraudCondition fc order by fc.id")
})
public class FraudCondition extends VersionEntityBase implements Serializable, Identifiable {

    public static final int LIMIT_MINIMUM_VALUE = 2;
    public static final int LIMIT_MAXIMUM_VALUE = 10000;
    public static final int PERIOD_MINIMUM_VALUE = 1;
    public static final int PERIOD_MAXIMUM_VALUE = 24 * 60 * 60;
    public static final int USER_INACTIVITY_TIMEOUT_MINIMUM = 1;
	public static final int USER_INACTIVITY_TIMEOUT_MAXIMUM = 1440 * 60;
    public static final int CONDITIONS_MAX_COUNT = 100;

    @SequenceGenerator(name = "FraudConditionGen", sequenceName = "FRAUDCONDITION_FRAUD_CONDITION_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FraudConditionGen")
    @Column(name = "FRAUD_CONDITION_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @IdConstraint
    private Long id;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @RequiredConstraint
    private FraudConditionType type = FraudConditionType.CLK;

    @Column(name = "PERIOD")
    @RequiredConstraint
    @RangeConstraint(min = "" + PERIOD_MINIMUM_VALUE, max = "" + PERIOD_MAXIMUM_VALUE)
    private Long period;

    @Column(name = "`limit`")
    @RequiredConstraint
    @RangeConstraint(min = "" + LIMIT_MINIMUM_VALUE, max = "" + LIMIT_MAXIMUM_VALUE)
    private Long limit;

    public FraudCondition() {
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
        registerChange("id");
    }

    public FraudConditionType getType() {
        return type;
    }

    public void setType(FraudConditionType type) {
        this.type = type;
        registerChange("type");
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
        registerChange("period");
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
        registerChange("limit");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FraudCondition)) {
            return false;
        }

        FraudCondition that = (FraudCondition) o;

        if (!ObjectUtils.equals(this.getId(), that.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getLimit(), that.getLimit())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getPeriod(), that.getPeriod())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getType(), that.getType())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getLimit() != null ? getLimit().hashCode() : 0);
        result = 31 * result + (getPeriod() != null ? getPeriod().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.foros.model.admin.FraudCondition[fraudConditionId=" + this.getId() + "]";
    }
}
