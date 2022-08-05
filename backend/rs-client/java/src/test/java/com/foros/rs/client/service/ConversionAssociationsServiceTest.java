package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.CampaignGroupLink;
import com.foros.rs.client.model.advertising.conversion.ConversionAssociation;
import com.foros.rs.client.model.advertising.conversion.ConversionAssociationsSelector;

import org.junit.Test;

public class ConversionAssociationsServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {

        ConversionAssociationsSelector selector = new ConversionAssociationsSelector();
        selector.setConversionId(longProperty("foros.test.conversion.id"));
        ConversionAssociation association = conversionAssociationsService.get(selector);
        assertTrue(!association.getCreativeGroups().isEmpty());

        Long displayGroupId = longProperty("foros.test.displayGroup.id");
        Long textGroupId = longProperty("foros.test.creativeGroup.id");
        boolean displayGroupExist = false;
        boolean textGroupExist = false;

        for (CampaignGroupLink groupLink : association.getCreativeGroups()) {
            if (groupLink.getId().equals(displayGroupId)) {
                displayGroupExist = true;
            }

            if (groupLink.getId().equals(textGroupId)) {
                textGroupExist = true;
            }

            if (textGroupExist && displayGroupExist) {
                break;
            }
        }

        assertTrue(displayGroupExist);
        assertTrue(textGroupExist);

    }

}
