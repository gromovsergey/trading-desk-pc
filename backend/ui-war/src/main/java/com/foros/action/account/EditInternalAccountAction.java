package com.foros.action.account;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Country;
import com.foros.model.account.InternalAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.StringUtil;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

public class EditInternalAccountAction extends EditSaveInternalAccountActionBase implements RequestContextsAware {

    private Breadcrumbs breadcrumbs = new Breadcrumbs();

    public EditInternalAccountAction() {
        account = new InternalAccount();
    }

    @Override
    public InternalAccount getExistingAccount() {
        return account;
    }

    @ReadOnly
    public String changeCountry() {
        if (StringUtil.isPropertyNotEmpty(account.getCountry().getCountryCode())) {
            Country country = countryService.find(account.getCountry().getCountryCode());

            account.setCurrency(country.getCurrency());
            account.setTimezone(country.getTimezone());
        } else {
            account.setCurrency(currencyService.getDefault());
            account.setTimezone(null);
        }

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewInternalAccount(account.getId());
        breadcrumbs.add(new InternalAccountsBreadcrumbsElement()).add(new InternalAccountBreadcrumbsElement(account));
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'Internal'")
    public String create() {
        account = new InternalAccount();
        account.setCurrency(currencyService.getDefault());
        breadcrumbs.add(new InternalAccountsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        ContextBase context = contexts.getContext(account.getRole());

        if (context != null) {
            context.switchTo(account.getId());
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
