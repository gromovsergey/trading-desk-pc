package com.foros.model.currency;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class CurrencyExchangeRatePK implements Serializable {

    @JoinColumn(name = "CURRENCY_EXCHANGE_ID", referencedColumnName = "CURRENCY_EXCHANGE_ID", updatable = false)
    @ManyToOne(optional = false)
    private CurrencyExchange currencyExchange;
    
    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID", updatable = false)
    @ManyToOne(optional = false)
    private Currency currency;

    public CurrencyExchangeRatePK() {
    }

    public CurrencyExchangeRatePK(Currency currency, CurrencyExchange currencyExchange) {
        this.currencyExchange = currencyExchange;
        this.currency = currency;
    }

    public CurrencyExchange getCurrencyExchange() {
        return currencyExchange;
    }

    public void setCurrencyExchange(CurrencyExchange currencyExchange) {
        this.currencyExchange = currencyExchange;
    }

    private Long getCurrencyExchangeId() {
        return currencyExchange == null ? null : currencyExchange.getId();
    }

    public long getCurrencyId() {
        if (currency == null || currency.getId() == null) {
            return -1;
        }
        return currency.getId();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyExchangeRatePK that = (CurrencyExchangeRatePK) o;

        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (currencyExchange != null ? !currencyExchange.equals(that.currencyExchange) : that.currencyExchange != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = currencyExchange != null ? currencyExchange.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRatePK[currencyId=" + getCurrencyId() + ", currencyExchangeId=" + getCurrencyExchangeId() + "]";
    }
}
