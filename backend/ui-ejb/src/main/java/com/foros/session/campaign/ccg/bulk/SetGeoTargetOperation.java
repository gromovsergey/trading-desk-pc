package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.GeoChannel;

import java.util.Collection;
import java.util.Set;

public class SetGeoTargetOperation extends GeoTargetOperationSupport {

    public SetGeoTargetOperation(Collection<Long> geoChannelIds) {
        super(geoChannelIds);
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        Set<GeoChannel> toUpdateChannels = toUpdate.getGeoChannels();
        toUpdateChannels.clear();
        toUpdateChannels.addAll(geoChannels);
    }
}
