package com.foros.action.admin.searchEngine;

import com.foros.action.BaseActionSupport;
import com.foros.model.admin.SearchEngine;
import com.foros.session.admin.searchEngine.SearchEngineService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class ViewEditSearchEngineActionSupport extends BaseActionSupport implements ModelDriven<SearchEngine> {
    @EJB
    protected SearchEngineService searchEngineService;
    protected Long id;
    protected SearchEngine searchEngine;

    @Override
    public SearchEngine getModel() {
        return searchEngine;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
