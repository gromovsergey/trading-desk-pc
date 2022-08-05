package com.foros.action.admin.searchEngine;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.SearchEngine;
import com.foros.restriction.annotation.Restrict;

public class EditSearchEngineAction extends ViewEditSearchEngineActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction="SearchEngine.create")
    public String create(){
        searchEngine = new SearchEngine();
        breadcrumbs = new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="SearchEngine.update")
    public String edit(){
        searchEngine = searchEngineService.findById(id);
        breadcrumbs = new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(new SearchEngineBreadcrumbsElement(searchEngine)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

