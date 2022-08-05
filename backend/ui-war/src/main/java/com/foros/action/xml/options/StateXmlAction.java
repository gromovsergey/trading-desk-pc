package com.foros.action.xml.options;

import com.foros.model.channel.GeoChannel;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.util.NameValuePair;

import java.util.Collection;

import javax.ejb.EJB;

public class StateXmlAction extends AbstractOptionsAction<GeoChannel> {
    @EJB
    private GeoChannelService geoChannelService;

    @EJB
    private CountryService countryService;

    private String countryCode;

    public StateXmlAction() {
        super(new com.foros.action.xml.options.converter.Converter<GeoChannel>() {
            @Override
            public NameValuePair<String, String> convert(GeoChannel value) {
                return new NameValuePair<String, String>(value.getName(), String.valueOf(value.getId()));
            }
        });
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public Collection<? extends GeoChannel> getOptions() {
        Collection<GeoChannel> states = geoChannelService.getStates(countryService.find(getCountryCode()));
        return states;
    }
}
