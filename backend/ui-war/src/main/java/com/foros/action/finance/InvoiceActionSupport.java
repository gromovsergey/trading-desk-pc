package com.foros.action.finance;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.finance.Invoice;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public abstract class InvoiceActionSupport extends BaseActionSupport implements ModelDriven<Invoice>, RequestContextsAware {
    @EJB
    protected AdvertisingFinanceService financeService;

    protected Invoice invoice;

    public abstract Invoice getExistingInvoice();

    @Override
    public Invoice getModel() {
        return invoice;
    }

    public Invoice getEntity() {
        return getModel();
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        AdvertiserAccount account = getExistingInvoice().getAccount();

        ContextBase context = contexts.getContext(account.getRole());

        if (context != null) {
            context.switchTo(account.getId());
        }
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return getExistingInvoice().getAccount().isInAgencyAdvertiser();
    }
}
