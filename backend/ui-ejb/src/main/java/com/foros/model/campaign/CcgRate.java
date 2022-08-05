package com.foros.model.campaign;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.security.DenyAll;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "CCGRATE")
@NamedQueries({
  @NamedQuery(name = "CcgRate.findByCcgRateId", query = "SELECT c FROM CcgRate c WHERE c.id = :id"),
  @NamedQuery(name = "CcgRate.findByEffectiveDate", query = "SELECT c FROM CcgRate c WHERE c.effectiveDate = :effectiveDate"),
  @NamedQuery(name = "CcgRate.findByCpm", query = "SELECT c FROM CcgRate c WHERE c.cpm = :cpm"),
  @NamedQuery(name = "CcgRate.findByCpc", query = "SELECT c FROM CcgRate c WHERE c.cpc = :cpc"),
  @NamedQuery(name = "CcgRate.findByCpa", query = "SELECT c FROM CcgRate c WHERE c.cpa = :cpa")
})
public class CcgRate extends EntityBase implements Serializable, Identifiable {
    @GenericGenerator(name = "CcgRateGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "CCGRATE_CCG_RATE_ID_SEQ")})
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CcgRateGen")
    @Column(name = "CCG_RATE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;

    @Column(name = "CPM")
    private BigDecimal cpm = BigDecimal.ZERO;

    @Column(name = "CPC")
    private BigDecimal cpc = BigDecimal.ZERO;

    @Column(name = "CPA")
    private BigDecimal cpa = BigDecimal.ZERO;

    @Column(name = "RATE_TYPE")
    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID", updatable = false)
    @ManyToOne()
    private CampaignCreativeGroup ccg;

    public CcgRate() {
    }

    public CcgRate(Long id) {
        this.id = id;
    }

    public CcgRate(Long id, Date effectiveDate) {
        this.id = id;
        this.effectiveDate = effectiveDate;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @DenyAll
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    public BigDecimal getCpm() {
        return this.cpm;
    }

    public void setCpm(BigDecimal cpm) {
        this.cpm = cpm;
        this.registerChange("cpm");
    }

    public BigDecimal getCpc() {
        return this.cpc;
    }

    public void setCpc(BigDecimal cpc) {
        this.cpc = cpc;
        this.registerChange("cpc");
    }

    public BigDecimal getCpa() {
        return this.cpa;
    }

    public void setCpa(BigDecimal cpa) {
        this.cpa = cpa;
        this.registerChange("cpa");
    }

    public CampaignCreativeGroup getCcg() {
        return this.ccg;
    }

    @XmlTransient
    public void setCcg(CampaignCreativeGroup ccg) {
        this.ccg = ccg;
        this.registerChange("ccg");
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
        this.registerChange("rateType");
    }

    public BigDecimal getRate() {
        switch (rateType) {
            case CPC:
                return cpc;
            case CPM:
                return cpm;
            case CPA:
                return cpa;
            default:
                throw new IllegalStateException("Invalid rate type: " + rateType);
        }
    }

    public void setRate(BigDecimal value, RateType rateType) {
        setRateType(rateType);
        switch (rateType) {
            case CPC:
                setCpc(value);
                break;
            case CPM:
                setCpm(value);
                break;
            case CPA:
                setCpa(value);
                break;
            default:
                throw new IllegalStateException("Invalid rate type: " + rateType);
        }
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
     * Determines whether another object is equal to this CcgRate.  The result is
     * <code>true</code> if and only if the argument is not null and is a CcgRate object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CcgRate)) {
            return false;
        }

        CcgRate other = (CcgRate)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    public BigDecimal getValue() {
        if (rateType == null) {
            return null;
        }
        switch (rateType) {
            case CPM:
                return cpm;
            case CPC:
                return cpc;
            case CPA:
                return cpa;
            default:
                throw new RuntimeException();
        }
    }

    public boolean compareFields(CcgRate other) {
        if (other == null) {
            return false;
        }

        if (this.getRateType() != other.getRateType()) {
            return false;
        }

        BigDecimal thisValue = this.getValue();
        BigDecimal otherValue = other.getValue();

        if ((thisValue == null && otherValue != null) ||
                (thisValue != null && otherValue == null) ||
                (thisValue != null && otherValue != null && thisValue.compareTo(otherValue) != 0)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.campaign.CcgRate[id=" + getId() + "]";
    }
}
