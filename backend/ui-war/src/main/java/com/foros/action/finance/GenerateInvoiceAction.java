package com.foros.action.finance;

import com.foros.action.BaseActionSupport;
import com.foros.session.finance.AdvertisingFinanceService;

import javax.ejb.EJB;

public class GenerateInvoiceAction extends BaseActionSupport {
    @EJB
    private AdvertisingFinanceService financeService;

    private Long id;

    public String generate() {
        financeService.generateInvoice(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
