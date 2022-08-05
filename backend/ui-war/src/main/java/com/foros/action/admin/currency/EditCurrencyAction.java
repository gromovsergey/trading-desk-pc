package com.foros.action.admin.currency;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.currency.Currency;
import com.foros.model.currency.Source;
import com.foros.restriction.annotation.Restrict;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ModelDriven;

public class EditCurrencyAction extends CurrencyActionSupport implements ModelDriven<Currency>, BreadcrumbsSupport {
    
    private Currency currency = new Currency();
    
    @ReadOnly
    @Restrict(restriction="Currency.update", parameters="find('Currency',#target.model.id)")
    public String edit() throws IOException {
        currency = currencyService.view(currency.getId());
        return SUCCESS;
    }
    
    @ReadOnly
    @Restrict(restriction="Currency.create")
    public String create() throws IOException {        
        if (!hasCurrenciesForCreate()) {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        currency.setSource(Source.MANUAL);
        return SUCCESS;
    }

    @Override
    public Currency getModel() {
        return currency;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CurrenciesBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
