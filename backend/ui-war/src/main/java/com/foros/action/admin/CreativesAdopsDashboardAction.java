package com.foros.action.admin;

import com.foros.action.BaseActionSupport;
import com.foros.action.SearchForm;
import com.foros.framework.ReadOnly;
import com.foros.session.creative.CreativeService;
import com.foros.session.creative.CreativeTO;
import com.foros.util.jpa.DetachedList;
import com.opensymphony.xwork2.ModelDriven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

public class CreativesAdopsDashboardAction extends BaseActionSupport implements ModelDriven<SearchForm> {
    @EJB
    private CreativeService creativeService;

    private List<CreativeTO> creatives;

    private String lookup;

    private SearchForm searchParams = new SearchForm();

    @ReadOnly
    public String lookup() throws Exception {
        return lookup;
    }

    @ReadOnly
    public String showPendingCreatives() {
        return SUCCESS;
    }

    @ReadOnly
    public String searchPendingCreatives() {
        int creativeCount = creativeService.findPendingFOROSCreativesCount();
        if (creativeCount == 0) {
            creatives = new DetachedList<>(new ArrayList<CreativeTO>(), 0);
        } else {
            creatives = creativeService.findPendingFOROSCreatives(searchParams.getFirstResultCount(), searchParams.getPageSize());
        }
        searchParams.setTotal((long)creativeCount);
        return SUCCESS;
    }

    public Collection<CreativeTO> getCreatives() {
        return creatives;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public SearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchForm searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public SearchForm getModel() {
        return searchParams;
    }
}
