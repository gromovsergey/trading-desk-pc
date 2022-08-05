package com.foros.model.finance;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

import com.foros.util.HashUtil;

public class InvoicingPeriod implements Serializable {
    private Date invoicingPeriodStartDate;

    private Date invoicingPeriodEndDate;
    
    public InvoicingPeriod(Date invoicingPeriodStartDate, Date invoicingPeriodEndDate) {
        this.invoicingPeriodStartDate = invoicingPeriodStartDate;
        this.invoicingPeriodEndDate = invoicingPeriodEndDate;
    }
    
    public Date getInvoicingPeriodStartDate() {
        return invoicingPeriodStartDate;
    }

    public Date getInvoicingPeriodEndDate() {
        return invoicingPeriodEndDate;
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(getInvoicingPeriodStartDate(), getInvoicingPeriodEndDate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvoicingPeriod)) {
            return false;
        }

        InvoicingPeriod other = (InvoicingPeriod) obj;
        if (!ObjectUtils.equals(this.getInvoicingPeriodStartDate(), other.getInvoicingPeriodStartDate())) {
            return false;
        }
        if (!ObjectUtils.equals(this.getInvoicingPeriodEndDate(), other.getInvoicingPeriodEndDate())) {
            return false;
        }

        return true;
    }
}
