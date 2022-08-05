package com.foros.action.admin.country.placementsBlacklist;

import com.foros.action.BaseActionSupport;
import com.foros.action.admin.country.CountriesBreadcrumbsElement;
import com.foros.action.admin.country.CountryBreadcrumbsElement;
import com.foros.action.SearchForm;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Country;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.service.PlacementsBlacklistService;
import com.foros.util.jpa.DetachedList;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;
import java.util.List;


public class ViewPlacementsBlacklistAction extends BaseActionSupport implements BreadcrumbsSupport, ModelDriven<SearchForm> {

    private PlacementsBlacklistSearchForm searchParams = new PlacementsBlacklistSearchForm();
    private DetachedList<PlacementBlacklist> placementsBlacklist;
    private Country country;
    private Breadcrumbs breadcrumbs;
    private boolean submitSearchNeeded;

    @EJB
    CountryService countryService;

    @EJB
    PlacementsBlacklistService placementsBlacklistService;

    @ReadOnly
    @Restrict(restriction = "PlacementsBlacklist.view")
    public String view() {
        return SUCCESS;
    }

    @ReadOnly
    public String search() {
        submitSearchNeeded = false;
        placementsBlacklist = placementsBlacklistService.getPlacementsBlacklist(
                searchParams.getUrl(),
                getCountry(),
                searchParams.getFirstResultCount(),
                searchParams.getPageSize()
        );
        searchParams.setTotal((long)placementsBlacklist.getTotal());
        return SUCCESS;
    }

    public String getId() {
        return searchParams.getCountryCode();
    }

    public void setId(String countryCode) {
        searchParams.setCountryCode(countryCode);
    }

    public Country getCountry() {
        if (country == null) {
            country = countryService.find(getId());
        }
        return country;
    }

    public PlacementsBlacklistSearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(PlacementsBlacklistSearchForm searchParams) {
        this.searchParams = searchParams;
    }

    public PlacementsBlacklistSearchForm getModel() {
        return searchParams;
    }

    public List<PlacementBlacklist> getPlacementsBlacklist() {
        return placementsBlacklist;
    }

    public Breadcrumbs getBreadcrumbs() {
        if (breadcrumbs == null) {
            breadcrumbs = new Breadcrumbs()
                    .add(new CountriesBreadcrumbsElement())
                    .add(new CountryBreadcrumbsElement(getCountry()))
                    .add(new PlacementsBlacklistBreadcrumbsElement());
        }
        return breadcrumbs;
    }

    public boolean isSubmitSearchNeeded() {
        return submitSearchNeeded;
    }

    public void setSubmitSearchNeeded(boolean submitSearchNeeded) {
        this.submitSearchNeeded = submitSearchNeeded;
    }
}
