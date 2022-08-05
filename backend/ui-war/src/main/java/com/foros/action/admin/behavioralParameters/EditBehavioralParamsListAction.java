package com.foros.action.admin.behavioralParameters;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;

public class EditBehavioralParamsListAction extends BehavioralParamsListActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String edit() {
        list = behavioralParamsListService.view(list.getId());
        sortBehavioralParameters(list.getBehavioralParameters());
        breadcrumbs = new Breadcrumbs().add(new BehavioralParametersBreadcrumbsElement()).add(new BehavioralParameterBreadcrumbsElement(list)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @ReadOnly
    public String create() {
        list.setThreshold(1L);
        breadcrumbs = new Breadcrumbs().add(new BehavioralParametersBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
