package com.foros.action.admin.currency;

import com.foros.framework.ReadOnly;
import com.foros.model.currency.Currency;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListCurrencyAction extends CurrencyActionSupport {

    // model
    private List<Currency> currencies;
       
    @ReadOnly
    public String list() {
        currencies = currencyService.findAll();
        Collections.sort(currencies, new Comparator<Currency>(){
            @Override
            public int compare(Currency o1, Currency o2) {
                return getCurrencyName(o1.getCurrencyCode()).compareTo(getCurrencyName(o2.getCurrencyCode()));
            }
            
        });
        return SUCCESS;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }
}
