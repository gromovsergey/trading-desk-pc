package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.model.publishing.Site;
import com.foros.rs.client.model.publishing.SiteSelector;

import java.util.Arrays;

import org.junit.Test;

public class SiteServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        SiteSelector  selector = new SiteSelector();
        selector.setSiteIds(Arrays.asList(longProperty("foros.test.site.id")));

        Result<Site> result = siteService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());

        result = siteService.get(new SiteSelector());
        assertNotNull(result);
    }
}