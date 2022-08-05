package com.foros.action.admin.userRole;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.security.UserRole;

public class UserRoleBreadcrumbsElement extends EntityBreadcrumbsElement {
    public UserRoleBreadcrumbsElement(UserRole userRole) {
        super("UserRole.entityName", userRole.getId(), userRole.getName(), "UserRole/view");
    }
}
