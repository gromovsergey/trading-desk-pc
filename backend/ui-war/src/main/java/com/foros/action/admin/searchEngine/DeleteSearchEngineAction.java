package com.foros.action.admin.searchEngine;

import com.foros.action.BaseActionSupport;
import com.foros.session.admin.searchEngine.SearchEngineService;

import javax.ejb.EJB;

public class DeleteSearchEngineAction extends BaseActionSupport {
    @EJB
    private SearchEngineService searchEngineService;

    private Long id;

    public String delete() {
        searchEngineService.delete(id);
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
