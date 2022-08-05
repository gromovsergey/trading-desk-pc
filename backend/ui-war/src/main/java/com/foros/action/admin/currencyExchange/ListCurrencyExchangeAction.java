package com.foros.action.admin.currencyExchange;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;

public class ListCurrencyExchangeAction extends CurrencyExchangeActionSupport{

    @ReadOnly
    @Restrict(restriction="CurrencyExchange.view")
    public String list() {
        return SUCCESS;
    }

}
