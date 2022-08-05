package com.foros.action.user;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.security.User;

public abstract class UserBreadcrumbsElement extends EntityBreadcrumbsElement {
    protected UserBreadcrumbsElement(User user, String entityTypeName, String path) {
        super(entityTypeName,
                user.getId(),
                new StringBuilder().append(user.getFirstName()).append(" ").append(user.getLastName()).toString(),
                path);
    }
}
