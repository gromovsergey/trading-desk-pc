package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.action.admin.geoChannel.GeoChannelHelper;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.model.Country;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.GeoChannel;
import com.foros.session.admin.country.CountryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import javax.ejb.EJB;

public class EditBulkGeotargetAction extends BulkGeotargetActionSupport {
    @EJB
    private CountryService countryService;

    private Country country;
    private List<CountryCO> countries;
    private Collection<GeoChannel> geoChannels;
    private Collection<GeoChannel> states;
    private Collection<GeoChannel> cities;
    private String stateLabel;
    private String cityLabel;

    @ReadOnly
    public String edit() {
        country = getGroupsCountry();
        if (country != null) {
            states = geoChannelService.getStates(country);
            cities = geoChannelService.getOrphanCities(country);
            country = countryService.find(country.getCountryCode());
            GeoChannelHelper helper = new GeoChannelHelper(country);
            setStateLabel(helper.getStateLabel());
            setCityLabel(helper.getCityLabel());
        } else {
            countries = new ArrayList<>();
            geoChannels = new ArrayList<>();
            states = new ArrayList<>();
            cities = new ArrayList<>();
        }

        return SUCCESS;
    }

    public Collection<GeoChannel> getCities() {
        return cities;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel;
    }

    public String getCountryCode() {
        return (country != null) ? country.getCountryCode() : "";
    }

    public String getCityLabel() {
        return cityLabel;
    }

    public void setCityLabel(String cityLabel) {
        this.cityLabel = cityLabel;
    }

    public Country getCountry() {
        return country;
    }

    public Collection<GeoChannel> getGeoChannels() {
        return geoChannels;
    }

    private Country getGroupsCountry() {
        TreeMap<String, Country> country = new TreeMap<String, Country>();
        for (Long id: ids) {
            CampaignCreativeGroup group = groupService.find(id);
            Country groupCountry = group.getCountry();
            country.put(groupCountry.getCountryCode(), groupCountry);
        }
        return (country.size() == 1) ? country.firstEntry().getValue() : null;
    }

    public Collection<GeoChannel> getStates() {
        return states;
    }
}
