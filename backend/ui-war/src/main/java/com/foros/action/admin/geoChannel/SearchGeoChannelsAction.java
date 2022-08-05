package com.foros.action.admin.geoChannel;

import com.foros.action.admin.channel.SearchChannelsActionBase;
import com.foros.framework.ReadOnly;
import com.foros.model.Country;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.geo.GeoChannelTO;
import com.foros.session.query.PartialList;

import java.util.Arrays;
import javax.ejb.EJB;

import com.foros.util.EntityUtils;
import org.apache.commons.lang.StringUtils;

public class SearchGeoChannelsAction extends SearchChannelsActionBase {
    @EJB
    private CountryService countryService;

    private PartialList<GeoChannelTO> channelLists;
    private String stateLabel;
    private String cityLabel;

    @Override
    @ReadOnly
    @Restrict(restriction = "GeoChannel.view")
    public String main() {
        super.main();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "GeoChannel.view")
    public String search() {
        try {
            channelLists = searchChannelService.searchGeoChannels(searchParams.getName(),
                    searchParams.getCountryCode(), searchParams.getFirstResultCount(), searchParams.getPageSize());
            appendStatusSuffix(channelLists);
            searchParams.setTotal((long) channelLists.getTotal());
            Country country;
            if (StringUtils.isBlank(searchParams.getCountryCode())) {
                country = new Country(); // mock country
            } else {
                country = countryService.find(searchParams.getCountryCode());
            }
            GeoChannelHelper helper = new GeoChannelHelper(country);
            setStateLabel(helper.getStateLabel());
            setCityLabel(helper.getCityLabel());
        } catch (Exception e) {
            addActionError(getText("errors.serviceIsNotAvailable",
                    Arrays.asList(getText("channel.channelSearchService"))));
        }

        return SUCCESS;
    }

    public PartialList<GeoChannelTO> getChannelLists() {
        return channelLists;
    }

    private void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    private void setCityLabel(String cityLabel) {
        this.cityLabel = cityLabel;
    }

    public String getCityLabel() {
        return cityLabel;
    }

    private void appendStatusSuffix(PartialList<GeoChannelTO> channels) {
        for (GeoChannelTO channelTO : channels) {
            channelTO.setName(EntityUtils.appendStatusSuffix(channelTO.getName(), channelTO.getStatus()));
        }
    }
}
