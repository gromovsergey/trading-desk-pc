package com.foros.action.admin.behavioralParameters;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;

public class ViewBehavioralParamsListAction extends BehavioralParamsListActionSupport implements BreadcrumbsSupport {

    private int usageCount;

    @ReadOnly
    public String view() {
        list = behavioralParamsListService.view(list.getId());
        usageCount = behavioralParamsListService.getChannelUsageCount(list.getId());
        sortBehavioralParameters(list.getBehavioralParameters());
        return SUCCESS;
    }

    @ReadOnly
    public String get() {
        if (list.getId() != null) {
            list = behavioralParamsListService.findWithNoErrors(list.getId());

            if (list != null) {
                sortBehavioralParameters(list.getBehavioralParameters());
            }
        }

        return SUCCESS;
    }

    public int getUsageCount() {
        return usageCount;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new BehavioralParametersBreadcrumbsElement()).add(new BehavioralParameterBreadcrumbsElement(list));
    }
}
