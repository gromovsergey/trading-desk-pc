package com.foros.model.site;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.util.NumberUtil;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * Entity class SiteRate
 *
 * @author Vladimir Nenashev
 * @version $Revision: 1.16 $
 */
@Entity
@Table(name = "SITERATE")
@NamedQueries(
      {
    @NamedQuery(name = "SiteRate.findBySiteRateId", query = "SELECT s FROM SiteRate s WHERE s.id = :id"),
    @NamedQuery(name = "SiteRate.findByEffectiveDate", query = "SELECT s FROM SiteRate s WHERE s.effectiveDate = :effectiveDate"),
    @NamedQuery(name = "SiteRate.findByCpm", query = "SELECT s FROM SiteRate s WHERE s.rate = :cpm")
  })
public class SiteRate extends EntityBase implements Serializable, Identifiable {
    @GenericGenerator(name = "SiteRateGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "SITERATE_SITE_RATE_ID_SEQ") })
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SiteRateGen")
    @Column(name = "SITE_RATE_ID", nullable = false)
    private Long id;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;

    @Column(name = "RATE", nullable = false)
    private BigDecimal rate;

    @RequiredConstraint
    @Column(name = "RATE_TYPE", nullable = false, updatable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.site.SiteRateType")
    })
    private SiteRateType rateType;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "TAG_PRICING_ID", referencedColumnName = "TAG_PRICING_ID")
    @ManyToOne
    private TagPricing tagPricing;

    /**
     * Creates a new instance of SiteRate
     */
    public SiteRate() {
    }

    /**
     * Creates a new instance of SiteRate with the specified values.
     * @param id the siteRateId of the SiteRate
     */
    public SiteRate(Long id) {
        this.id = id;
    }

    /**
     * Creates a new instance of SiteRate with the specified values.
     * @param id the siteRateId of the SiteRate
     * @param effectiveDate the effectiveDate of the SiteRate
     */
    public SiteRate(Long id, Date effectiveDate) {
        this.id = id;
        this.effectiveDate = effectiveDate;
    }

    /**
     * Gets the siteRateId of this SiteRate.
     * @return the siteRateId
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the siteRateId of this SiteRate to the specified value.
     *
     * @param id the new siteRateId
     */
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * Gets the effectiveDate of this SiteRate.
     * @return the effectiveDate
     */
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    /**
     * Sets the effectiveDate of this SiteRate to the specified value.
     *
     * @param effectiveDate the new effectiveDate
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this SiteRate.  The result is
     * <code>true</code> if and only if the argument is not null and is a SiteRate object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiteRate)) {
            return false;
        }
        SiteRate other = (SiteRate)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.SiteRate[id=" + getId() + "]";
    }

    public boolean equalsRate(SiteRate siteRate) {
        if (rate.compareTo(siteRate.rate) == 0 && rateType == siteRate.rateType) {
            return true;
        }
        return false;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
        this.registerChange("rate");
    }

    public BigDecimal getRatePercent() {
        return NumberUtil.toPercents(getRate());
    }

    public void setRatePercent(BigDecimal commission) {
        setRate(NumberUtil.fromPercents(commission));
        this.registerChange("ratePercent");
    }

    public SiteRateType getRateType() {
        return rateType;
    }

    public void setRateType(SiteRateType rateType) {
        this.rateType = rateType;
        this.registerChange("rateType");
    }

    public TagPricing getTagPricing() {
        return tagPricing;
    }

    public void setTagPricing(TagPricing tagPricing) {
        this.tagPricing = tagPricing;
        this.registerChange("tagPricing");
    }
}
