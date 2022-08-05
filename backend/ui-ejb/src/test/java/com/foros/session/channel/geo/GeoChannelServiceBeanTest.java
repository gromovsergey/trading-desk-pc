package com.foros.session.channel.geo;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.channel.Channel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoChannelAddress;
import com.foros.test.factory.GeoChannelTestFactory;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GeoChannelServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private GeoChannelTestFactory geoChannelTestFactory;

    @Autowired
    private GeoChannelService geoChannelService;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager entityManager;

    @Test
    public void testFindCountryChannel() throws Exception {
        assertNotNull(geoChannelService.findCountryChannel("RU"));

        assertNotNull(geoChannelService.findCountryChannel("US"));

        try {
            geoChannelService.findCountryChannel("??");
            fail("No such a channel");
        } catch (EntityNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testCreateAddress() {
        GeoChannel geoChannel = geoChannelTestFactory.create();
        GeoChannel newChannel = geoChannelService.findOrCreateAddressChannel(geoChannel);
        commitChanges();
        clearContext();

        GeoChannel persisted = geoChannelService.findOrCreateAddressChannel(newChannel);
        assertEquals("ADDRESS_" + persisted.getId(), persisted.getName());
        assertEquals(Status.ACTIVE, persisted.getStatus());
        assertEquals(Channel.LIVE, persisted.getDisplayStatus());

        GeoChannel sameAddr = geoChannelTestFactory.create();
        GeoChannel existing  = geoChannelService.findOrCreateAddressChannel(sameAddr);

        assertEquals(persisted.getId(), existing.getId());

        //Address with different radius
        GeoChannel diffRadius = geoChannelTestFactory.create();
        diffRadius.getRadius().setDistance(new BigDecimal(35));

        GeoChannel diffRadiusPersisted  = geoChannelService.findOrCreateAddressChannel(diffRadius);
        assertNotSame(persisted.getId(), diffRadiusPersisted.getId());
    }


    // @Test
    // Uncomment in OUI-27463
    public void testYandexGeoApi() {
        BigDecimal lat = new BigDecimal(55.7661).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal lon = new BigDecimal(37.6042).setScale(4, BigDecimal.ROUND_HALF_UP);
        boolean found = false;

        for (GeoChannelAddress addr : geoChannelService.searchRUAddress("город Москва, Тверская улица, дом 18, корпус 1")) {
            assertTrue(addr.getAddress().contains("Тверская"));
            if (addr.getLatitude().equals(lat) && addr.getLongitude().equals(lon))
                found = true;
        }
        assertTrue("Point: " + lat + ", " +  lon + " is not found", found);
    }
}