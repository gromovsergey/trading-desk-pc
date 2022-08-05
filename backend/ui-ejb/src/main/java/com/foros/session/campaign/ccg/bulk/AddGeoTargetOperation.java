package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.GeoChannel;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class AddGeoTargetOperation extends GeoTargetOperationSupport {

    public AddGeoTargetOperation(Collection<Long> geoChannelIds) {
        super(geoChannelIds);
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        Set<GeoChannel> newChannels = new TreeSet<>(BY_ID_COMPARATOR);
        newChannels.addAll(existing.getGeoChannels());
        newChannels.addAll(geoChannels);
        toUpdate.setGeoChannels(newChannels);
    }
}
