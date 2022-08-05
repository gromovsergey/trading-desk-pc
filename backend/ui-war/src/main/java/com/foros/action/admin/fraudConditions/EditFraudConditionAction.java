package com.foros.action.admin.fraudConditions;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;

public class EditFraudConditionAction extends FraudConditionActionSupport implements BreadcrumbsSupport {
    @ReadOnly
    public String edit() {
        return doViewEdit();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new FraudConditionBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
