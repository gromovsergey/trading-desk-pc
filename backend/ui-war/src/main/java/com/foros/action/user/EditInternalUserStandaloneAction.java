package com.foros.action.user;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.security.User;

public class EditInternalUserStandaloneAction extends EditUserAction {
    @Override
    Breadcrumbs buildInternalUserEditBreadcrumbs(User entity) {
        return new Breadcrumbs()
                .add(new InternalUsersBreadcrumbsElement())
                .add(new InternalUserStandaloneBreadcrumbsElement(entity));
    }
}
