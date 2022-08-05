package com.foros.action.admin.country.placementsBlacklist;

import com.foros.action.BaseActionSupport;
import com.foros.model.Country;
import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.service.PlacementsBlacklistService;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePlacementsBlacklistAction extends BaseActionSupport {
    private PlacementsBlacklistSearchForm searchParams = new PlacementsBlacklistSearchForm();
    private Country country;
    private List<Long> setNumberIds = new ArrayList<>();

    @EJB
    CountryService countryService;

    @EJB
    PlacementsBlacklistService placementsBlacklistService;

    @Restrict(restriction = "PlacementsBlacklist.update")
    public String drop() {
        placementsBlacklistService.dropAll(getCountry(), getPlacementsBlacklist());
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

    public List<Long> getSetNumberIds() {
        return setNumberIds;
    }

    public void setSetNumberIds(List<Long> setNumberIds) {
        this.setNumberIds = setNumberIds;
    }

    private Collection<PlacementBlacklist> getPlacementsBlacklist() {
        Map<Long, PlacementBlacklist> result = new HashMap<>(setNumberIds.size());
        for (Long id : setNumberIds) {
            PlacementBlacklist placement = new PlacementBlacklist();
            placement.setId(id);
            placement.setAction(BlacklistAction.REMOVE);

            result.put(id, placement);
        }
        return result.values();
    }
}
