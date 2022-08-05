package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreative;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreativeOperations;
import com.foros.rs.client.model.siteCreative.ThirdPartyCreativeSelector;

import org.junit.Test;


public class ThirdPartyCreativeServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        ThirdPartyCreativeSelector selector = new ThirdPartyCreativeSelector();
        selector.setSiteId(longProperty("foros.test.site.id"));

        Result<ThirdPartyCreative> result = thirdPartyCreativeService.get(selector);
        assertNotNull(result);
        assertTrue(result.getEntities().size() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        EntityLink site = new EntityLink();
        site.setId(longProperty("foros.test.site.id"));

        EntityLink creative = new EntityLink();
        creative.setId(longProperty("foros.test.site.creative.id"));

        ThirdPartyCreative thirdPartyCreative = new ThirdPartyCreative();
        thirdPartyCreative.setCreative(creative);
        thirdPartyCreative.setThirdPartyCreativeId("RS TEST 00001");

        ThirdPartyCreativeOperations operations = new ThirdPartyCreativeOperations();
        operations.setSite(site);
        operations.getThirdPartyCreatives().add(thirdPartyCreative);

        thirdPartyCreativeService.perform(operations);
    }
}
