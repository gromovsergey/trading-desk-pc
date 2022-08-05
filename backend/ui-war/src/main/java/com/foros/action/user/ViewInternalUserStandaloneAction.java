package com.foros.action.user;

import com.foros.breadcrumbs.Breadcrumbs;

public class ViewInternalUserStandaloneAction extends ViewInternalUserAction {
    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new InternalUsersBreadcrumbsElement())
                .add(new InternalUserStandaloneBreadcrumbsElement(user));
    }
}
