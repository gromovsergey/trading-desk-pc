package com.foros.action.admin.wdFrequencyCaps;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.session.admin.globalParams.GlobalParamsService;

public class ViewWDFrequencyCapsAction extends WDFrequencyCapsActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String view() {
        return process();
    }

    @ReadOnly
    public String edit() {
        breadcrumbs = new Breadcrumbs().add(new WDFrequencyCapsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
        return process();
    }

    public String process() {
        setEventsFrequencyCap(globalParamsService.getWDFrequencyCap(GlobalParamsService.WD_FREQ_CAP_EVENT));
        setChannelsFrequencyCap(globalParamsService.getWDFrequencyCap(GlobalParamsService.WD_FREQ_CAP_CHANNEL));
        setCategoriesFrequencyCap(globalParamsService.getWDFrequencyCap(GlobalParamsService.WD_FREQ_CAP_CATEGORY));
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
