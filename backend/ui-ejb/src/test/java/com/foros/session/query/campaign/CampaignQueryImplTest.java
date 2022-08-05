package com.foros.session.query.campaign;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.campaign.Campaign;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.query.QueryExecutorService;
import com.foros.test.factory.TextCampaignTestFactory;

import java.util.Collections;
import java.util.List;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CampaignQueryImplTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private QueryExecutorService executorService;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    @Test
    public void testQueryTO() {
        Campaign campaign = campaignTF.createPersistent();
        commitChanges();

        // NameTO
        List<IdNameTO> tos = new CampaignQueryImpl()
                .campaigns(Collections.singleton(campaign.getId()))
                .asNamedTO("account.id", "name")
                .executor(executorService)
                .list();

        assertNotNull(tos);
        assertEquals(1, tos.size());
        IdNameTO to = tos.get(0);
        assertNotNull(to);
        assertEquals(campaign.getAccount().getId(), to.getId());
        assertEquals(campaign.getName(), to.getName());
    }

    @Test
    public void testQueryName() {
        Campaign campaign = campaignTF.createPersistent();
        commitChanges();

        // Name property
        List<String> names = new CampaignQueryImpl()
                .campaigns(Collections.singleton(campaign.getId()))
                .asProperties("name")
                .executor(executorService)
                .list();

        assertEquals(1, names.size());
        assertEquals(campaign.getName(), names.get(0));
    }
}
