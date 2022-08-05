package com.foros.action.admin.globalParams;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;


public class SaveGlobalParamAction extends GlobalParamActionSupport implements BreadcrumbsSupport {

    public String save() throws Exception {
        paramsService.updateExchangeRateUpdate(getModel().getExchangeRateUpdate());
        paramsService.updateWDTagMapping(getModel().getWdTagMapping());
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new GlobalParamsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
