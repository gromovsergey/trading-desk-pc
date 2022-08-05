/*
 * CurrencyExchangeRate.java
 *
 * Created on March 16, 2007, 5:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foros.model.currency;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.CurrencyExchangeRateAuditSerializer;
import com.foros.jaxb.adapters.CurrencyXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "CURRENCYEXCHANGERATE")
@Audit(serializer = CurrencyExchangeRateAuditSerializer.class)
@XmlType(propOrder = {
        "currency",
        "rate"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CurrencyExchangeRate extends EntityBase implements Serializable {

    @EmbeddedId
    private CurrencyExchangeRatePK id;

    @RequiredConstraint
    @RangeConstraint(min = "0.00001", max = "9999999.99999")
    @Column(name = "RATE", nullable = false)
    private BigDecimal rate;

    @Column(name = "LAST_UPDATED_DATE", nullable = false)
    private Timestamp lastUpdated;

    public CurrencyExchangeRate() {
    }

    public CurrencyExchangeRate(CurrencyExchangeRatePK id) {
        this.id = id;
    }

    public CurrencyExchangeRate(Currency currency, CurrencyExchange exchange) {
        this.id = new CurrencyExchangeRatePK(currency, exchange);
    }

    /**
     * Copy rate to another CurrencyExchange
     * @param rate rate to copy
     * @param exchange CurrencyExchange to assign
     */
    public CurrencyExchangeRate(CurrencyExchangeRate rate, CurrencyExchange exchange) {
        this.id = new CurrencyExchangeRatePK(rate.getCurrency(), exchange);
        this.rate = rate.getRate();
        this.lastUpdated = rate.getLastUpdated();
    }

    @XmlTransient
    public CurrencyExchangeRatePK getId() {
        return this.id;
    }

    public void setId(CurrencyExchangeRatePK id) {
        this.id = id;
        this.registerChange("id");
    }

    public BigDecimal getRate() {
        return this.rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
        this.registerChange("rate");
    }

    @XmlJavaTypeAdapter(CurrencyXmlAdapter.class)
    public Currency getCurrency() {
        return getId() == null ? null : getId().getCurrency();
    }

    @XmlTransient
    public CurrencyExchange getCurrencyExchange() {
        return this.id == null ? null : this.id.getCurrencyExchange();
    }

    @XmlTransient
    public Timestamp getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
        this.registerChange("lastUpdated");
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
        if (!(object instanceof CurrencyExchangeRate)) {
            return false;
        }
        
        CurrencyExchangeRate other = (CurrencyExchangeRate)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return CurrencyExchangeRate.class.getCanonicalName() + "[id=" + getId() + "]";
    }
}
