package com.foros.session.action;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.jaxb.adapters.CampaignGroupLink;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.TextCCGTestFactory;

import group.Db;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ActionServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private ActionService actionService;

    @Autowired
    public ActionTestFactory actionTestTF;

    @Autowired
    public CampaignCreativeGroupService groupService;

    @Autowired
    public AdvertiserAccountTestFactory advertiserAccountTestTF;

    @Autowired
    public TextCCGTestFactory ccgTestFactory;

    @Test
    public void testCreate() {
        Action action = actionTestTF.createPersistent();
        Action found = actionService.findById(action.getId());

        assertNotNull("ID wasn't set", action.getId());
        assertEquals("Status wasn't set", Status.ACTIVE, action.getStatus());
        assertSame("Entity is not created properly", action, found);
    }

    @Test
    public void testUpdate() {
        Action action = actionTestTF.createPersistent();
        Action created = actionService.findById(action.getId());

        String updatedName = actionTestTF.getTestEntityRandomName();
        String updatedUrl = "http://updated_url.org";
        created.setName(updatedName);
        created.setUrl(updatedUrl);
        actionService.update(created);

        Action updated = actionService.findById(created.getId());

        assertEquals("name wasn't updated", updatedName, updated.getName());
        assertEquals("url wasn't updated", updatedUrl, updated.getUrl());
    }

    @Test
    public void testDelete() {
        Action action = actionTestTF.createPersistent();

        actionService.delete(action.getId());
        Action found = actionService.findById(action.getId());

        assertEquals("Status is incorect", Status.DELETED, action.getStatus());
        assertEquals("Status is incorect", Status.DELETED, found.getStatus());
    }

    @Test
    public void testUndelete() {
        Action action = actionTestTF.createPersistent();

        actionService.delete(action.getId());
        Action deleted = actionService.findById(action.getId());

        actionService.undelete(deleted.getId());
        Action undeleted = actionService.findById(deleted.getId());

        assertEquals("Status is incorect", Status.ACTIVE, undeleted.getStatus());
    }

    @Test
    public void testSearch() {
        AdvertiserAccount account = advertiserAccountTestTF.createPersistent();
        actionTestTF.createPersistent(account);
        actionTestTF.createPersistent(account);

        List<?> actions = actionService.search(account.getId());

        int rowCount = jdbcTemplate.queryForInt("SELECT COUNT(0) FROM ACTION WHERE ACCOUNT_ID = ?", account.getId());
        assertEquals("JDBC query must show the same number of Actions", rowCount, actions.size());
    }

    @Test
    public void testGet() {
        AdvertiserAccount account = advertiserAccountTestTF.createPersistent();
        actionTestTF.createPersistent(account);
        actionTestTF.createPersistent(account);

        ActionSelector selector = new ActionSelector();
        selector.setAdvertiserIds(Arrays.asList(account.getId()));
        Result<Action> result = actionService.get(selector);
        assertEquals(2, result.getEntities().size());

        selector = new ActionSelector();
        Long actionId = result.getEntities().get(0).getId();
        selector.setActionIds(Arrays.asList(actionId));
        result = actionService.get(selector);
        assertEquals(1, result.getEntities().size());
        assertEquals(actionId, result.getEntities().get(0).getId());

    }

    @Test
    public void testGetAssociations() {
        CampaignCreativeGroup group = ccgTestFactory.createPersistent();
        Action action = actionTestTF.createPersistent(group.getAccount());

        groupService.linkConversions(Arrays.asList(group.getId()), Arrays.asList(action.getId()));
        commitChanges();

        Collection<CampaignGroupLink> ccgIds = actionService.getAssociations(action.getId());
        assertEquals(1, ccgIds.size());
        CampaignGroupLink first = ccgIds.iterator().next();
        assertEquals(group.getId(), first.getId());
        assertEquals(group.getCampaign().getId(), first.getCampaign().getId());

    }

    @Test
    public void testFindEntityTOByAgencyAccountId() {
        long agencyAccountId = jdbcTemplate.queryForLong("SELECT MIN(ACCOUNT_ID) FROM ACCOUNT WHERE STATUS = 'A' AND ROLE_ID = 4");
        List<EntityTO> actions = actionService.findEntityTOByMultipleParameters(agencyAccountId, null, null, false);
        int rowCount = jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM ACTION A JOIN ACCOUNT ACC ON ACC.ACCOUNT_ID = A.ACCOUNT_ID WHERE ACC.AGENCY_ACCOUNT_ID = ?", agencyAccountId);
        assertEquals("JDBC query must show the same number of Actions", rowCount, actions.size());
    }

    @Test
    public void testFindEntityTOForAloneAdvertisers() {
        List<EntityTO> actions = actionService.findEntityTOByMultipleParameters(null, null, null, false);

        int rowCount = jdbcTemplate.queryForInt(
                "SELECT COUNT(*) FROM ACTION A JOIN ACCOUNT ACC ON ACC.ACCOUNT_ID = A.ACCOUNT_ID WHERE ACC.AGENCY_ACCOUNT_ID IS NULL");
        assertEquals("JDBC query must show the same number of Actions", rowCount, actions.size());
    }

    @Test
    public void testFindEntityTOForAloneAdvertiser() {
        long advertiserId = jdbcTemplate.queryForLong("SELECT MIN(ACCOUNT_ID) FROM ACCOUNT WHERE STATUS = 'A' AND ROLE_ID = 1 AND AGENCY_ACCOUNT_ID IS NULL");
        List<EntityTO> actions = actionService.findEntityTOByMultipleParameters(advertiserId, null, null, false);

        int rowCount = jdbcTemplate.queryForInt("SELECT COUNT(0) FROM ACTION WHERE ACCOUNT_ID = ?", advertiserId);
        assertEquals("JDBC query must show the same number of Actions", rowCount, actions.size());
    }

    @Test
    public void testFindByAccountIdAndDate() {
        Action action = actionTestTF.createPersistent();
        LocalDate fromDate = new LocalDate();
        LocalDate toDate = new LocalDate();
        List<ActionTO> tos = actionService.findByAccountIdAndDate(action.getAccount().getId(), fromDate, toDate, true);
        assertNotNull(tos);
        assertEquals(1, tos.size());
    }

}

