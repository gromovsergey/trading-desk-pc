package com.foros.action.campaign.campaignGroup;


import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.channel.Coordinates;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoChannelAddress;
import com.foros.model.channel.Radius;
import com.foros.session.channel.geo.GeoChannelService;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class GeoCoordXmlAction extends AbstractXmlAction<GeoChannelAddress> {
    @EJB
    private GeoChannelService geoChannelService;

    private Long channelId;

    @RequiredStringValidator(key = "errors.required", message = "Channel.channelId")
    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    @Override
    protected GeoChannelAddress generateModel() throws ProcessException {
        GeoChannel channel = geoChannelService.find(channelId);
        GeoChannelAddress address = new GeoChannelAddress();
        address.setAddress(channel.getAddress());
        Coordinates coordinates = channel.getCoordinates();
        if (coordinates != null) {
            address.setLatitude(coordinates.getLatitude());
            address.setLongitude(coordinates.getLongitude());
        }
        Radius radius = channel.getRadius();
        if (radius != null) {
            address.setRadius(radius.getDistance());
            address.setRadiusUnits(radius.getRadiusUnit());
        }
        return address;
    }
}
