package com.foros.action.admin.currencyExchange;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.currency.CurrencyExchangeRate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EditCurrencyExchangeActionSupport extends CurrencyExchangeActionSupport implements BreadcrumbsSupport {

    protected List<CurrencyExchangeRate> manualRates = new ArrayList<>();
    protected Timestamp previousEffectiveDate;

    public List<CurrencyExchangeRate> getManualRates() {
        return manualRates;
    }

    public Timestamp getPreviousEffectiveDate() {
        return previousEffectiveDate;
    }

    public void setPreviousEffectiveDate(Timestamp previousEffectiveDate) {
        this.previousEffectiveDate = previousEffectiveDate;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CurrencyExchangesBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }

    public Integer findIndex(Long currencyId) {
        for (int i = 0; i < manualRates.size(); i++) {
            CurrencyExchangeRate rate = manualRates.get(i);
            if (currencyId.equals(rate.getCurrency().getId())) {
                return i;
            }
        }
        return null;
    }
}
