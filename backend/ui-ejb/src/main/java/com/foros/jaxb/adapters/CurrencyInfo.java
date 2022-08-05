package com.foros.jaxb.adapters;

import com.foros.model.currency.Currency;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "currencyId",
        "currencyCode",
        "fractionDigits"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CurrencyInfo {

    private long currencyId;
    private String currencyCode;
    private int fractionDigits;

    public CurrencyInfo() {
    }

    public CurrencyInfo(Currency currency) {
        this.currencyId = currency.getId();
        this.currencyCode = currency.getCurrencyCode();
        this.fractionDigits = currency.getFractionDigits();
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }
}
