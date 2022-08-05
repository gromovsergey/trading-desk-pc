package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.device.Platform;
import com.foros.rs.client.model.operation.PagingSelector;

import java.util.List;
import org.junit.Test;

public class PlatformServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() {
        PlatformService platformService = foros.getPlatformService();

        List<Platform> platforms = platformService.fetcher()
                .withPageSize(10)
                .all();

        int totalSize = platforms.size();
        assertTrue(totalSize > 20);

        // fetch max 10 platforms with page size 3
        PagingSelector selector = new PagingSelector();
        selector.setCount(10L);
        List<Platform> max10 = platformService.fetcher()
                .withPageSize(3)
                .fetch(selector);

        assertEquals(10, max10.size());
        assertNull(selector.getFirst());
        assertEquals(Long.valueOf(10L), selector.getCount());


        selector.setFirst(5L);
        List<Platform> max10first5 = platformService.fetcher()
                .withPageSize(3)
                .fetch(selector);

        assertEquals(10, max10first5.size());
        assertEquals(Long.valueOf(5L), selector.getFirst());
        assertEquals(Long.valueOf(10L), selector.getCount());

    }
}