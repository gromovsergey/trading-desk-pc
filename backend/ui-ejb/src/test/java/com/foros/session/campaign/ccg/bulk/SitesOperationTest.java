package com.foros.session.campaign.ccg.bulk;

import com.foros.AbstractUnitTest;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.site.Site;
import com.foros.session.bulk.BulkOperation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import group.Bulk;
import group.Unit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Bulk.class} )
public class SitesOperationTest extends AbstractUnitTest {

    @Test
    public void testAdd() throws Exception {
        AddSitesOperation operation = new AddSitesOperation(sites(1L, 2L, 3L, 4L, 5L));

        CampaignCreativeGroup ccg;
        CampaignCreativeGroup res;

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(true);
        ccg.setSites(sites(1L, 2L, 3L, 7L));


        res = perform(operation, ccg);
        assertEquals(sites(1L, 2L, 3L, 4L, 5L, 7L), res.getSites());

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(false);

        res = perform(operation, ccg);
        assertEquals(sites(), res.getSites());
    }

    @Test
    public void testRemove() throws Exception {
        LinkedHashSet<Site> allSites = sites(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);

        RemoveSitesOperation operation = new RemoveSitesOperation(sites(1L, 2L, 3L, 4L, 5L), allSites);

        CampaignCreativeGroup ccg;
        CampaignCreativeGroup res;

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(true);
        ccg.setSites(sites(1L, 2L, 3L, 7L));


        res = perform(operation, ccg);
        assertEquals(sites(7L), res.getSites());

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(false);

        res = perform(operation, ccg);
        assertTrue(res.isIncludeSpecificSitesFlag());
        assertEquals(sites(6L, 7L, 8L, 9L), res.getSites());
        assertTrue(res.isChanged("includeSpecificSites"));
    }

    @Test
    public void testSet() throws Exception {
        // set to specific sites
        SetSitesOperation operation = new SetSitesOperation(sites(1L, 2L, 3L, 4L, 5L), true);

        CampaignCreativeGroup ccg;
        CampaignCreativeGroup res;

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(true);
        ccg.setSites(sites(1L, 2L, 3L, 4L, 5L));


        res = perform(operation, ccg);
        assertEquals(sites(1L, 2L, 3L, 4L, 5L), res.getSites());

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(false);

        res = perform(operation, ccg);
        assertTrue(res.isIncludeSpecificSitesFlag());
        assertEquals(sites(1L, 2L, 3L, 4L, 5L), res.getSites());

        // set to all sites
        operation = new SetSitesOperation(new ArrayList<Site>(), false);

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(true);
        ccg.setSites(sites(1L, 2L, 3L, 4L, 5L));


        res = perform(operation, ccg);
        assertFalse(res.isIncludeSpecificSitesFlag());
        assertEquals(sites(), res.getSites());
        assertTrue(res.isChanged("includeSpecificSites", "sites"));

        ccg = new CampaignCreativeGroup();
        ccg.setIncludeSpecificSitesFlag(false);

        res = perform(operation, ccg);
        assertFalse(res.isIncludeSpecificSitesFlag());
        assertEquals(sites(), res.getSites());
        assertTrue(res.isChanged("includeSpecificSites", "sites"));
    }

    private CampaignCreativeGroup perform(BulkOperation<CampaignCreativeGroup> operation, CampaignCreativeGroup existing) {
        CampaignCreativeGroup group = new CampaignCreativeGroup();
        operation.perform(existing, group);
        return group;
    }
    private LinkedHashSet<Site> sites(long... ids) {
        LinkedHashSet<Site> res = new LinkedHashSet<>();
        for (long id : ids) {
            res.add(new Site(id));
        }
        return res;
    }
}