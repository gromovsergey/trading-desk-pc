package com.foros.session.channel.service;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Country;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.session.channel.KeywordChannelCsvTO;
import com.foros.session.channel.KeywordChannelTO;
import com.foros.session.channel.triggerQA.TriggerQASearchFilter;
import com.foros.session.channel.triggerQA.TriggerQASearchParameters;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQASortType;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;

import group.Db;
import group.Restriction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class KeywordChannelServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private BehavioralParamsTestFactory behavioralParamsTF;

    @Autowired
    private KeywordChannelService keywordChannelService;

    @Autowired
    private TriggerQAService triggerQAService;

    @Test
    public void testFindOrCreate() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        Set<String> keywords = new HashSet<String>();
        keywords.add(UUID.randomUUID().toString());
        keywords.add(UUID.randomUUID().toString());

        Map<String, Long> map = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords);
        commitChanges();

        assertNotNull(map);
        assertTrue(map.keySet().containsAll(keywords));

        Map<String, Long> map2 = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords);
        getEntityManager().flush();

        assertNotNull(map2);
        assertEquals(map, map2);

        Map.Entry<String, Long> testEntry = map.entrySet().iterator().next();
        KeywordChannel channel = keywordChannelService.findById(testEntry.getValue());
        assertNotNull(channel);
        assertEquals(testEntry.getKey(), channel.getName());

        TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, TriggerQAType.KEYWORD, TriggerQASearchFilter.CHANNEL,
                null, null, null, null, null, channel.getId(), "GB",
                null, null, null, null, TriggerQASortType.NEWEST);
        List<TriggerQATO> list = triggerQAService.search(parameters);
        assertEquals(1, list.size());
    }

    @Test
    public void testSearch() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        String keyword = UUID.randomUUID().toString();
        Set<String> keywords = new HashSet<String>();
        keywords.add(keyword);

        Long channelId = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords).get(keyword);
        assertNotNull(channelId);
        getEntityManager().flush();

        List<KeywordChannelTO> list = keywordChannelService.search(0, 1, keyword, account.getId(), "GB");
        assertTrue(list.size() > 0);

        KeywordChannelTO channelTO = new KeywordChannelTO();
        channelTO.setId(channelId);
        assertTrue(list.contains(channelTO));
    }

    @Test
    public void testExport() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        String keyword = UUID.randomUUID().toString();
        Set<String> keywords = new HashSet<String>();
        keywords.add(keyword);

        Long channelId = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords).get(keyword);
        assertNotNull(channelId);
        getEntityManager().flush();

        KeywordChannel channel = keywordChannelService.findById(channelId);
        assertNotNull(channel);

        Collection<KeywordChannelCsvTO> channels = keywordChannelService.export(200, keyword, account.getId(), "GB", null);
        assertTrue(channels.size() > 0);
    }

    @Test
    public void testUpdateDefaultParameters() throws Exception {
        BehavioralParameters searchParams = behavioralParamsTF.createBParam(TriggerType.SEARCH_KEYWORD);
        BehavioralParameters pageParams = behavioralParamsTF.createBParam(TriggerType.PAGE_KEYWORD);
        DefaultKeywordSettingsTO settingsTO = keywordChannelService.findDefaultKeywordSettings();
        settingsTO.getBehavioralParameters().clear();
        settingsTO.getBehavioralParameters().add(searchParams);
        settingsTO.getBehavioralParameters().add(pageParams);
        keywordChannelService.updateDefaultParameters(settingsTO);
        commitChanges();

        settingsTO = keywordChannelService.findDefaultKeywordSettings();

        assertNotNull(settingsTO);
        searchParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.SEARCH_KEYWORD);
        pageParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.PAGE_KEYWORD);
        assertNotNull(searchParams);
        assertNotNull(pageParams);
        Long searchId = searchParams.getId();
        Long pageId = pageParams.getId();
        assertNotNull(searchId);
        assertNotNull(pageId);

        settingsTO = keywordChannelService.findDefaultKeywordSettings();
        searchParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.SEARCH_KEYWORD);
        pageParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.PAGE_KEYWORD);

        searchParams.setMinimumVisits(10L);
        pageParams.setMinimumVisits(20L);

        keywordChannelService.updateDefaultParameters(settingsTO);
        commitChanges();

        settingsTO = keywordChannelService.findDefaultKeywordSettings();

        assertNotNull(settingsTO);
        searchParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.SEARCH_KEYWORD);
        pageParams = TriggerType.findBehavioralParameters(settingsTO.getBehavioralParameters(), TriggerType.PAGE_KEYWORD);
        assertNotNull(searchParams);
        assertNotNull(pageParams);

        assertEquals(searchId, searchParams.getId());
        assertEquals(pageId, pageParams.getId());
        assertEquals(20L, pageParams.getMinimumVisits().longValue());
        assertEquals(10L, searchParams.getMinimumVisits().longValue());
    }

    @Test
    public void testUpdateAll() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        Set<String> keywords = new HashSet<String>();
        keywords.add(UUID.randomUUID().toString());
        keywords.add(UUID.randomUUID().toString());

        Map<String, Long> map = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords);
        List<KeywordChannel> channels = createChannelTOList(map);
        commitChanges();

        // Behavioral Parameters set 1
        for (KeywordChannel channel : channels) {
            channel.getBehavioralParameters().add(createBehavioralParameters('S', 60L, 120L));
        }

        keywordChannelService.updateAll(channels);
        commitChanges();

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            KeywordChannel channel = keywordChannelService.findById(entry.getValue());
            assertNotNull(channel.getBehavioralParameters());
            assertEquals(1, channel.getBehavioralParameters().size());
        }

        // Behavioral Parameters set 2
        for (KeywordChannel channel : channels) {
            channel.getBehavioralParameters().clear();
            channel.getBehavioralParameters().add(createBehavioralParameters('S', 60L, 120L));
            channel.getBehavioralParameters().add(createBehavioralParameters('P', 3600L, 7200L));
        }

        keywordChannelService.updateAll(channels);
        commitChanges();

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            KeywordChannel channel = keywordChannelService.findById(entry.getValue());
            assertNotNull(channel.getBehavioralParameters());
            assertEquals(2, channel.getBehavioralParameters().size());
        }
    }

    @Test
    public void testUpdateAllInvalidCreate() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        Set<String> keywords = new HashSet<String>();
        keywords.add(UUID.randomUUID().toString());
        keywords.add(UUID.randomUUID().toString());

        Map<String, Long> map = keywordChannelService.findOrCreate(account.getId(), "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords);

        List<KeywordChannel> channels = new ArrayList<KeywordChannel>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            KeywordChannel channel = keywordChannelService.findById(entry.getValue());
            KeywordChannel copy = copy(channel);
            copy.setName(UUID.randomUUID().toString());
            channels.add(copy);
        }

        getEntityManager().clear();

        for (KeywordChannel channelTO : channels) {
            channelTO.getBehavioralParameters().add(createBehavioralParameters('S', 60L, 120L));
        }

        try {
            keywordChannelService.updateAll(channels);
            fail("Channels creating should not be allowed!");
        } catch (Exception e) {
        }
    }

    private List<KeywordChannel> createChannelTOList(Map<String, Long> map) {
        List<KeywordChannel> channels = new ArrayList<KeywordChannel>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            KeywordChannel channel = keywordChannelService.findById(entry.getValue());
            channels.add(copy(channel));
        }
        return channels;
    }

    private KeywordChannel copy(KeywordChannel channel) {
        KeywordChannel copy = new KeywordChannel();
        copy.setName(channel.getName());
        copy.setTriggerType(channel.getTriggerType());
        InternalAccount account = new InternalAccount();
        account.setName(channel.getAccount().getName());
        account.setCountry(new Country(channel.getCountry().getCountryCode()));
        copy.setAccount(account);
        return copy;
    }

    private BehavioralParameters createBehavioralParameters(char triggerType, Long timeFrom, Long timeTo) {
        BehavioralParameters param = new BehavioralParameters();
        param.setMinimumVisits(10L);
        param.setTimeFrom(timeFrom);
        param.setTimeTo(timeTo);
        param.setTriggerType(triggerType);
        return param;
    }
}
