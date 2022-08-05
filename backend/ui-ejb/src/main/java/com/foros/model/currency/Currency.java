package com.foros.model.currency;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.CurrencyAuditSerializer;
import com.foros.audit.serialize.serializer.primitive.SourceAuditSerializer;
import com.foros.jaxb.adapters.TimestampXmlAdapter;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "CURRENCY")
@NamedQueries({
  @NamedQuery(name = "Currency.findAll", query = "SELECT c FROM Currency c ORDER BY c.currencyCode"),
  @NamedQuery(name = "Currency.findUpdatable", query = "SELECT c FROM Currency c where c.source=:source ORDER BY c.currencyCode"),
  @NamedQuery(name = "Currency.findById", query = "SELECT c FROM Currency c WHERE c.id = :id"),
  @NamedQuery(name = "Currency.getByCode", query = "SELECT c FROM Currency c WHERE c.currencyCode = :code")
})
@Audit(serializer = CurrencyAuditSerializer.class)
public class Currency extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "CurrencyGen", sequenceName = "CURRENCY_CURRENCY_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CurrencyGen")
    @Column(name = "CURRENCY_ID", nullable = false)
    @IdConstraint
    private Long id;
    
    @RequiredConstraint
    @PatternConstraint(regexp = "[A-Z]{3}", message="Currency.invalid.currencyCode")
    @Column(name = "CURRENCY_CODE", nullable = false, length = 3)
    private String currencyCode;

    @RequiredConstraint
    @Column(name = "FRACTION_DIGITS", nullable = false)
    private int fractionDigits;
    
    @RangeConstraint(min = "0.00001", max = "9999999.99999")
    @Transient
    private BigDecimal rate;

    @Transient
    private Timestamp effectiveDate;

    @Transient
    private Timestamp lastUpdated;

    @Audit(serializer = SourceAuditSerializer.class)
    @Column(name = "SOURCE", nullable = false)
    private char source = 'M';

    public Currency() {
    }

    public Currency(Long id) {
        this.id = id;
    }

    public Currency(Long id, String currencyCode, int fractionDigits) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.fractionDigits = fractionDigits;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
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
        if (!(object instanceof Currency)) {
            return false;
        }
        
        Currency other = (Currency)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        
        return true;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        this.registerChange("currencyCode");
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
        this.registerChange("rate");
    }

    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    public Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
        this.registerChange("lastUpdated");
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
        this.registerChange("fractionDigits");
    }

    public Source getSource() {
        return Source.valueOf(source);
    }

    public void setSource(Source source) {
        this.source = source.getLetter();
        this.registerChange("source");
    }

    @Override
    public String toString() {
        return "com.foros.model.currency.Currency[id=" + getId() + "]";
    }
}
