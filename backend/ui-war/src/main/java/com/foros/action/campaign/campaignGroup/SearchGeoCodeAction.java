package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.GeoChannelAddress;
import com.foros.session.channel.geo.GeoChannelService;

import javax.ejb.EJB;
import java.util.List;

public class SearchGeoCodeAction extends BaseActionSupport {

    @EJB
    private GeoChannelService geoChannelService;

    private String geoCode;
    private List<GeoChannelAddress> addresses;

    @ReadOnly
    public String search() {
        addresses = geoChannelService.searchRUAddress(geoCode);
        return SUCCESS;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public List<GeoChannelAddress> getAddresses() {
        return addresses;
    }
}
