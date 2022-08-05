package com.foros.action.user;

import com.foros.model.security.User;

public class InternalUserBreadcrumbsElement extends UserBreadcrumbsElement {
    public InternalUserBreadcrumbsElement(User user) {
        super(user, "InternalUser.breadcrumbs", "internal/account/user/view");
    }
}
