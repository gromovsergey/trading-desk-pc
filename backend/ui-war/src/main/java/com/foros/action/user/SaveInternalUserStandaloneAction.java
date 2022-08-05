package com.foros.action.user;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.security.User;

public class SaveInternalUserStandaloneAction extends SaveUserAction {
    @Override
    Breadcrumbs buildInternalUserBreadcrumbs(User entity) {
        return new Breadcrumbs()
                .add(new InternalUsersBreadcrumbsElement())
                .add(new InternalUserStandaloneBreadcrumbsElement(entity))
                .add(ActionBreadcrumbs.EDIT);
    }
}
