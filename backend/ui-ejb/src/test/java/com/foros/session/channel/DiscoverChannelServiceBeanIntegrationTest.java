package com.foros.session.channel;

import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.trigger.PageKeywordTrigger;
import com.foros.model.channel.trigger.PageKeywordsHolder;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.session.channel.triggerQA.TriggerQASearchParameters;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQASortType;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;
import com.foros.util.EntityUtils;
import com.foros.util.xml.QADescriptionChannelMaxUrlTriggerShare;

import group.Db;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class DiscoverChannelServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<DiscoverChannel> {
    @Autowired
    private DiscoverChannelService discoverChannelService;

    @Autowired
    private DiscoverChannelTestFactory discoverChannelTF;

    @Autowired
    private BehavioralParamsTestFactory bParamsTF;

    @Autowired
    private TriggerQAService triggerQAService;

    @Test
    public void testUpdateWithBehavioralParams() throws Exception {
        DiscoverChannel dc = discoverChannelTF.createPersistent();
        getEntityManager().clear();

        BehavioralParametersList bParamsList = bParamsTF.createPersistent();
        dc.setBehavParamsList(bParamsList);

        dc.getPageKeywords().setPositive("pageKeyword");
        dc.getSearchKeywords().setPositive("searchKeyword");
        dc.getUrls().setPositive("http://test.site.ru");

        discoverChannelService.update(dc);
        entityManager.flush();

        int cnt = jdbcTemplate.queryForObject(
                "select count(*) from channel where channel_id = ? and behav_params_list_id = ?",
                Integer.class,
                dc.getId(), bParamsList.getId());
        assertEquals("DiscoverChannel was not bound to BehavioralParametersList", 1, cnt);
    }

    @Test
    public void testCreateCopy() throws Exception {
        DiscoverChannel dc = discoverChannelTF.createPersistent();
        dc.setLanguage("ru");
        Long dcCopyId = discoverChannelService.copy(dc.getId());
        assertNotNull("Copy of WD Channel was not created", dcCopyId);
        DiscoverChannel copy = discoverChannelService.view(dcCopyId);
        assertNotNull("Copy of WD Channel was not created", copy);
        assertFalse(dc.getName().equalsIgnoreCase(copy.getName()));
        assertEquals(dc.getLanguage(), copy.getLanguage());
    }

    @Test
    public void testTriggersRenewal() throws Exception {
        DiscoverChannel channel = discoverChannelTF.create();
        channel.setCountry(new Country("US"));
        channel.getPageKeywords().setPositive("test");
        channel.getSearchKeywords().setPositive("testsearch");
        channel.getUrls().setPositive("test.com");
        discoverChannelTF.persist(channel);

        commitChanges();

        TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, null, null,
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        List<TriggerQATO> triggers = triggerQAService.search(parameters);
        assertEquals(3, triggers.size());

        parameters = new TriggerQASearchParameters(0, 100, null, "test",
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        triggers = triggerQAService.search(parameters);
        assertEquals(2, triggers.size());

        entityManager.clear();
        discoverChannelService.update(channel);
        entityManager.flush();

        commitChanges();

        parameters = new TriggerQASearchParameters(0, 100, null, null,
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        triggers = triggerQAService.search(parameters);
        assertEquals(3, triggers.size());

        parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.KEYWORD, "testsearch",
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        triggers = triggerQAService.search(parameters);
        assertEquals(1, triggers.size());

        parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.URL, "test.com",
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        triggers = triggerQAService.search(parameters);
        assertEquals(1, triggers.size());
    }

    private void testDisplayStatus(DiscoverChannel channel, Status status, char[] triggerQa, DisplayStatus expected) {
        channel.setStatus(status);
        discoverChannelService.update(channel);
        commitChanges();

        List<TriggerQATO> triggers = triggerQATOsByChannel(channel);
        assertEquals(2, triggers.size());
        triggers.get(0).setQaStatus(ApproveStatus.valueOf(triggerQa[0]));
        triggers.get(1).setQaStatus(ApproveStatus.valueOf(triggerQa[1]));
        triggerQAService.update(triggers);

        channel = discoverChannelTF.refresh(channel);

        assertEquals(expected, channel.getDisplayStatus());
    }

    private List<TriggerQATO> triggerQATOsByChannel(DiscoverChannel channel) {
        TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, null, null,
                null, null, null, channel.getId(), channel.getCountry().getCountryCode(), null, TriggerQASortType.NEWEST);
        return triggerQAService.search(parameters);
    }

    @Test
    public void testDisplayStatus() throws Exception {
        Account account = internalAccountTF.createPersistent();
        account.getCountry().setLowChannelThreshold(0L);
        account.getCountry().setHighChannelThreshold(0L);
        commitChanges();

        DiscoverChannel channel = prepareChannel(account);

        testDisplayStatus(channel, Status.ACTIVE, new char[] {'A', 'A'}, DiscoverChannel.LIVE);

        // All triggers are declined
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'D', 'D'}, DiscoverChannel.DECLINED);

        // At least one trigger is approved
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'A', 'H'}, DiscoverChannel.LIVE_TRIGGERS_NEED_ATT);
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'A', 'A'}, DiscoverChannel.LIVE);

        // No approved triggers, but at least one is pending
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'D', 'H'}, DiscoverChannel.PENDING_FOROS);

        // Inactive
        testDisplayStatus(channel, Status.INACTIVE, new char[] {'A', 'A'}, DiscoverChannel.INACTIVE);

        // Deleted
        setDeletedObjectsVisible(true);
        testDisplayStatus(channel, Status.DELETED, new char[]{'A', 'A'}, DiscoverChannel.DELETED);
    }

    private DiscoverChannel prepareChannel(Account account) {
        DiscoverChannel channel = new DiscoverChannel();
        EntityUtils.copy(channel, discoverChannelTF.createPersistent(account));

        List<PageKeywordTrigger> pageKeywordsList = new ArrayList<PageKeywordTrigger>();
        for (int i = 0; i < 2; i++) {
            String keyword = UUID.randomUUID().toString();
            pageKeywordsList.add(new PageKeywordTrigger(channel.getCountry().getCountryCode(), keyword, false));
        }

        PageKeywordsHolder pHolder = channel.getPageKeywords();
        pHolder.setAll(pageKeywordsList);

        channel.setUrls(null);
        channel.setSearchKeywords(null);
        channel.setPageKeywords(pHolder);

        discoverChannelService.update(channel);
        commitChanges();

        channel = discoverChannelService.view(channel.getId());
        return channel;
    }

    @Test
    public void testNotEnoughUniqueUsers() throws Exception {
        Account account = internalAccountTF.createPersistent();
        account.getCountry().setLowChannelThreshold(1000L);
        account.getCountry().setHighChannelThreshold(4000L);

        commitChanges();

        DiscoverChannel channel = prepareChannel(account);

        testDisplayStatus(channel, Status.ACTIVE, new char[] {'A', 'A'}, DiscoverChannel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS);

        commitChanges();
        insertIntoChannelInventory(channel.getId(), 10000);

        entityManager.refresh(channel);
        assertEquals(DiscoverChannel.LIVE, channel.getDisplayStatus());

        // All triggers are declined
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'D', 'D'}, DiscoverChannel.DECLINED);

        // After channel was declined need to check unique users again
        testDisplayStatus(channel, Status.ACTIVE, new char[] {'A', 'A'}, DiscoverChannel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS);
    }


    @Test
    public void testDisplayStatusCountryChanged() {
        Account account = internalAccountTF.createPersistent();
        DiscoverChannel channel = discoverChannelTF.createPersistent(account);
        commitChanges();
        channel.setCountry(new Country(channel.getCountry().getCountryCode().equals("GB") ? "US" : "GB"));
        discoverChannelService.update(channel);
        commitChanges();
        assertTrue(discoverChannelService.view(channel.getId()).getQaStatus().equals(ApproveStatus.APPROVED));
    }

    @Test
    public void testMakeApprovedOnChange() {
        Account account = internalAccountTF.createPersistent();
        DiscoverChannel channel = discoverChannelTF.createPersistent(account);

        channel.setQaStatus(ApproveStatus.DECLINED);
        channel.setQaDescriptionObject(new QADescriptionChannelMaxUrlTriggerShare());
        entityManager.merge(channel);
        commitChanges();

        clearContext();
        channel.getUrls().setPositive("updated-url.com");
        discoverChannelService.update(channel);
        commitChanges();

        assertTrue(discoverChannelService.view(channel.getId()).getQaStatus().equals(ApproveStatus.APPROVED));
    }
}
