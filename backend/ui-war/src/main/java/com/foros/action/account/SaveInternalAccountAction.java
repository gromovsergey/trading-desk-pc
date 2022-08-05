package com.foros.action.account;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.account.InternalAccount;

public class SaveInternalAccountAction extends EditSaveInternalAccountActionBase {
    private InternalAccount existingAccount;

    public SaveInternalAccountAction() {
        account = new InternalAccount();
    }

    public String update() {
        accountService.updateInternalAccount(account);

        return SUCCESS;
    }

    public String create() {
        accountService.createInternalAccount(account);

        return SUCCESS;
    }

    @Override
    public InternalAccount getExistingAccount() {
        if (account.getId() == null) {
            return account;
        }

        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = (InternalAccount) accountService.find(account.getId());

        return existingAccount;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement());
        if (account.getId() != null) {
            breadcrumbs.add(new InternalAccountBreadcrumbsElement(getExistingAccount()));
        } else {
            breadcrumbs.add(ActionBreadcrumbs.CREATE);
        }
        return breadcrumbs;
    }
}
