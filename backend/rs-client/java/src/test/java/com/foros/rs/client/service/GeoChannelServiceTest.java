package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.geo.GeoChannel;
import com.foros.rs.client.model.geo.GeoChannelSelector;
import com.foros.rs.client.model.geo.GeoType;
import com.foros.rs.client.model.operation.Result;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class GeoChannelServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        GeoChannelSelector selector = new GeoChannelSelector();
        Result<GeoChannel> result = geoChannelService.get(selector);
        assertTrue(!result.getEntities().isEmpty());

        selector.setGeoTypes(Arrays.asList(GeoType.CNTRY));
        assertFalse(geoChannelService.get(selector).getEntities().isEmpty());
        selector.setCountryCodes(Arrays.asList("GB"));
        List<GeoChannel> entities = geoChannelService.get(selector).getEntities();
        assertEquals(1, entities.size());

        long id = entities.get(0).getId();
        selector = new GeoChannelSelector();
        selector.setParentChannelIds(Arrays.asList(id));
        assertFalse(geoChannelService.get(selector).getEntities().isEmpty());

        selector = new GeoChannelSelector();
        selector.setChannelIds(Arrays.asList(id));
        assertTrue(geoChannelService.get(selector).getEntities().get(0).getId().equals(id));

    }

}
