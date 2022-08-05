package com.foros.model.currency;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.currency.CurrencyExchangeRateAuditWrapperChange;

@Auditable
@Audit(nodeFactory = CurrencyExchangeRateAuditWrapperChange.Factory.class)
public class CurrencyExchangeRateAuditWrapper {

    @ChangesInspection(type = InspectionType.CASCADE)
    private CurrencyExchangeRate rate;

    private boolean updated;

    public CurrencyExchangeRateAuditWrapper(CurrencyExchangeRate rate) {
        this.rate = rate;
    }

    public CurrencyExchangeRate getRate() {
        return rate;
    }

    public void setRate(CurrencyExchangeRate rate) {
        this.rate = rate;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
