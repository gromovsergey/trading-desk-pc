package com.foros.session.channel.service;

import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignAssociationTO;
import com.foros.model.channel.*;
import com.foros.service.RemoteServiceException;
import com.foros.session.EntityTO;
import com.foros.session.NamedTO;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.ChannelSelector;
import com.foros.session.campaign.bulk.DiscoverChannelSelector;
import com.foros.session.campaign.bulk.GeoChannelSelector;
import com.foros.session.channel.*;
import com.foros.session.channel.geo.GeoChannelTO;
import com.foros.session.query.PartialList;
import com.foros.util.jpa.DetachedList;
import org.joda.time.LocalDate;

import javax.ejb.Local;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Local
public interface SearchChannelService {
    Collection<ChannelCCGUsedTO> findAccountCCGUsedChannels(Long accountId, String countryCode);

    ChannelStatsTO findChannelStatistics(Long channelId);

    Channel find(Long id);

    Channel findWithCategories(Long id);

    Channel view(Long id);

    Collection<ChannelTO> findChannelsForAccount(Long advertiserAccountId);

    Collection<CampaignAssociationTO> findCampaignAssociations(Long channelId, LocalDate dateFrom, LocalDate dateTo);

    Collection<EntityTO> findAdvertisingChannelsByAccount(Long accountId);

    Collection<EntityTO> findByAccountAndStatus(Long accountId, DisplayStatus displayStatus,
            ChannelVisibilityCriteria visibilityCriteria, String countryCode,
            String name, int maxResults);

    Collection<EntityTO> findDiscoverByAccountAndStatus(Long accountId, Long discoverChannelListId,
            DisplayStatus displayStatus, String countryCode,
            String name, int maxResults);

    Collection<EntityTO> findDiscoverListsByAccount(Long accountId, String countryCode);

    Collection<EntityTO> findBehavioralDiscoverChannels(Long accountId, String query, int firstResult, int maxResults);

    Collection<EntityTO> findAdvertisingByAccountAndStatuses(Long accountId, DisplayStatus[] displayStatuses);

    /**
     * Search Expression and Behaviour channels with specified criterias.
     *
     * @param accountId search by channel owner. Unused if null.
     * @param name search by name. Unused if empty.
     * @param content search by content. Unused if empty.
     * @param countryCode search by country code.
     * @param searchMy add all my channels to search.
     * @param searchPub add all public channels to search.
     * @param searchCmp add all cmp channels to search.
     * @return found channels
     * @throws RemoteServiceException e
     */
    DetachedList<ChannelTO> searchAdvertisingChannels(Long accountId, String name, String content, String countryCode, boolean searchMy,
            boolean searchPub, boolean searchCmp) throws RemoteServiceException;

    List<TriggersChannel> findMatchedChannelsByIds(Set<Long> ids);

    PopulatedMatchInfo match(String url, String keywords) throws RemoteServiceException;

    PartialList<ChannelTO> search(
            String name, Long accountId, String countryCode,
            AccountSearchTestOption testOption,
            AdvertisingChannelType[] types,
            DisplayStatus[] displayStatuses,
            ChannelVisibilityCriteria visibility,
            String keyword, Long categoryChannelId, int from, int count) throws RemoteServiceException;

    Collection<Channel> searchForExport(
            String name, Long accountId, String countryCode,
            AccountSearchTestOption testOption,
            AdvertisingChannelType[] types,
            DisplayStatus[] displayStatuses,
            ChannelVisibilityCriteria visibility,
            String keyword, int maxResultsCount) throws RemoteServiceException;

    PartialList<DiscoverChannelTO> searchDiscover(
            String name, Long accountId, String countryCode, String language,
            int from, int count, String keyword, DisplayStatus... displayStatuses) throws RemoteServiceException;

    PartialList<DiscoverChannelListTO> searchDiscoverLists(
            String name, Long accountId, String countryCode, String language,
            int from, int count, String keyword, DisplayStatus... displayStatuses) throws RemoteServiceException;

    PartialList<GeoChannelTO> searchGeoChannels(String name, String countryCode, int from, int count);

    int getPendingAdvertisingChannelsCount();

    int getPendingDiscoverChannelsCount();

    Collection<ChannelReportTO> findChannelsByAccountAndType(
            Long accountId, String name,
            List<Class<? extends Channel>> channelClasses,
            int maxResults);

    Collection<ChannelReportTO> findChannelsByAccountTypeAndVisibility(
            Long accountId, String name,
            List<Class<? extends Channel>> channelClasses,
            ChannelVisibility visibility,
            int maxResults);

    List<ExpressionAssociationTO> findExpressionAssociations(Long channelId);

    Collection<EntityTO> findSupersededChannelsByAccountAndCountry(Long accountId, String countryCode,
            Long selectedId, Long selfId,
            String name, int maxResults);

    Result<Channel> get(ChannelSelector channelSelector) throws RemoteServiceException;

    Result<DiscoverChannel> getDiscover(DiscoverChannelSelector channelSelector);

    boolean hasLiveAdvertisers(Long channelId);

    Collection<NamedTO> findAdvertisingChannels(String accountName, String countryCode);

    NamedTO findAdvertisingChannel(String channelName, String accountName, String countryCode);

    List<? extends Channel> resolveChannelTargets(List<String> channelNames, AdvertisingAccountBase defaultAccount, Country country);

    Channel findChannelTarget(Long id);

    ChannelPerformanceTO findChannelPerformanceStats(Long channelId);

    ChannelLiveAssociationsStatsTO findChannelAssociationsStats(Long channelId);

    Result<ApiGeoChannelTO> getGeoChannels(GeoChannelSelector channelSelector);

    Result<ApiDeviceChannelTO> getDeviceChannels(DeviceChannelSelector channelSelector);
}
