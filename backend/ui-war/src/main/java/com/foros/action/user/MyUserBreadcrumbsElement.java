package com.foros.action.user;

import com.foros.model.security.User;

public class MyUserBreadcrumbsElement extends ExternalUserBreadcrumbsElementBase {
    protected MyUserBreadcrumbsElement(User user) {
        super(user, "myAccount/myUser/");
    }
}
