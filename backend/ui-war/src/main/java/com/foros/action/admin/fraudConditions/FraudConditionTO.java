package com.foros.action.admin.fraudConditions;

import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.FraudConditionType;

import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import org.apache.commons.lang.ObjectUtils;
import java.sql.Timestamp;

@Conversion()
public class FraudConditionTO {
    public static final String UNITS_SECONDS = "second";
    public static final String UNITS_MINUTES = "minute";
    public static final String UNITS_HOURS = "hour";

    private static final Long HOURS = 60L * 60;
    private static final Long MINUTES = 60L;

    private Long id;
    private String type;
    private Long period;
    private Long limit;
    private String units = UNITS_SECONDS;
    private Timestamp version;

    public FraudConditionTO() {
    }

    public FraudConditionTO(FraudCondition fraudCondition) {
        this.id = fraudCondition.getId();
        this.limit = fraudCondition.getLimit();
        this.type = fraudCondition.getType().name();
        this.version = fraudCondition.getVersion();
        updatePeriodInUnitsBySeconds(fraudCondition.getPeriod());
    }

    private void updatePeriodInUnitsBySeconds(Long aPeriod) {
        if (aPeriod != null && aPeriod > 0) {
            if (periodUnitsAre(aPeriod, HOURS)) {
                setPeriodAndUnits(aPeriod / HOURS, UNITS_HOURS);
            } else {
                if (periodUnitsAre(aPeriod, MINUTES)) {
                    setPeriodAndUnits(aPeriod / MINUTES, UNITS_MINUTES);
                } else {
                    setPeriodAndUnits(aPeriod, UNITS_SECONDS);
                }
            }
        } else {
            setPeriodAndUnits(0L, UNITS_SECONDS);
        }
    }

    private void setPeriodAndUnits(Long aPeriod, String unitType) {
        period = aPeriod;
        units = unitType;
    }

    private boolean periodUnitsAre(Long aPeriod, Long units) {
        return aPeriod % units == 0;
    }

    public Long getPeriodInSeconds() {
        if (period == null) {
            return null;
        }

        if (UNITS_HOURS.equalsIgnoreCase(units)) {
            return period * HOURS;
        }
        if (UNITS_MINUTES.equalsIgnoreCase(units)) {
            return period * MINUTES;
        }
        if (UNITS_SECONDS.equalsIgnoreCase(units)) {
            return period;
        }

        throw new RuntimeException("Incorrect units value: " + units);
    }

    public FraudCondition toFraudCondition() {
        return toFraudCondition(new FraudCondition());
    }

    public FraudCondition toFraudCondition(FraudCondition fraudCondition) {
        if (getId() != null && getId() != 0) {
            fraudCondition.setId(getId());
        }

        fraudCondition.setLimit(getLimit());
        fraudCondition.setPeriod(getPeriodInSeconds());
        fraudCondition.setType(FraudConditionType.valueOf(getType()));
        fraudCondition.setVersion(getVersion());
        return fraudCondition;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public Long getPeriod() {
        return period;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public void setPeriod(Long period) {
        this.period = period;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public Long getLimit() {
        return limit;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FraudConditionTO)) {
            return false;
        }
        FraudConditionTO that = (FraudConditionTO) o;
        if (!ObjectUtils.equals(this.getId(), that.getId())) {
            return false;
        }
        return duplicates(that);
    }

    public boolean duplicates(FraudConditionTO that) {
        if (!ObjectUtils.equals(this.getLimit(), that.getLimit())) {
            return false;
        }
        if (!ObjectUtils.equals(this.getPeriod(), that.getPeriod())) {
            return false;
        }
        if (!ObjectUtils.equals(this.getType(), that.getType())) {
            return false;
        }
        if (!ObjectUtils.equals(this.getUnits(), that.getUnits())) {
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
        return "com.foros.action.admin.FraudConditionTO[fraudConditionId=" + this.getId() + "]";
    }
}
