package com.foros.session.channel;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.TriggersChannel;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.DiscoverChannelListTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;

import group.Db;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ChannelServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private BehavioralChannelService behavioralChannelService;

    @Autowired
    private SearchChannelService searchChannelService;

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private DiscoverChannelTestFactory discoverChannelTF;

    @Autowired
    private DiscoverChannelListTestFactory discoverChannelListTF;

    @Test
    public void testGetChannelsForAdvertiserContext() {
        BehavioralChannel ch1 = behavioralChannelTF.createPersistent();
        behavioralChannelService.delete(ch1.getId());

        setDeletedObjectsVisible(true);
        Collection<ChannelTO> result = searchChannelService.findChannelsForAccount(ch1.getAccount().getId());
        assertNotNull(result);
        assertEquals(1, result.size());

        setDeletedObjectsVisible(false);
        result = searchChannelService.findChannelsForAccount(ch1.getAccount().getId());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testCreate() throws Exception {
        Account account = internalAccountTF.createPersistent();
        BehavioralChannel channel = behavioralChannelTF.create(account);

        behavioralChannelService.create(channel);

        assertEquals("Wrong status", Status.ACTIVE, channel.getStatus());
        assertEquals("Account is wrong", account, channel.getAccount());
    }

    @Test
    public void testFindMatchedChannelsByIds() {
        BehavioralChannel behavioralChannel = behavioralChannelTF.createPersistent();
        DiscoverChannel discoverChannel = discoverChannelTF.createPersistent();
        DiscoverChannelList discoverChannelList = discoverChannelListTF.createPersistent();

        Set<Long> channelIds = new HashSet<>();
        channelIds.add(behavioralChannel.getId());
        channelIds.add(discoverChannel.getId());
        channelIds.add(discoverChannelList.getId());

        List<TriggersChannel> matchedChannels = searchChannelService.findMatchedChannelsByIds(channelIds);

        assertEquals("Should match", 2, matchedChannels.size());
        assertTrue("Should contain", matchedChannels.contains(behavioralChannel));
        assertTrue("Should contain", matchedChannels.contains(discoverChannel));
    }

    @Test
    public void testSearchUsedChannels() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        searchChannelService.findAccountCCGUsedChannels(account.getId(), account.getCountry().getCountryCode());
    }
}
