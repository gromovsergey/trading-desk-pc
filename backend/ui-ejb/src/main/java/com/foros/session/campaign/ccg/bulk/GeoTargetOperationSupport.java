package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.GeoChannel;
import com.foros.session.bulk.BulkOperation;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class GeoTargetOperationSupport implements BulkOperation<CampaignCreativeGroup> {

    protected static final Comparator<GeoChannel> BY_ID_COMPARATOR = new Comparator<GeoChannel>() {
        @Override
        public int compare(GeoChannel o1, GeoChannel o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    protected Set<GeoChannel> geoChannels;

    public GeoTargetOperationSupport(Set<GeoChannel> geoChannels) {
        this.geoChannels = geoChannels;
    }

    public GeoTargetOperationSupport(Collection<Long> geoChannelIds) {
        this(new LinkedHashSet<>(CollectionUtils.convert(geoChannelIds, new Converter<Long, GeoChannel>() {
            @Override
            public GeoChannel item(Long id) {
                return new GeoChannel(id);
            }
        })));
    }

    public Set<GeoChannel> getGeoChannels() {
        return geoChannels;
    }
}
