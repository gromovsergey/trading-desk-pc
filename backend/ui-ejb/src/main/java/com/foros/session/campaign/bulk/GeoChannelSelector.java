package com.foros.session.campaign.bulk;

import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.validation.constraint.IdCollectionConstraint;

import java.util.List;

public class GeoChannelSelector implements Selector<GeoChannel> {

    @IdCollectionConstraint
    private List<Long> parentChannelIds;
    @IdCollectionConstraint
    private List<Long> channelIds;
    private List<String> countryCodes;
    private List<GeoType> geoTypes;
    private Paging paging;

    public List<Long> getParentChannelIds() {
        return parentChannelIds;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public List<GeoType> getGeoTypes() {
        return geoTypes;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private List<Long> parentChannelIds;
        private List<Long> channelIds;
        private List<String> countryCodes;
        private List<GeoType> geoTypes;
        private Paging paging;

        public Builder parentChannelIds(List<Long> parentChannelIds) {
            this.parentChannelIds = parentChannelIds;
            return this;
        }

        public Builder channelIds(List<Long> channelIds) {
            this.channelIds = channelIds;
            return this;
        }

        public Builder countryCodes(List<String> countryCode) {
            this.countryCodes = countryCode;
            return this;
        }

        public Builder geoTypes(List<GeoType> geoTypes) {
            this.geoTypes = geoTypes;
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public GeoChannelSelector build() {
            return new GeoChannelSelector(this);
        }
    }

    private GeoChannelSelector(Builder builder) {
        this.parentChannelIds = builder.parentChannelIds;
        this.channelIds = builder.channelIds;
        this.countryCodes = builder.countryCodes;
        this.geoTypes = builder.geoTypes;
        this.paging = builder.paging;
    }
}
