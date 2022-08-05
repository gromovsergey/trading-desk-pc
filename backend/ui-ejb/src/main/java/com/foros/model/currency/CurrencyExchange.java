package com.foros.model.currency;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.IdConstraint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "CURRENCYEXCHANGE")
@NamedQueries({
    @NamedQuery(name = "CurrencyExchange.findById", query = "SELECT c FROM CurrencyExchange c WHERE c.id = :id"),
    @NamedQuery(name = "CurrencyExchange.findLast", query = "SELECT c FROM CurrencyExchange c WHERE c.effectiveDate = (SELECT MAX(ce.effectiveDate) FROM CurrencyExchange ce WHERE ce.effectiveDate is not null)"),
    @NamedQuery(name = "CurrencyExchange.findByEffectiveDate", query = "SELECT c FROM CurrencyExchange c WHERE c.effectiveDate = :effectiveDate")
})
@XmlRootElement(name = "currencyExchange")
@XmlType(propOrder = {
        "id",
        "effectiveDate",
        "currencyExchangeRates"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CurrencyExchange extends EntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "CurrencyExchangeGen", sequenceName = "CURRENCYEXCHANGE_CURRENCY_EXCHANGE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CurrencyExchangeGen")
    @Column(name = "CURRENCY_EXCHANGE_ID", nullable = false)
    @IdConstraint
    private Long id;
    
    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private Timestamp effectiveDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id.currencyExchange")
    @ChangesInspection(type = InspectionType.NONE)
    private Set<CurrencyExchangeRate> currencyExchangeRates = new LinkedHashSet<>();

    public CurrencyExchange() {
    }

    public CurrencyExchange(Long id) {
        this.id = id;
    }

    public CurrencyExchange(Long id, Timestamp effectiveDate) {
        this.id = id;
        this.effectiveDate = effectiveDate;
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

    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Timestamp getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    @XmlElementWrapper
    @XmlElement(name = "currencyExchangeRate")
    public Set<CurrencyExchangeRate> getCurrencyExchangeRates() {
        return new ChangesSupportSet<>(this, "currencyExchangeRates", currencyExchangeRates);
    }

    public void setCurrencyExchangeRates(Set<CurrencyExchangeRate> currencyExchangeRateCollection) {
        this.currencyExchangeRates = currencyExchangeRateCollection;
        this.registerChange("currencyExchangeRates");
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
        if (!(object instanceof CurrencyExchange)) {
            return false;
        }
        
        CurrencyExchange other = (CurrencyExchange)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return CurrencyExchange.class.getCanonicalName() + "[currencyExchangeId=" + getId() + "]";
    }
}
