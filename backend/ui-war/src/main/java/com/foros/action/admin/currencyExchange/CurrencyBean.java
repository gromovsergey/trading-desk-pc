package com.foros.action.admin.currencyExchange;

import com.foros.model.currency.Source;
import com.foros.session.NamedTO;

import java.math.BigDecimal;

public class CurrencyBean extends NamedTO {

    private String currencyCode;
    private String symbol;
    private BigDecimal rate;
    private int fractionDigits;
    private Source source;

    public CurrencyBean() {}

    public CurrencyBean(Long id, String name, String symbol) {
        super(id, name);
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
