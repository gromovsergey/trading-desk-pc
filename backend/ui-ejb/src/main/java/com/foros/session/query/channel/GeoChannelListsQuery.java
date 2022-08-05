package com.foros.session.query.channel;

import com.foros.model.channel.GeoType;

import java.util.List;

public interface GeoChannelListsQuery extends ChannelQuery<GeoChannelListsQuery>{

    GeoChannelListsQuery orderByName();

    GeoChannelListsQuery parentChannels(List<Long> parentChannelIds);

    GeoChannelListsQuery geoTypes(List<GeoType> geoTypes);

    GeoChannelListsQuery notAddress();
}
