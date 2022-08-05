package com.foros.action.admin.searchEngine;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;

public class ViewSearchEngineAction extends ViewEditSearchEngineActionSupport implements BreadcrumbsSupport {

    @ReadOnly
    @Restrict(restriction = "SearchEngine.view")
    public String view() {
        searchEngine = searchEngineService.findById(id);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(new SearchEngineBreadcrumbsElement(searchEngine));
    }
}
