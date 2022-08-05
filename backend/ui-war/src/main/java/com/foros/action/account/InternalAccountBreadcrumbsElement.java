package com.foros.action.account;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.account.Account;

public class InternalAccountBreadcrumbsElement extends EntityBreadcrumbsElement {
    public InternalAccountBreadcrumbsElement(Account account) {
        super("InternalAccount.breadcrumbs", account.getId(), account.getName(), "internal/account/view");
    }
}
