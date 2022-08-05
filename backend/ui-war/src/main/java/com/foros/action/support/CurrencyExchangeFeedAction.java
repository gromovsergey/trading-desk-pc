package com.foros.action.support;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.currencyExchange.CurrencyExchangeImporterTimedBean;

import javax.ejb.EJB;

public class CurrencyExchangeFeedAction extends BaseActionSupport {
    @EJB
    private CurrencyExchangeImporterTimedBean currencyExchangeImporterTimedBean;

    @ReadOnly
    @Restrict(restriction = "CurrencyExchange.update")
    public String update() throws Exception {
        currencyExchangeImporterTimedBean.proceed();
        return SUCCESS;
    }
}
