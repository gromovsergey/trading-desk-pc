package com.foros.action.user;

import com.foros.model.security.User;

public class InternalUserStandaloneBreadcrumbsElement extends UserBreadcrumbsElement {
    public InternalUserStandaloneBreadcrumbsElement(User user) {
        super(user, "InternalUser.breadcrumbs", "InternalUser/view");
    }
}
