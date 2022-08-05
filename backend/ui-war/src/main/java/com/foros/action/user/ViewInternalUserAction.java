package com.foros.action.user;

import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.session.admin.userRole.UserRoleService;

import java.math.BigDecimal;
import javax.ejb.EJB;

public class ViewInternalUserAction extends ViewUserAction implements BreadcrumbsSupport {
    @EJB
    private UserRoleService userRoleService;

    public boolean isAdvertisingFinanceUser() {
        return userRoleService.isAdvertisingFinanceUser(user.getRole().getId());
    }

    public boolean isLimitedBudget() {
        return user.getMaxCreditLimit().compareTo(BigDecimal.ZERO) == 1;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {

        return new Breadcrumbs()
                .add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(user.getAccount()))
                .add(new InternalUserBreadcrumbsElement(user));
    }

}
