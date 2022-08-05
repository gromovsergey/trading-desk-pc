package com.foros.action.admin.userRole;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;

public class EditUserRoleAction extends UserRoleActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="UserRole.create")
    public String create() {
        breadcrumbs = new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="UserRole.update")
    public String edit() {
        entity = service.findById(getModel().getId());
        breadcrumbs = new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement(entity)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
