package com.foros.rs.client.model.geo;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class GeoChannelSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("channel.ids")
    private List<Long> channelIds;

    @QueryParameter("countryCodes")
    private List<String> countryCodes;

    @QueryParameter("parentChannel.ids")
    private List<Long> parentChannelIds;

    @QueryParameter("geoTypes")
    private List<GeoType> geoTypes;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public List<Long> getParentChannelIds() {
        return parentChannelIds;
    }

    public void setParentChannelIds(List<Long> parentChannelIds) {
        this.parentChannelIds = parentChannelIds;
    }

    public List<GeoType> getGeoTypes() {
        return geoTypes;
    }

    public void setGeoTypes(List<GeoType> geoTypes) {
        this.geoTypes = geoTypes;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}
