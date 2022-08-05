package com.foros.action.account;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.InternalAccount;

public class ViewInternalAccountAction extends ViewAccountActionBase<InternalAccount> implements BreadcrumbsSupport {
    private Long id;

    @ReadOnly
    public String view() {
        account = accountService.viewInternalAccount(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement()).add(new InternalAccountBreadcrumbsElement(account));
    }
}
