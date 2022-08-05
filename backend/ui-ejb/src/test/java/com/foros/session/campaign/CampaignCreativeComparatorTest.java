package com.foros.session.campaign;

import com.foros.model.FrequencyCap;
import com.foros.model.campaign.CampaignCreative;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(Unit.class)
public class CampaignCreativeComparatorTest {
    @Test
    public void compareWithNull() {
        CampaignCreativeComparator comparator = new CampaignCreativeComparator();
        CampaignCreative cc = new CampaignCreative();

        assertTrue(comparator.compare(null, null) == 0);
        assertTrue(comparator.compare(cc, null) != 0);
        assertTrue(comparator.compare(null, cc) != 0);
        assertTrue(comparator.compare(cc, cc) == 0);
    }

    @Test
    public void compareProperties() {
        CampaignCreativeComparator comparator = new CampaignCreativeComparator();
        CampaignCreative oldCC = new CampaignCreative();
        CampaignCreative newCC = new CampaignCreative();

        oldCC.setWeight(15L);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.setWeight(15L);
        assertTrue(comparator.compare(oldCC, newCC) == 0);

        oldCC.setFrequencyCap(new FrequencyCap());
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.setFrequencyCap(new FrequencyCap());
        assertTrue(comparator.compare(oldCC, newCC) == 0);

        oldCC.getFrequencyCap().setPeriod(1);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setPeriod(10);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setPeriod(1);
        assertTrue(comparator.compare(oldCC, newCC) == 0);

        oldCC.getFrequencyCap().setLifeCount(1);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setLifeCount(10);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setLifeCount(1);
        assertTrue(comparator.compare(oldCC, newCC) == 0);

        oldCC.getFrequencyCap().setWindowCount(1);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setWindowCount(10);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setWindowCount(1);
        assertTrue(comparator.compare(oldCC, newCC) == 0);

        oldCC.getFrequencyCap().setWindowLength(1);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setWindowLength(10);
        assertTrue(comparator.compare(oldCC, newCC) != 0);
        newCC.getFrequencyCap().setWindowLength(1);
        assertTrue(comparator.compare(oldCC, newCC) == 0);
    }
}
