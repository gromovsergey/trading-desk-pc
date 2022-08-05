package com.foros.model;

import com.foros.annotations.Audit;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.audit.serialize.serializer.primitive.TimeSpanAuditSerializer;
import com.foros.jaxb.adapters.IntegerXmlAdapter;
import com.foros.model.time.TimeSpan;
import com.foros.validation.constraint.RangeConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "FREQCAP")
@NamedQueries({
    @NamedQuery(name = "FrequencyCap.findByLifeCount", query = "SELECT f FROM FrequencyCap f WHERE f.lifeCount = :lifeCount")
})
@XmlType(propOrder = {
        "id",
        "period",
        "windowLength",
        "windowCount",
        "lifeCount"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class FrequencyCap extends VersionEntityBase implements Serializable, Identifiable {
    @Column(name = "FREQ_CAP_ID", nullable = false)
    @SequenceGenerator(name = "FreqCapGen", sequenceName = "FREQCAP_FREQ_CAP_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FreqCapGen")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;
    
    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "PERIOD")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan period;
    
    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "WINDOW_LENGTH")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan windowLength;
    
    @Column(name = "WINDOW_COUNT")
    @RangeConstraint(min = "1", max = "999999")
    private Integer windowCount;
    
    @Column(name = "LIFE_COUNT")
    @RangeConstraint(min = "1", max = "999999")
    private Integer lifeCount;

    public FrequencyCap() {
    }

    public FrequencyCap(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlJavaTypeAdapter(IntegerXmlAdapter.class)
    public Integer getPeriod() {
        return TimeSpan.getValueInSecondsInt(getPeriodSpan());
    }

    public void setPeriod(Integer period) {
        if (period == null) {
            setPeriodSpan(null);
        } else {
            setPeriodSpan(TimeSpan.fromSeconds(period));
        }
    }

    @XmlTransient
    public TimeSpan getPeriodSpan() {
        return period;
    }

    public void setPeriodSpan(TimeSpan period) {
        this.period = period;
        this.registerChange("period");
    }

    @XmlJavaTypeAdapter(IntegerXmlAdapter.class)
    public Integer getWindowLength() {
        return TimeSpan.getValueInSecondsInt(getWindowLengthSpan());
    }

    public void setWindowLength(Integer windowLength) {
        setWindowLengthSpan(TimeSpan.fromSeconds(windowLength));
    }

    @XmlTransient
    public TimeSpan getWindowLengthSpan() {
        return windowLength;
    }

    public void setWindowLengthSpan(TimeSpan windowLength) {
        this.windowLength = windowLength;
        this.registerChange("windowLength");
    }

    @XmlJavaTypeAdapter(IntegerXmlAdapter.class)
    public Integer getWindowCount() {
        return this.windowCount;
    }

    public void setWindowCount(Integer windowCount) {
        this.windowCount = windowCount;
        this.registerChange("windowCount");
    }

    @XmlJavaTypeAdapter(IntegerXmlAdapter.class)
    public Integer getLifeCount() {
        return this.lifeCount;
    }

    public void setLifeCount(Integer lifeCount) {
        this.lifeCount = lifeCount;
        this.registerChange("lifeCount");
    }

    public boolean isEmpty() {
        return (getPeriod() == null && getWindowLength() == null && windowCount == null && lifeCount == null);
    }

    public boolean isZeroOrNull() {
        boolean isPeriodEmpty = getPeriod() == null || getPeriod() == 0;
        boolean isWindowLengthEmpty = getWindowLength() == null || getWindowLength() == 0;
        boolean isWindowCountEmpty = windowCount == null || windowCount == 0;
        boolean isLifeCountEmpty = lifeCount == null || lifeCount == 0;

        return isPeriodEmpty && isWindowLengthEmpty && isWindowCountEmpty && isLifeCountEmpty;
    }

    @Override
    public boolean equals(Object o) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrequencyCap that = (FrequencyCap) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (getId() != null ? getId().hashCode() : 0);
    }

    public void registerParametersChanges() {
        registerChange("period");
        registerChange("windowCount");
        registerChange("windowLength");
        registerChange("lifeCount");
    }
}
