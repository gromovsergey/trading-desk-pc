package com.foros.action.admin.accountType;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.security.AccountType;

public class AccountTypeBreadcrumbsElement extends EntityBreadcrumbsElement {
    public AccountTypeBreadcrumbsElement(AccountType accountType) {
        super("AccountType.entityName", accountType.getId(), accountType.getName(), "AccountType/view");
    }
}
