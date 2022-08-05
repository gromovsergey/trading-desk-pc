package com.foros.action.admin.currencyExchange;

import com.foros.framework.ReadOnly;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.Source;
import com.foros.restriction.annotation.Restrict;

import java.util.List;

public class EditCurrencyExchangeAction extends EditCurrencyExchangeActionSupport {

    @ReadOnly
    @Restrict(restriction="CurrencyExchange.update")
    public String edit() {
        List<CurrencyBean> tos = getCurrencyExchangeRates();
        manualRates.clear();
        for (CurrencyBean to : tos) {
            if (to.getSource() == Source.MANUAL) {
                CurrencyExchangeRate r = new CurrencyExchangeRate(new Currency(to.getId()), null);
                r.setRate(to.getRate());
                manualRates.add(r);
            }
        }
        previousEffectiveDate = getExchange().getEffectiveDate();
        return SUCCESS;
    }
}
