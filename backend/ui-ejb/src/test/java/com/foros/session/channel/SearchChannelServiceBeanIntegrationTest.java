package com.foros.session.channel;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.ConfigParameters;
import com.foros.config.MockConfigService;
import com.foros.model.DisplayStatus;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.channel.ApiGeoChannelTO;
import com.foros.model.channel.BannedChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.security.SecurityContextMock;
import com.foros.service.RemoteServiceException;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.ChannelSelector;
import com.foros.session.campaign.bulk.DiscoverChannelSelector;
import com.foros.session.campaign.bulk.GeoChannelSelector;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.query.PartialList;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.test.factory.DiscoverChannelListTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;
import com.foros.test.factory.ExpressionChannelTestFactory;
import com.foros.test.factory.GeoChannelTestFactory;
import com.foros.util.jpa.DetachedList;

import group.Db;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class SearchChannelServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private SearchChannelService searchChannelService;

    @Autowired
    private CategoryChannelService categoryChannelService;

    @Autowired
    public BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private ExpressionChannelTestFactory expressionChannelTF;

    @Autowired
    private DiscoverChannelTestFactory discoverChannelTF;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTF;

    @Autowired
    private DiscoverChannelListTestFactory discoverChannelListTF;

    @Autowired
    private GeoChannelTestFactory geoChannelTF;

    @Autowired
    private MockConfigService mockConfigService;

    @Test
    public void testSearch() {
        ExpressionChannel persistent = expressionChannelTF.createPersistent();
        getEntityManager().refresh(persistent);
        try {
            assertTrue(searchChannelService.search(null, null, null, AccountSearchTestOption.INCLUDE,
                new AdvertisingChannelType[] { AdvertisingChannelType.EXPRESSION },
                new DisplayStatus[] { persistent.getDisplayStatus() }, null, null, null, 0, 20).size() > 0);
        } catch (RemoteServiceException e) {
            fail();
        }

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        try {
            searchChannelService.search(null, null, null, AccountSearchTestOption.INCLUDE,
                new AdvertisingChannelType[] { AdvertisingChannelType.EXPRESSION },
                new DisplayStatus[] { Channel.LIVE }, null, null,  null,0, 20);
        } catch (RemoteServiceException e) {
            fail();
        }

        try {
            searchChannelService.search(null, null, null, AccountSearchTestOption.INCLUDE,
                new AdvertisingChannelType[] { AdvertisingChannelType.AUDIENCE },
                new DisplayStatus[] { Channel.LIVE }, null, null,  null, 0, 20);
        } catch (RemoteServiceException e) {
            fail();
        }
    }

    @Test
    public void testGetDiscover() {
        int categoriesCount = 5;
        int channelsCount = 3;
        int resultCount = 2;

        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < channelsCount; i++) {
            DiscoverChannel discoverChannel = prepareDiscoverChannel(categoriesCount);
            ids.add(discoverChannel.getId());
        }

        getEntityManager().clear();

        DiscoverChannelSelector channelSelector = new DiscoverChannelSelector();
        channelSelector.setChannelIds(ids);
        channelSelector.setPaging(new Paging(0, resultCount));

        Result<DiscoverChannel> result = searchChannelService.getDiscover(channelSelector);
        assertTrue(result.getEntities().size() == resultCount);
        int categoriesSize = result.getEntities().get(0).getCategories().size();
        assertTrue("categoriesSize:" + categoriesSize, categoriesSize == categoriesCount);
    }

    @Test
    public void testSearchDiscover() {
        DiscoverChannel persistent = discoverChannelTF.createPersistent();
        try {
            assertTrue(searchChannelService.searchDiscover(null, null, null, null, 0, 20, null, persistent.getDisplayStatus()).size() > 0);
        } catch (RemoteServiceException e) {
            fail();
        }

        DiscoverChannel discoverChannel = prepareDiscoverChannel(0);
        getEntityManager().clear();

        try {
            PartialList<DiscoverChannelTO> list = searchChannelService.searchDiscover(discoverChannel.getName(), null, null, null, 0, 20, null, null);
            DiscoverChannelTO channelTO = new DiscoverChannelTO();
            channelTO.setId(discoverChannel.getId());
            assertTrue("Size:" + list.size(), list.contains(channelTO));
        } catch (RemoteServiceException e) {
            fail();
        }

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        try {
            searchChannelService.searchDiscover(null, null, null, null, 0, 20, null, Channel.LIVE);
        } catch (RemoteServiceException e) {
            fail();
        }

    }

    @Test
    public void testSearchDiscoverLists() {
        try {
            searchChannelService.searchDiscoverLists(null, null, null, null, 0, 20, null, Channel.LIVE);
        } catch (RemoteServiceException e) {
            fail();
        }

        DiscoverChannelList discoverChannelList = discoverChannelListTF.createPersistent();
        getEntityManager().clear();

        try {
            PartialList<DiscoverChannelListTO> list = searchChannelService.searchDiscoverLists(discoverChannelList.getName(), null, null, null, 0, 20, null);
            DiscoverChannelListTO to = new DiscoverChannelListTO();
            to.setId(discoverChannelList.getId());
            to.setName(discoverChannelList.getName());
            assertTrue("List should contain channel", list.contains(to));
        } catch (RemoteServiceException e) {
            fail();
        }

        SecurityContextMock.getInstance().setPrincipal(INTERNAL_USER_ACCOUNT_PRINCIPAL);
        try {
            searchChannelService.searchDiscoverLists(null, null, null, null, 0, 20, null, Channel.LIVE);
        } catch (RemoteServiceException e) {
            fail();
        }
    }

    @Test
    public void testFindChannelStatistics() {
        Channel bChannel = behavioralChannelTF.createPersistent();
        Channel eChannel = expressionChannelTF.createPersistent();
        Channel dChannel = discoverChannelTF.createPersistent();

        searchChannelService.findChannelStatistics(bChannel.getId());
        searchChannelService.findChannelStatistics(eChannel.getId());
        searchChannelService.findChannelStatistics(dChannel.getId());
    }

    @Test
    public void testSearchAdvertisingChannels() {
        try {
            DetachedList<ChannelTO> foundChannels = searchChannelService.searchAdvertisingChannels(2L, "", "", "GB", true, true, false);
            assertNotNull(foundChannels);
        } catch (RemoteServiceException e) {
            fail();
        }
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSearchNoAdvChannel() {
        searchChannelService.find(BannedChannel.NO_ADV_CHANNEL_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSearchNoTrackingChannel() {
        searchChannelService.find(BannedChannel.NO_TRACK_CHANNEL_ID);
    }

    private DiscoverChannel prepareDiscoverChannel(int categotriesCount) {
        DiscoverChannel discoverChannel = discoverChannelTF.createPersistent();
        Set<CategoryChannel> categories = new HashSet<CategoryChannel>();
        for (int i = 0; i < categotriesCount; i++) {
            categories.add(categoryChannelTF.createPersistent());
        }
        discoverChannel.setCategories(categories);
        categoryChannelService.updateChannelCategories(discoverChannel);
        commitChanges();

        return discoverChannelTF.refresh(discoverChannel);
    }

    @Test
    /*
    Current hibernate framework (version 3.6.10.Final) instantiates incorrect entity class when:
      1) There is an entities class hierarchy with discriminator column
      2) Several derived entity classes have property fetched from the same column and table in DB
      3) Property represented entity
      4) Types of this property are different in different derived classes
    Currently this is the case for Device and Geo channels, please see OUI-24558
     */
    public void testCorrectEntityClassInstantiating() {
        Long deviceChannelId = jdbcTemplate.queryForObject(
            "SELECT MAX(channel_id) FROM Channel WHERE channel_type = 'V' AND parent_channel_id IS NOT NULL", Long.class);
        searchChannelService.find(deviceChannelId);

        Long geoChannelId = jdbcTemplate.queryForObject(
            "SELECT MAX(channel_id) FROM Channel WHERE channel_type = 'G' AND parent_channel_id IS NOT NULL", Long.class);
        searchChannelService.find(geoChannelId);
    }

    @Test
    public void testGetPendingDiscoverChannelsCount() {
        int count = searchChannelService.getPendingDiscoverChannelsCount();
        assertTrue(count >= 0);
    }

    @Test
    public void testGet() throws RemoteServiceException {
        BehavioralChannel ch1 = behavioralChannelTF.createPersistent();
        BehavioralChannel ch2 = behavioralChannelTF.createPersistent(ch1.getAccount());

        commitChanges();

        // exactly one channel
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 3);

        ChannelSelector selector = new ChannelSelector();
        selector.setChannelIds(Arrays.asList(ch1.getId(), ch2.getId()));
        selector.setPaging(new Paging(0, 10));

        Result<Channel> res1 = searchChannelService.get(selector);
        assertEquals(1, res1.getEntities().size());
        assertEquals(1, (int) res1.getPaging().getCount());

        // all channels
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 100);

        res1 = searchChannelService.get(selector);
        assertEquals(2, res1.getEntities().size());
        assertEquals(10, (int) res1.getPaging().getCount());

        // exactly two channels
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 6);

        res1 = searchChannelService.get(selector);
        assertEquals(2, res1.getEntities().size());
        assertEquals(10, (int) res1.getPaging().getCount());

        // one channel and part of second
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 4);

        res1 = searchChannelService.get(selector);
        assertEquals(1, res1.getEntities().size());
        assertEquals(1, (int) res1.getPaging().getCount());

        // part of one channel
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 1);
        try {
            searchChannelService.get(selector);
            fail();
        } catch (TooManyTriggersException e) {
            // expected
        }

        // search by huge number of ids
        mockConfigService.set(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT, 100);
        selector = new ChannelSelector();
        int count = 1000000;
        List<Long> channelIds = new ArrayList<>(count + 1);
        channelIds.add(ch1.getId());
        for (long i = 0; i < count; i++) {
            channelIds.add(i);
        }
        selector.setChannelIds(channelIds);
        selector.setPaging(new Paging(0, 1));
        res1 = searchChannelService.get(selector);
        assertNotNull(res1);
        assertEquals(1, res1.getEntities().size());
    }

    @Test
    public void testGetGeoChannels() {
        GeoChannelSelector.Builder builder = new GeoChannelSelector.Builder();
        builder.paging(new Paging(3, 4));
        Result<ApiGeoChannelTO> geoChannels = searchChannelService.getGeoChannels(builder.build());
        assertTrue(!geoChannels.getEntities().isEmpty());

        GeoChannel geoChannel = geoChannelTF.createPersistent();
        builder = new GeoChannelSelector.Builder();
        builder.channelIds(Arrays.asList(geoChannel.getId()));
        geoChannels = searchChannelService.getGeoChannels(builder.build());
        assertEquals(geoChannel.getId(), geoChannels.getEntities().get(0).getId());

    }

}
