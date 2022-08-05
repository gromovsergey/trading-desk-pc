package com.foros.model.account;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.security.DenyAll;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNTFINANCIALDATA")
public class AdvertisingFinancialData extends VersionEntityBase implements Serializable {

    @RequiredConstraint
    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @Column(name = "PREPAID_AMOUNT", nullable = false)
    private BigDecimal prepaidAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_PAID", updatable = false, insertable = false)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "INVOICED_OUTSTANDING", updatable = false, insertable = false)
    private BigDecimal invoicedOutstanding = BigDecimal.ZERO;

    @Column(name = "NOT_INVOICED", updatable = false, insertable = false)
    private BigDecimal notInvoiced = BigDecimal.ZERO;

    AdvertisingFinancialData() {
    }

    // Advertising

    public Long getAccountId() {
        return this.accountId;
    }

    void setAccountId(Long id) {
        this.accountId = id;
        this.registerChange("accountId");
    }

    public BigDecimal getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(BigDecimal prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
        this.registerChange("prepaidAmount");
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    @DenyAll
    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
        this.registerChange("totalPaid");
    }

    public BigDecimal getInvoicedOutstanding() {
        return invoicedOutstanding;
    }

    @DenyAll
    public void setInvoicedOutstanding(BigDecimal invoicedOutstanding) {
        this.invoicedOutstanding = invoicedOutstanding;
        this.registerChange("invoicedOutstanding");
    }

    public BigDecimal getNotInvoiced() {
        return notInvoiced;
    }

    @DenyAll
    public void setNotInvoiced(BigDecimal notInvoiced) {
        this.notInvoiced = notInvoiced;
        this.registerChange("notInvoiced");
    }

    @Override
    public String toString() {
        return "com.foros.model.account.AdvertisingFinancialData[id=" + getAccountId() + "]";
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AdvertisingFinancialData)) {
            return false;
        }

        AdvertisingFinancialData other = (AdvertisingFinancialData)object;
        if (this.getAccountId() != other.getAccountId() && (this.getAccountId() == null || !this.getAccountId().equals(other.getAccountId()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getAccountId() != null ? this.getAccountId().hashCode() : 0);
        return hash;
    }
}
