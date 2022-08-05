package com.foros.session.channel.geo;

import com.foros.model.Country;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoChannelAddress;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface GeoChannelService {
    GeoChannel find(Long id);

    GeoChannel view(Long id);

    void delete(Long channelId);

    void undelete(Long channelId);

    Collection<GeoChannel> getStates(Country country);

    Collection<GeoChannel> getOrphanCities(Country country);

    GeoChannel findCountryChannel(String countryCode);

    List<GeoChannelAddress> searchRUAddress(String geoCode);

    GeoChannel findOrCreateAddressChannel(GeoChannel channel);

    GeoChannel createAddressChannel(GeoChannel channel);
}
