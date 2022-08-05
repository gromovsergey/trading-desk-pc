package com.foros.action.xml.options;

import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.channel.GeoChannel;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.util.NameValuePair;
import com.foros.util.comparator.IdNameComparator;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;

public class CityXmlAction extends AbstractOptionsAction<GeoChannel> {
    @EJB
    private GeoChannelService geoChannelService;
    @EJB
    private CountryService countryService;

    private Long stateId;
    private String countryCode;

    public CityXmlAction() {
        super(new com.foros.action.xml.options.converter.Converter<GeoChannel>() {
            @Override
            public NameValuePair<String, String> convert(GeoChannel value) {
                return new NameValuePair<String, String>(value.getName(),String.valueOf(value.getId()));
            }
        });
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public Collection<? extends GeoChannel> getOptions() {
        if (stateId == null) {
            Country country = countryService.find(getCountryCode());
            return geoChannelService.getOrphanCities(country);
        }
        GeoChannel channel = geoChannelService.view(stateId);
        return prepare(channel);
    }

    private Set<GeoChannel> prepare(GeoChannel target) {
        Set<GeoChannel> channels =  new TreeSet<GeoChannel>(new IdNameComparator());
        for (GeoChannel channel : target.getChildChannels()) {
            if (!channel.getStatus().equals(Status.DELETED)) {
                channels.add(channel);
            }
        }
        return channels;
    }
}
