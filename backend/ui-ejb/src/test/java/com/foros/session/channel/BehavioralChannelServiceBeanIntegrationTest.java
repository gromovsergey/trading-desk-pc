package com.foros.session.channel;

import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.trigger.TriggerBase;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.triggerQA.TriggerQASearchFilter;
import com.foros.session.channel.triggerQA.TriggerQASearchParameters;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQASortType;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.test.factory.BehavioralChannelTestFactory;

import group.Db;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class BehavioralChannelServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<BehavioralChannel> {

    @Autowired
    private BehavioralChannelService behavioralChannelService;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private TriggerQAService triggerQAService;

    @Autowired
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @Test
    public void canCopyPrivateChannelTest() {
        Account account = internalAccountTF.createPersistent();
        final BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setVisibility(ChannelVisibility.PRI);
        behavioralChannelTF.update(channel);
        behavioralChannelTF.refresh(channel);
        assertNotNull(channel.getId());
        currentUserRule.setPrincipal(ADVERTISER_PRINCIPAL);
        assertFalse(advertisingChannelRestrictions.canCreateCopy(channel));
    }

    @Test
    public void canCopyPublicChannelTest() {
        Account account = internalAccountTF.createPersistent();
        final BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setVisibility(ChannelVisibility.PUB);
        behavioralChannelTF.update(channel);
        behavioralChannelTF.refresh(channel);
        assertNotNull(channel.getId());
        currentUserRule.setPrincipal(ADVERTISER_PRINCIPAL);
        assertTrue(advertisingChannelRestrictions.canCreateCopy(channel));
    }

    @Test
    public void testBehavioralChannelUpdate() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();

        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        behavioralChannelTF.refresh(channel);
        assertNotNull(channel.getId());

        Timestamp version = channel.getVersion();
        BehavioralChannel updatedChannel = behavioralChannelTF.create(account);
        updatedChannel.setId(channel.getId());
        Long id = behavioralChannelService.update(updatedChannel);
        entityManager.flush();
        assertNotNull(id);
        channel = behavioralChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));

        // update triggers only
        version = channel.getVersion();

        updatedChannel = behavioralChannelTF.create(account);
        updatedChannel.setId(channel.getId());
        updatedChannel.setName(behavioralChannelTF.getTestEntityRandomName());

        behavioralChannelService.update(updatedChannel);
        entityManager.flush();

        channel = behavioralChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));
    }

    @Test
    public void testBehavioralChannelCreateCopy() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        BehavioralChannel channel2 = behavioralChannelTF.createPersistent(account);
        channel.setSupersededByChannel(channel2);
        channel.setLanguage("ru");

        behavioralChannelTF.update(channel);
        behavioralChannelTF.submitToCmp(channel);

        Long newChannelId = behavioralChannelService.copy(channel.getId());
        commitChanges();
        clearContext();
        assertNotSame(channel.getId(), newChannelId);

        BehavioralChannel newChannel = behavioralChannelService.view(newChannelId);
        assertNotNull(newChannel);
        assertEquals(newChannel.getUrls().size(), channel.getUrls().size());
        assertEquals(newChannel.getPageKeywords().size(), channel.getPageKeywords().size());
        assertEquals(newChannel.getSearchKeywords().size(), channel.getSearchKeywords().size());
        assertEquals(newChannel.getTriggers().size(), channel.getTriggers().size());
        assertEquals(channel.getAccount(), newChannel.getAccount());
        assertNull(newChannel.getSupersededByChannel());
        assertEquals(ChannelVisibility.PRI, newChannel.getVisibility());
        assertNull(newChannel.getChannelRate());
        assertEquals(channel.getLanguage(), newChannel.getLanguage());
    }

    @Test
    public void testCMPChannelCreateCopy() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        BehavioralChannel channel2 = behavioralChannelTF.createPersistent(account);
        channel.setLanguage("ru");
        channel.setSupersededByChannel(channel2);

        behavioralChannelTF.update(channel);
        behavioralChannelTF.submitToCmp(channel);
        behavioralChannelService.inactivate(channel.getId());
        assertEquals(Status.PENDING_INACTIVATION, channel.getStatus());

        Long newChannelId = behavioralChannelService.copy(channel.getId());
        assertNotSame(channel.getId(), newChannelId);

        BehavioralChannel newChannel = behavioralChannelService.view(newChannelId);
        assertNotNull(newChannel);
        assertEquals(channel.getAccount(), newChannel.getAccount());
        assertNull(newChannel.getSupersededByChannel());
        assertEquals(ChannelVisibility.PRI, newChannel.getVisibility());
        assertNull(newChannel.getChannelRate());
        assertEquals(Status.ACTIVE, newChannel.getStatus());
        assertEquals(ApproveStatus.APPROVED, newChannel.getQaStatus());
        assertNotSame(channel.getStatus(), newChannel.getStatus());
        assertEquals(channel.getQaStatus(), newChannel.getQaStatus());
        assertEquals(channel.getLanguage(), newChannel.getLanguage());
    }

    private void testDisplayStatus(BehavioralChannel channel, Status status, ApproveStatus qaStatus, char[] triggerQa, DisplayStatus expected) {
        setDeletedObjectsVisible(true);
        channel.setStatus(status);
        channel.setQaStatus(qaStatus);
        behavioralChannelService.update(channel);
        commitChanges();
        if (triggerQa != null) {
            TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.KEYWORD, TriggerQASearchFilter.CHANNEL,
                    null, null, null, null, null, channel.getId(), channel.getCountry().getCountryCode(),
                    null, null, null, null, TriggerQASortType.NEWEST);
            List<TriggerQATO> triggers = triggerQAService.search(parameters);
            assertEquals(2, triggers.size());
            triggers.get(0).setQaStatus(ApproveStatus.valueOf(triggerQa[0]));
            triggers.get(1).setQaStatus(ApproveStatus.valueOf(triggerQa[1]));
            triggerQAService.update(triggers);
        }

        entityManager.refresh(channel);
        commitChanges();

        assertEquals(expected, channel.getDisplayStatus());
    }


    @Test
    public void testDisplayStatus() throws Exception {
        setDeletedObjectsVisible(true);

        Account account = internalAccountTF.createPersistent();
        account.getCountry().setLowChannelThreshold(0L);
        account.getCountry().setHighChannelThreshold(0L);
        entityManager.flush();

        BehavioralChannel channel = prepareBehavioralChannel(account);

        // All triggers are declined
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'D', 'D'}, BehavioralChannel.DECLINED);

        // At least one trigger is approved
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'A', 'H'}, BehavioralChannel.LIVE_TRIGGERS_NEED_ATT);
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.LIVE);

        // No approved triggers, but at least one is pending
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'D', 'H'}, BehavioralChannel.PENDING_FOROS);

        // Inactive
        testDisplayStatus(channel, Status.INACTIVE, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.INACTIVE);

        // Deleted
        testDisplayStatus(channel, Status.DELETED, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.DELETED);

        // Pending inactivation
        testDisplayStatus(channel, Status.PENDING_INACTIVATION, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.LIVE_PENDING_INACTIVATION);
        testDisplayStatus(channel, Status.PENDING_INACTIVATION, ApproveStatus.APPROVED, new char[] {'A', 'H'}, BehavioralChannel.LIVE_AMBER_PENDING_INACTIVATION);
    }

    @Test
    public void testNotEnoughUniqueUsers() throws Exception {
        Account account = internalAccountTF.createPersistent();
        account.getCountry().setLowChannelThreshold(1000L);
        account.getCountry().setHighChannelThreshold(4000L);
        commitChanges();

        BehavioralChannel channel = prepareBehavioralChannel(account);

        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS);

        insertIntoChannelInventory(channel.getId(), 10000);

        entityManager.refresh(channel);
        commitChanges();

        assertEquals(BehavioralChannel.LIVE, channel.getDisplayStatus());

        // All triggers are declined
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'D', 'D'}, BehavioralChannel.DECLINED);

        // After channel was declined need to check unique users again
        testDisplayStatus(channel, Status.ACTIVE, ApproveStatus.APPROVED, new char[] {'A', 'A'}, BehavioralChannel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS);
    }

    private BehavioralChannel prepareBehavioralChannel(Account account) throws Exception {
        BehavioralChannel channel = behavioralChannelTF.create(account);
        String keyword1 = UUID.randomUUID().toString().replace("-", "Z");
        String keyword2 = UUID.randomUUID().toString().replace("-", "Z");
        channel.getPageKeywords().setPositive(keyword1, keyword2);
        channel.getSearchKeywords().clear();
        channel.getUrls().clear();
        behavioralChannelTF.persist(channel);

        behavioralChannelTF.submitToCmp(channel);
        return channel;
    }

    @Test
    public void testTriggerURLSearch()  throws Exception {
        Account account = internalAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.create(account);

        String domain = UUID.randomUUID().toString();
        String url1 = domain + ".com/" + UUID.randomUUID().toString();
        String url2 = domain + ".com/" + UUID.randomUUID().toString();
        channel.getUrls().setPositive(url1, url2);

        Long channelId = behavioralChannelService.create(channel);
        entityManager.flush();

        commitChanges();

        TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.URL, TriggerQASearchFilter.ALL,
                url1, null, null, null, null, channelId, channel.getCountry().getCountryCode(),
                null, null, null, null, TriggerQASortType.NEWEST);
        int size = triggerQAService.search(parameters).size();
        assertEquals(1, size);

        parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.URL, TriggerQASearchFilter.ALL,
                "\"" + url2 + "\"", null, null, null, null, channelId, channel.getCountry().getCountryCode(),
                null, null, null, null, TriggerQASortType.NEWEST);
        size = triggerQAService.search(parameters).size();
        assertEquals(1, size);

        String[] arrayUrl = new String[4002];
        Arrays.fill(arrayUrl, "s");
        String maxSizeUrl = Arrays.toString(arrayUrl);
        parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.URL, TriggerQASearchFilter.ALL,
            "\"" + maxSizeUrl + "\"", null, null, null, null, channelId, channel.getCountry().getCountryCode(),
            null, null, null, null, TriggerQASortType.NEWEST);
        size = triggerQAService.search(parameters).size();
        assertEquals(0, 0);

    }

    @Test
    public void testTriggerKeywordSearch() throws Exception {
        Account account = internalAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.create(account);
        String keyword = UUID.randomUUID().toString().trim();

        channel.getPageKeywords().setPositive(keyword);
        channel.getSearchKeywords().setPositive(keyword);

        Long channelId = behavioralChannelService.create(channel);
        entityManager.flush();

        commitChanges();

        TriggerQASearchParameters parameters;
        parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.KEYWORD, TriggerQASearchFilter.ALL,
                keyword, null, null, null, null, channelId, channel.getCountry().getCountryCode(),
                null, null, null, null, TriggerQASortType.NEWEST);
        int size = triggerQAService.search(parameters).size();
        assertEquals(1, size);
    }

    @Test
    public void testMakePublic() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();

        BehavioralChannel channel = behavioralChannelTF.create(account);
        channel.setVisibility(ChannelVisibility.PRI);
        behavioralChannelTF.persist(channel);

        behavioralChannelService.makePublic(channel.getId(), channel.getVersion());

        BehavioralChannel updated = behavioralChannelService.view(channel.getId());
        assertEquals(ChannelVisibility.PUB, updated.getVisibility());
    }

    @Test
    public void testSubmitToCmp() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();

        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setStatus(Status.ACTIVE);
        channel.setQaStatus(ApproveStatus.APPROVED);
        behavioralChannelTF.update(channel);

        Long channelId = channel.getId();

        BigDecimal cpc = new BigDecimal(123);
        RateType rateType = RateType.CPC;
        channel.setChannelRate(behavioralChannelTF.createChannelRate(channel, rateType, cpc));

        behavioralChannelService.submitToCmp(channel);

        BehavioralChannel updated = behavioralChannelService.view(channelId);
        assertEquals(ChannelVisibility.CMP, updated.getVisibility());
        assertEquals(cpc, updated.getChannelRate().getCpc());
        assertEquals(rateType, updated.getChannelRate().getRateType());
    }

    @Test
    public void testEditCmp() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();

        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        channel.setStatus(Status.ACTIVE);
        channel.setQaStatus(ApproveStatus.APPROVED);
        channel.setChannelRate(behavioralChannelTF.createChannelRate(channel, RateType.CPC, new BigDecimal(123)));
        behavioralChannelService.submitToCmp(channel);
        entityManager.flush();
        entityManager.clear();

        Long channelId = channel.getId();
        String newName = behavioralChannelTF.getTestEntityRandomName();
        channel.setName(newName);
        String newDescription = behavioralChannelTF.getTestEntityRandomName();
        channel.setDescription(newDescription);

        BehavioralChannel supersededByChannel = behavioralChannelTF.createPersistent(account);
        channel.setSupersededByChannel(supersededByChannel);

        BigDecimal cpc = new BigDecimal(12345);
        RateType rateType = RateType.CPC;
        channel.setChannelRate(behavioralChannelTF.createChannelRate(channel, rateType, cpc));

        behavioralChannelService.update(channel);

        BehavioralChannel updated = behavioralChannelService.view(channelId);
        assertEquals(newName, updated.getName());
        assertEquals(newDescription, updated.getDescription());
        assertEquals(supersededByChannel.getId(), updated.getSupersededByChannel().getId());
        assertEquals(cpc, updated.getChannelRate().getCpc());
        assertEquals(rateType, updated.getChannelRate().getRateType());
    }

    @Test
    public void testEditSuperseded() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);

        BehavioralChannel supersededBy1 = behavioralChannelTF.createPersistent(account);
        BehavioralChannel supersededBy2 = behavioralChannelTF.createPersistent(account);

        // init superseded
        getEntityManager().flush();
        getEntityManager().clear();

        channel.setSupersededByChannel(supersededBy1);
        behavioralChannelService.update(channel);
        channel = getEntityManager().find(BehavioralChannel.class, channel.getId());
        channel.getCategories().size();
        assertEquals(supersededBy1, channel.getSupersededByChannel());

        // change superseded
        getEntityManager().clear();

        channel.setSupersededByChannel(supersededBy2);
        behavioralChannelService.update(channel);
        channel = getEntityManager().find(BehavioralChannel.class, channel.getId());
        assertEquals(supersededBy2, channel.getSupersededByChannel());

        // clear superseded
        getEntityManager().clear();

        channel.setSupersededByChannel(null);
        behavioralChannelService.update(channel);
        channel = getEntityManager().find(BehavioralChannel.class, channel.getId());
        assertNull(channel.getSupersededByChannel());
    }

    @Test
    public void testUpdateStatus() {
        CmpAccount account = cmpAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.createPersistent(account);
        getEntityManager().flush();
        getEntityManager().clear();

        channel = behavioralChannelTF.refresh(channel);
        assertFalse(channel.getPageKeywords().isEmpty());

        BehavioralChannel toUpdate = new BehavioralChannel();
        toUpdate.setId(channel.getId());
        toUpdate.setStatus(Status.INACTIVE);

        behavioralChannelService.update(toUpdate);
        getEntityManager().flush();
        getEntityManager().clear();

        channel = behavioralChannelTF.refresh(channel);
        assertEquals(Status.INACTIVE, channel.getStatus());
        assertFalse(channel.getPageKeywords().isEmpty());
    }

    @Test
    public void testCreateNormalizedUrls() {
        Account account = internalAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.create(account);

        List<UrlTrigger> urls = new ArrayList<>();
        urls.add(new UrlTrigger("http://google.com", false));
        urls.add(new UrlTrigger("www.google.com", false));
        urls.add(new UrlTrigger("google.com/", false));
        urls.add(new UrlTrigger("google.com:80", false));
        urls.add(new UrlTrigger("google.com#anchor", false));
        urls.add(new UrlTrigger("http://mail.ru", false));

        channel.getUrls().setPositive(urls);

        behavioralChannelService.create(channel);
        entityManager.flush();
        commitChanges();

        assertEquals(2, channel.getUrls().size());
        Map<TriggerType, Collection<? extends TriggerBase>> removedTriggers = channel.getProperty(BehavioralChannelService.REMOVED_TRIGGERS);
        assertNotNull(removedTriggers);
        assertEquals(4, removedTriggers.get(TriggerType.URL).size());
    }

    @Test
    public void testUpdateNormalizedUrls() {
        Account account = internalAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.create(account);
        List<UrlTrigger> urls = new ArrayList<>();
        urls.add(new UrlTrigger("google.com", false));
        channel.getUrls().setPositive(urls);
        Long channelId = behavioralChannelService.create(channel);
        entityManager.flush();
        commitChanges();
        clearContext();

        // update the channel with the same URLs (in their normalized form)
        channel = behavioralChannelTF.create();
        channel.setId(channelId);
        urls = new ArrayList<>();
        urls.add(new UrlTrigger("http://google.com", false));
        urls.add(new UrlTrigger("www.google.com", false));
        urls.add(new UrlTrigger("google.com/", false));
        urls.add(new UrlTrigger("google.com:80", false));
        urls.add(new UrlTrigger("google.com#anchor", false));
        urls.add(new UrlTrigger("http://mail.ru", false));
        channel.getUrls().setPositive(urls);
        behavioralChannelService.update(channel);

        BehavioralChannel updated = behavioralChannelService.find(channelId);

        assertEquals(2, updated.getUrls().size());
        Map<TriggerType, Collection<? extends TriggerBase>> removedTriggers = channel.getProperty(BehavioralChannelService.REMOVED_TRIGGERS);
        assertEquals(4, removedTriggers.get(TriggerType.URL).size());
    }
}
