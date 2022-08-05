package com.foros.action.user;

import com.foros.model.security.User;

public class ExternalUserBreadcrumbsElement extends ExternalUserBreadcrumbsElementBase {
    public ExternalUserBreadcrumbsElement(User user) {
        super(user, "account/user/");
    }
}
