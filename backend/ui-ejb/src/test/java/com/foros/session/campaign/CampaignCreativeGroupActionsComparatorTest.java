package com.foros.session.campaign;

import com.foros.model.action.Action;
import com.foros.model.campaign.CampaignCreativeGroup;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(Unit.class)
public class CampaignCreativeGroupActionsComparatorTest {
    @Test
    public void compareWithNull() {
        CampaignCreativeGroupActionsComparator comparator = new CampaignCreativeGroupActionsComparator();
        CampaignCreativeGroup cc = new CampaignCreativeGroup();

        assertTrue(comparator.compare(null, null) == 0);
        assertTrue(comparator.compare(cc, null) != 0);
        assertTrue(comparator.compare(null, cc) != 0);
        assertTrue(comparator.compare(cc, cc) == 0);
    }

    @Test
    public void compareProperties() {
        CampaignCreativeGroupActionsComparator comparator = new CampaignCreativeGroupActionsComparator();
        CampaignCreativeGroup oldCC = new CampaignCreativeGroup();
        CampaignCreativeGroup newCC = new CampaignCreativeGroup();

        Action a1 = new Action();
        a1.setName("Test");

        oldCC.getActions().add(a1);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getActions().add(a1);
        assertTrue(comparator.compare(oldCC, newCC) == 0);
    }
}
