package com.foros.session.opportunity;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.test.factory.OpportunityTestFactory;
import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.HashMap;

@Category(Db.class)
public class OpportunityServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private OpportunityTestFactory opportunityTF;

    @Autowired
    private OpportunityService opportunityService;

    @Test
    public void testCreate() {
        Opportunity opportunity = opportunityTF.create();
        Long id = opportunityService.create(opportunity, new HashMap<String, File>());
        getEntityManager().flush();

        assertNotNull("ID wasn't set", opportunity.getId());
        assertEquals(id, opportunity.getId());
    }

    @Test
    public void testUpdate() {
        Opportunity opportunity = opportunityTF.create();
        Long id = opportunityService.create(opportunity, new HashMap<String, File>());
        getEntityManager().flush();

        String updatedName = opportunityTF.getTestEntityRandomName();

        opportunity = opportunityService.find(id);
        opportunity.setName(updatedName);
        opportunity.setProbability(Probability.AWAITING_GO_LIVE);

        opportunity = opportunityService.update(opportunity, new HashMap<String, File>());
        assertEquals(updatedName, opportunity.getName());
    }

    @Test
    public void testFindOpportunitiesForAccount() {
        Opportunity opportunity = opportunityTF.create();
        Long id = opportunityService.create(opportunity, new HashMap<String, File>());
        getEntityManager().flush();

        opportunity = opportunityService.find(id);
        assertEquals(1, opportunityService.findOpportunitiesForAccount(opportunity.getAccount().getId()).size());
    }
}
