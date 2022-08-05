package com.foros.jaxb.adapters;

import com.foros.model.campaign.RateType;

import java.math.BigDecimal;
import java.util.Date;

public class XMLRate {
    
    private BigDecimal value;
    private RateType rateType;
    private Date effectiveDate;
    
    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    public RateType getRateType() {
        return rateType;
    }
    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
