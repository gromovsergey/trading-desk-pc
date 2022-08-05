package com.foros.model.currency;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.currency.CurrencyExchangeAuditWrapperChange;
import com.foros.changes.inspection.changeNode.custom.SortedCurrencyExchangeRatesChange;

import java.util.LinkedHashSet;
import java.util.Set;

@Auditable
@Audit(nodeFactory = CurrencyExchangeAuditWrapperChange.Factory.class)
public class CurrencyExchangeAuditWrapper {

    private Source source;

    @ChangesInspection(type = InspectionType.CASCADE)
    private CurrencyExchange exchange;

    @ChangesInspection(type = InspectionType.CASCADE)
    @Audit(nodeFactory = SortedCurrencyExchangeRatesChange.Factory.class)
    private Set<CurrencyExchangeRateAuditWrapper> rates = new LinkedHashSet<CurrencyExchangeRateAuditWrapper>();

    public CurrencyExchangeAuditWrapper(CurrencyExchange exchange, Source source) {
        this.exchange = exchange;
        this.source = source;
    }

    public CurrencyExchange getExchange() {
        return exchange;
    }

    public void setExchange(CurrencyExchange exchange) {
        this.exchange = exchange;
    }

    public Set<CurrencyExchangeRateAuditWrapper> getRates() {
        return rates;
    }

    public void setRates(Set<CurrencyExchangeRateAuditWrapper> rates) {
        this.rates = rates;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
