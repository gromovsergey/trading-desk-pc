package com.foros.action.admin.searchEngine;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.admin.SearchEngine;
import com.foros.session.admin.searchEngine.SearchEngineService;

import java.util.List;
import javax.ejb.EJB;

public class ListSearchEngineAction extends BaseActionSupport {

    @EJB
    protected SearchEngineService searchEngineService;

    private List<SearchEngine> entities;

    @ReadOnly
    public String list() {
        //noinspection unchecked
        entities = searchEngineService.list();
        return SUCCESS;
    }

    public List<SearchEngine> getEntities() {
        return entities;
    }
}
