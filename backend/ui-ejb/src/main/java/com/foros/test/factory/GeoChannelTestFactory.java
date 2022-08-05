package com.foros.test.factory;

import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.model.channel.Coordinates;
import com.foros.model.channel.Radius;
import com.foros.model.channel.RadiusUnit;
import com.foros.session.channel.geo.GeoChannelService;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class GeoChannelTestFactory extends TestFactory<GeoChannel>  {

    @EJB
    private GeoChannelService geoChannelService;

    @EJB
    private CountryTestFactory countryTestFactory;

    @Override
    public GeoChannel create() {
        GeoChannel geoChannel = new GeoChannel();
        geoChannel.setName("ADDRESS");
        geoChannel.setGeoType(GeoType.ADDRESS);
        geoChannel.setAddress("Россия, Москва, Тверская улица, 18к1");
        geoChannel.setCoordinates(new Coordinates(new BigDecimal(55.761507), new BigDecimal(37.596013)));
        geoChannel.setRadius(new Radius(new BigDecimal(2), RadiusUnit.km));
        geoChannel.setCountry(countryTestFactory.find("RU"));
        return geoChannel;
    }

    @Override
    public void persist(GeoChannel entity) {
        geoChannelService.findOrCreateAddressChannel(entity);
    }

    @Override
    public void update(GeoChannel entity) {
        geoChannelService.findOrCreateAddressChannel(entity);
    }

    @Override
    public GeoChannel createPersistent() {
        GeoChannel geoChannel = create();
        persist(geoChannel);
        return geoChannel;
    }
}
