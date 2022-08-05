package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.device.DeviceChannelSelector;
import com.foros.rs.client.model.device.DeviceChannel;
import com.foros.rs.client.model.operation.Result;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class DeviceChannelServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        DeviceChannelSelector selector = new DeviceChannelSelector();
        Result<DeviceChannel> result = deviceChannelService.get(selector);
        List<DeviceChannel> entities = result.getEntities();
        assertFalse(entities.isEmpty());

        long id = longProperty("foros.test.device.mobileChannel.id");

        selector = new DeviceChannelSelector();
        selector.setChannelIds(Arrays.asList(id));
        entities = deviceChannelService.get(selector).getEntities();
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).getId().equals(id));

        selector = new DeviceChannelSelector();
        selector.setParentChannelIds(Arrays.asList(id));
        entities = deviceChannelService.get(selector).getEntities();
        assertFalse(entities.isEmpty());
    }
}
