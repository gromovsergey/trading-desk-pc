package com.foros.model.isp;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.BigDecimalXmlAdapter;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.RangeConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.security.DenyAll;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "COLOCATIONRATE")
@NamedQueries({
    @NamedQuery(name = "ColocationRate.findByColoRateId", query = "SELECT c FROM ColocationRate c WHERE c.id = :id"),
    @NamedQuery(name = "ColocationRate.findByEffectiveDate", query = "SELECT c FROM ColocationRate c WHERE c.effectiveDate = :effectiveDate"),
    @NamedQuery(name = "ColocationRate.findByRevenueShare", query = "SELECT c FROM ColocationRate c WHERE c.revenueShare = :revenueShare")
})
@XmlType(propOrder = {
        "effectiveDate",
        "revenueShare"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ColocationRate extends EntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "ColocationRateGen", sequenceName = "COLOCATIONRATE_COLO_RATE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ColocationRateGen")
    @Column(name = "COLO_RATE_ID", nullable = false)
    private Long id;
    
    @Column(name = "EFFECTIVE_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;
    
    @Column(name = "REVENUE_SHARE", updatable = false)
    private BigDecimal revenueShare;
    
    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "COLO_ID", referencedColumnName = "COLO_ID", updatable = false)
    @ManyToOne
    private Colocation colocation;

    public ColocationRate() {
    }

    public ColocationRate(Long id) {
        this.id = id;
    }

    public ColocationRate(Long id, Date effectiveDate) {
        this.id = id;
        this.effectiveDate = effectiveDate;
    }

    public Long getId() {
        return this.id;
    }

    @DenyAll
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    @XmlElement
    public BigDecimal getRevenueShare() {
        return this.revenueShare;
    }

    public void setRevenueShare(BigDecimal revenueShare) {
        this.revenueShare = revenueShare;
        this.registerChange("revenueShare");
    }

    @XmlTransient
    public Colocation getColocation() {
        return this.colocation;
    }

    public void setColocation(Colocation colocation) {
        this.colocation = colocation;
        this.registerChange("colocation");
    }

    @XmlTransient
    @Transient
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getRevenueShareInPercent() {
        if (getRevenueShare() == null) {
            return null;
        }
        return getRevenueShare().multiply(new BigDecimal(100)).stripTrailingZeros();
    }

    public void setRevenueShareInPercent(BigDecimal revenueShare) {
        if (revenueShare != null) {
            revenueShare = revenueShare.divide(new BigDecimal(100));
        }
        setRevenueShare(revenueShare);
        this.registerChange("revenueShareInPercent");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ColocationRate)) {
            return false;
        }
        ColocationRate other = (ColocationRate)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.ColocationRate[id=" + getId() + "]";
    }
}
