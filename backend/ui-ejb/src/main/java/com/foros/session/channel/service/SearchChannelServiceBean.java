package com.foros.session.channel.service;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAssociationTO;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.*;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.service.RemoteServiceException;
import com.foros.service.ServiceProvider;
import com.foros.session.*;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.ChannelSelector;
import com.foros.session.campaign.bulk.DiscoverChannelSelector;
import com.foros.session.campaign.bulk.GeoChannelSelector;
import com.foros.session.channel.*;
import com.foros.session.channel.geo.GeoChannelTO;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.channel.*;
import com.foros.session.security.AccountTO;
import com.foros.session.security.UserService;
import com.foros.util.*;
import com.foros.util.bean.Filter;
import com.foros.util.expression.ExpressionHelper;
import com.foros.util.jpa.DetachedList;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.jpa.NamedQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;
import com.foros.validation.util.ValidationUtil;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.LocalDate;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;

import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelMatchInfo;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearch;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchResult;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.WDiscoverChannelMatchInfo;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.WMatchInfo;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.WNewsItemInfo;

@Stateless(name = "SearchChannelService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class SearchChannelServiceBean implements SearchChannelService {
    public static final Column EXPRESSION_CHANNEL_ID = buildColumn("expression_channel_id", "expression_channel_id", ColumnTypes.id());

    private static final Logger logger = Logger.getLogger(SearchChannelServiceBean.class.getName());

    private static final int MAX_SEARCH_RESULTS = 1000;
    private static final int MAX_CHANNEL_OVERLAP_STATS_ROWS = 10;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private AdvertisingChannelRestrictions advertisingRestrictions;

    @EJB
    private DiscoverChannelRestrictions discoverRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private UserService userService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private AccountService accountService;

    @EJB
    private ConfigService configService;

    @EJB
    private BulkChannelToolsService bulkChannelToolsService;

    @EJB
    private ExpressionService expressionService;

    @EJB
    private ExpressionChannelValidations expressionChannelValidations;

    @EJB
    private TriggerService triggerService;

    @EJB
    private AudienceChannelService audienceChannelService;

    @EJB
    private LoggingJdbcTemplate loggingJdbcTemplate;

    @Override
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('Account', #accountId)")
    public Collection<ChannelCCGUsedTO> findAccountCCGUsedChannels(Long accountId, String countryCode) {

        final List<ChannelCCGUsedTO> result = loggingJdbcTemplate.withAuthContext()
                .query("select * from statqueries.ccg_used_channels(?::integer, ?::varchar)",
            new Object[] {
                    accountId,
                    countryCode != null && countryCode.isEmpty() ? null : countryCode
            },
            new RowMapper<ChannelCCGUsedTO>() {

                @Override
                public ChannelCCGUsedTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long channelId = rs.getLong(1);
                    String channelName = rs.getString(2);
                    long displayStatusId = rs.getLong(3);
                    String visibility = rs.getString(4);
                    BigDecimal userCount = rs.getBigDecimal(5);
                    Long imps = rs.getLong(6);
                    int reuse = rs.getInt(7);
                    Date lastUse = rs.getDate(8);
                    List<NamedTO> ccgInfos = PGArray.read(rs.getArray(9), new Converter<PGRow, NamedTO>() {
                        @Override
                        public NamedTO item(PGRow row) {
                            Long id = row.getLong(0);
                            Long displayStatusId1 = row.getLong(2);
                            String convertedName = EntityUtils.appendStatusSuffix(row.getString(1), CampaignCreativeGroup.getDisplayStatus(displayStatusId1));
                            return new NamedTO(id, convertedName);
                        }
                    });
                    long accId = rs.getLong(10);
                    String accountName = rs.getString(11);
                    BigDecimal rate = rs.getBigDecimal(12);
                    String rateType = rs.getString(13);
                    Long currencyId = rs.getLong(14);

                    ChannelCCGUsedTO to = new ChannelCCGUsedTO(channelId, channelName,
                        accId, accountName, displayStatusId);
                    to.setReuse(reuse);
                    to.setImps(imps);
                    to.setLastUse(lastUse);
                    to.setUserCount(userCount);
                    to.setRate(rate);
                    to.setRateType(rateType);
                    to.setVisibility(ChannelVisibility.valueOf(visibility));
                    to.setCurrencyId(currencyId);

                    to.getCcgs().addAll(ccgInfos);
                    return to;
                }

            });
        return result;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('Account', #accountId)")
    public DetachedList<ChannelTO> searchAdvertisingChannels(Long accountId, String name, String content, String countryCode,
            boolean searchMy, boolean searchPub, boolean searchCmp) throws RemoteServiceException {

        List<Long> idsToSearch = getIdsToSearch(content);
        if (StringUtil.isPropertyEmpty(name) && StringUtil.isPropertyNotEmpty(content) && idsToSearch.isEmpty()) {
            return new DetachedList<ChannelTO>();
        }

        final List<ChannelTO> channels = loggingJdbcTemplate.query(new PreparedStatementCreatorFactory(
            "select * from statqueries.find_advertising_channels(?, ?, ?, ?, ?, ?, ?)",
            new int[] {
                    Types.INTEGER,
                    Types.CHAR,
                    Types.VARCHAR,
                    Types.ARRAY,
                    Types.BOOLEAN,
                    Types.BOOLEAN,
                    Types.BOOLEAN
            }).newPreparedStatementCreator(new Object[] {
                accountId,
                countryCode != null && countryCode.isEmpty() ? null : countryCode,
                name,
                loggingJdbcTemplate.createArray("bigint", idsToSearch),
                searchMy,
                searchCmp,
                searchPub
        }), new RowMapper<ChannelTO>() {

            @Override
            public ChannelTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                ChannelTO channelTO = new ChannelTO();
                channelTO.setId(rs.getLong(1));
                channelTO.setName(rs.getString(2));
                channelTO.setAccountId(rs.getLong(9));
                channelTO.setAccountName(rs.getString(10));
                channelTO.setDisplayStatus(Channel.getDisplayStatus(rs.getLong(3)));
                channelTO.setImps(rs.getLong(6));
                channelTO.setUserCount(rs.getBigDecimal(5));
                channelTO.setReuse(rs.getInt(7));
                channelTO.setRate(rs.getBigDecimal(11));
                channelTO.setRateType(rs.getString(12));
                channelTO.setVisibility(ChannelVisibility.valueOf(rs.getString(4)));
                channelTO.setLastUse(rs.getDate(8));
                channelTO.setCurrencyId(rs.getLong(13));
                return (channelTO);
            }
        });
        int size = channels.size();
        if (channels.size() > MAX_SEARCH_RESULTS) {// function returns max 1001 channels
            channels.remove(MAX_SEARCH_RESULTS);
        }

        return new DetachedList<ChannelTO>(channels, size);
    }

    private List<Long> getIdsToSearch(String content) throws RemoteServiceException {
        ChannelSearchResult[] matchedByContent = searchContent(content);
        // ids to search in db
        List<Long> idsToSearch = new ArrayList<Long>(matchedByContent.length);

        for (ChannelSearchResult result : matchedByContent) {
            long id = result.channel_id;
            idsToSearch.add(id);
        }
        return idsToSearch;
    }

    private ChannelSearchResult[] searchContent(String content) throws RemoteServiceException {
        if (StringUtil.isPropertyEmpty(content)) {
            return new ChannelSearchResult[] {};
        }

        try {
            ChannelSearch channelSearchService = ServiceProvider.getInstance().getService(ChannelSearch.class);
            return channelSearchService.wsearch(content);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Remote service exception occured", e);
            throw new RemoteServiceException("Remote service exception", e);
        }
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('Channel', #channelId)")
    public ChannelStatsTO findChannelStatistics(Long channelId) {
        Channel channel = find(channelId);
        ChannelStatsTO stats = new ChannelStatsTO();

        boolean isDiscover = channel instanceof DiscoverChannel;
        boolean needTriggerStats = (channel instanceof TriggersChannel || channel instanceof KeywordChannel) &&
                (currentUserService.isInternal() || ChannelVisibility.PUB.equals(channel.getVisibility()) ||
                currentUserService.getAccountId().equals(channel.getAccount().getId()));

        fillActivity(channel.getId(), stats, needTriggerStats, !isDiscover);
        if (isDiscover || channel instanceof DeviceChannel) {
            return stats;
        }

        fillServing(channel.getId(), stats);

        if (channel instanceof BehavioralChannel || channel instanceof ExpressionChannel || channel instanceof AudienceChannel) {
            fillOverlap(channel, stats);
        }

        if (ChannelVisibility.PUB.equals(channel.getVisibility()) || ChannelVisibility.CMP.equals(channel.getVisibility())) {
            stats.setAlsoUsed(findChannelAlsoUsedStats(channel.getId()));
        }

        return stats;
    }

    private void fillOverlap(Channel channel, ChannelStatsTO stats) {
        List<ChannelOverlapTO> channelOverlap = loggingJdbcTemplate.query(
                " select * from channeloverlap.get_channel_overlap_stats(?::date, ?::bigint, ?::numeric)", new Object[] {
                        PostgreLocalDateUserType.FORMATTER.print(DateHelper.yesterday()),
                        channel.getId(),
                        MAX_CHANNEL_OVERLAP_STATS_ROWS
                }, new RowMapper<ChannelOverlapTO>() {

                    @Override
                    public ChannelOverlapTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new ChannelOverlapTO(rs.getLong(1), rs.getString(2),
                                rs.getBigDecimal(3), rs.getBigDecimal(4), rs.getBigDecimal(5), rs.getBigDecimal(6));
                    }
                });
        stats.setChannelOverlap(channelOverlap);
    }

    private void fillActivity(final Long channelId, final ChannelStatsTO stats, final boolean needTriggerStats, final boolean needUserActions) {

        List<ChannelActivityTO> result = loggingJdbcTemplate.query(
            needTriggerStats || needUserActions ?
                    "select * from statqueries.channel_activities_detailed(?::bigint)" :
                    "select * from statqueries.channel_activities(?::bigint)"
            , new Object[] { channelId }, new RowMapper<ChannelActivityTO>() {

                @Override
                public ChannelActivityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ChannelActivityTO to = new ChannelActivityTO();

                    to.setStatsDate(rs.getDate("sdate"));
                    to.setTotalUniques(rs.getBigDecimal("total_uniques"));
                    to.setActiveDailyUniques(rs.getBigDecimal("active_daily_uniques"));

                    if (needUserActions) {
                        to.setImpressions(rs.getBigDecimal("served_imps"));
                        to.setClicks(rs.getBigDecimal("served_clicks"));
                        to.setCtr(rs.getBigDecimal("served_ctr"));
                        to.setValue(rs.getBigDecimal("served_value"));
                        to.setEcpm(rs.getBigDecimal("served_ecpm"));
                    }

                    if (needTriggerStats) {
                        to.setUrls(rs.getBigDecimal("match_urls"));
                        to.setUrlsPercent(rs.getBigDecimal("match_urls_pc"));
                        to.setSearchKeywords(rs.getBigDecimal("match_search_keywords"));
                        to.setSearchKeywordsPercent(rs.getBigDecimal("match_search_keywords_pc"));
                        to.setPageKeywords(rs.getBigDecimal("match_page_keywords"));
                        to.setPageKeywordsPercent(rs.getBigDecimal("match_page_keywords_pc"));
                        to.setUrlKeywords(rs.getBigDecimal("match_url_keywords"));
                        to.setUrlKeywordsPercent(rs.getBigDecimal("match_url_keywords_pc"));
                        to.setTotalHits(rs.getBigDecimal("match_total"));
                    }
                    return to;
                }
            });
        stats.setActivity(result);
        stats.setTriggersStatPresent(needTriggerStats);
    }

    private void fillServing(Long channelId, ChannelStatsTO stats) {

        ServingStatsTO statsTO = loggingJdbcTemplate.queryForObject(
                "select * from statqueries.channel_servings(?::bigint)",
                new Object[] {channelId}, new RowMapper<ServingStatsTO>() {

                    @Override
                    public ServingStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ServingStatsTO to = new ServingStatsTO();

                        ChannelStatsRow statsRows = new ChannelStatsRow();
                        statsRows.setImps(rs.getBigDecimal("imps_all"));
                        statsRows.setUniques(rs.getBigDecimal("uniques_all"));
                        statsRows.setEcpm(rs.getBigDecimal("ecpm_all"));
                        statsRows.setValue(rs.getBigDecimal("values_all"));
                        to.setOpportunitiesToServe(statsRows);

                        statsRows = new ChannelStatsRow();
                        statsRows.setImps(rs.getBigDecimal("imps"));
                        statsRows.setUniques(rs.getBigDecimal("uniques"));
                        statsRows.setEcpm(rs.getBigDecimal("ecpm"));
                        statsRows.setValue(rs.getBigDecimal("values"));
                        to.setServed(statsRows);

                        statsRows = new ChannelStatsRow();
                        statsRows.setImps(rs.getBigDecimal("imps_other"));
                        statsRows.setUniques(rs.getBigDecimal("uniques_other"));
                        statsRows.setEcpm(rs.getBigDecimal("ecpm_other"));
                        statsRows.setValue(rs.getBigDecimal("values_other"));
                        to.setForosAdServed(statsRows);

                        if (currentUserService.isInternal()) {
                            statsRows = new ChannelStatsRow();
                            statsRows.setImps(rs.getBigDecimal("imps_op"));
                            statsRows.setUniques(rs.getBigDecimal("uniques_op"));
                            statsRows.setEcpm(rs.getBigDecimal("ecpm_op"));
                            statsRows.setValue(rs.getBigDecimal("values_op"));
                            to.setNonForosAdServed(statsRows);
                        }

                        to.setStatsDate(rs.getDate("sdate"));
                        return to;
                    }
                });
        stats.setServing(statsTO);
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('Channel', #channelId)")
    public ChannelPerformanceTO findChannelPerformanceStats(Long channelId) {
        return loggingJdbcTemplate.query(
                "select * from statqueries.channel_performance(?::bigint)", new Object[]{
                        channelId
                }, new ResultSetExtractor<ChannelPerformanceTO>() {

                    @Override
                    public ChannelPerformanceTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        ChannelPerformanceTO to = new ChannelPerformanceTO();
                        to.setLastUsed(rs.getDate("last_use"));
                        to.setLifetimeImps(rs.getLong("imps"));
                        if (currentUserService.isInternal()) {
                            to.setLifetimeClicks(rs.getLong("clicks"));
                            to.setLifetimeRevenue(rs.getBigDecimal("revenue"));
                        }
                        return to;
                    }
                });
    }

    @Override
    public boolean hasLiveAdvertisers(Long channelId) {
        ChannelLiveAssociationsStatsTO to = findChannelAssociationsStats(channelId);
        return to != null && to.getLiveAdvertisers() > 0L;
    }

    @Override
    public Collection<NamedTO> findAdvertisingChannels(String accountName, String countryCode) {
        Long accountId = findAdvertiserAccountIdByName(accountName);
        if (accountId == null) {
            return Collections.emptyList();
        }

        Query query = em.createQuery(
            "select new com.foros.session.NamedTO(c.id, c.name) from Channel c " +
                    "where c.account.id = :accountId " +
                    "  and c.country.countryCode = :countryCode " +
                    "  and c.namespace='A' " +
                    "  and c.status<>'D'"
            );
        query.setParameter("accountId", accountId);
        query.setParameter("countryCode", countryCode);

        //noinspection unchecked
        return query.getResultList();
    }

    @Override
    public NamedTO findAdvertisingChannel(String channelName, String accountName, String countryCode) {
        Long accountId = findAdvertiserAccountIdByName(accountName);
        if (accountId == null) {
            return null;
        }

        Query query = em.createQuery(
            "select new com.foros.session.NamedTO(c.id, c.name) from Channel c " +
                    "where c.account.id = :accountId " +
                    "  and c.country.countryCode = :countryCode " +
                    "  and c.namespace='A' " +
                    "  and c.name = :channelName " +
                    "  and c.status<>'D'"
            );
        query.setParameter("accountId", accountId);
        query.setParameter("countryCode", countryCode);
        query.setParameter("channelName", channelName);

        //noinspection unchecked
        List<NamedTO> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }

        throw new NonUniqueResultException();
    }

    private Long findAdvertiserAccountIdByName(String accountName) {
        List<Long> tmp = em.createQuery("SELECT a.id FROM Account a WHERE a.name = :name AND a.agency IS NULL", Long.class)
            .setParameter("name", accountName)
            .getResultList();
        if (tmp.isEmpty()) {
            return null;
        } else if (tmp.size() > 1) {
            throw new NonUniqueResultException();
        }
        return tmp.get(0);
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('Channel', #channelId)")
    public ChannelLiveAssociationsStatsTO findChannelAssociationsStats(Long channelId) {
        Channel channel = find(channelId);

        // only for PUB and CMP advertising channels
        if (channel.getNamespace() != ChannelNamespace.ADVERTISING || channel.getVisibility() == ChannelVisibility.PRI) {
            return null;
        }

        StringBuilder queryBuilder = new StringBuilder(
            "SELECT COUNT(DISTINCT(C.ACCOUNT_ID)), COUNT(DISTINCT(CCG.CAMPAIGN_ID)), COUNT(DISTINCT(CCG.CCG_ID)) " +
                    "FROM CAMPAIGNCREATIVEGROUP CCG, CAMPAIGN C, ACCOUNT A " +
                    "WHERE A.ACCOUNT_ID = C.ACCOUNT_ID AND C.CAMPAIGN_ID = CCG.CAMPAIGN_ID AND CCG.CHANNEL_ID = ? ");

        queryBuilder.append(" AND A.DISPLAY_STATUS_ID IN ");
        addLiveStatusIds(queryBuilder, Account.getAvailableDisplayStatuses());

        queryBuilder.append(" AND C.DISPLAY_STATUS_ID IN ");
        addLiveStatusIds(queryBuilder, Campaign.getAvailableDisplayStatuses());

        queryBuilder.append(" AND CCG.DISPLAY_STATUS_ID IN ");
        addLiveStatusIds(queryBuilder, CampaignCreativeGroup.getAvailableDisplayStatuses());

        Query query = em.createNativeQuery(queryBuilder.toString());
        query.setParameter(1, channelId);

        List result = query.getResultList();
        ChannelLiveAssociationsStatsTO to;
        if (result != null && result.size() > 0) {
            to = new ChannelLiveAssociationsStatsTO();
            Object[] raw = (Object[]) result.get(0);
            to.setLiveAdvertisers(((Number) raw[0]).longValue());
            to.setLiveCampaigns(((Number) raw[1]).longValue());
            to.setLiveCreativeGroups(((Number) raw[2]).longValue());
        } else {
            // no associations
            to = null;
        }

        return to;
    }

    private void addLiveStatusIds(StringBuilder queryBuilder, Collection<DisplayStatus> displayStatuses) {
        queryBuilder.append(" (");
        for (DisplayStatus displayStatus : displayStatuses) {
            if (DisplayStatus.Major.LIVE.equals(displayStatus.getMajor())
                    || DisplayStatus.Major.LIVE_NEED_ATT.equals(displayStatus.getMajor())) {
                queryBuilder.append(" ").append(displayStatus.getId()).append(",");
            }
        }
        queryBuilder = queryBuilder.delete(queryBuilder.length() - 1, queryBuilder.length());
        queryBuilder.append(") ");
    }

    private List<ChannelAlsoUsedTO> findChannelAlsoUsedStats(Long channelId) {
        ChannelsFilter filter = new ChannelsFilter("CH", userService);
        Query query = em.createNativeQuery(
            "SELECT CCG.CHANNEL_ID, CH.NAME, COUNT(DISTINCT C.ACCOUNT_ID) " +
                    "FROM CAMPAIGNCREATIVEGROUP CCG, CAMPAIGN C, CHANNEL CH " +
                    "WHERE " +
                    "  C.CAMPAIGN_ID = CCG.CAMPAIGN_ID " +
                    "  AND CCG.CHANNEL_ID = CH.CHANNEL_ID " +
                    "  AND CCG.CHANNEL_ID <> ? " +
                    "  AND " + filter.buildFilterClause(ChannelVisibilityCriteria.NON_PRIVATE) + " " +
                    "  AND C.ACCOUNT_ID IN ( " +
                    "    SELECT DISTINCT C.ACCOUNT_ID " +
                    "    FROM CAMPAIGNCREATIVEGROUP CCG, CAMPAIGN C " +
                    "    WHERE C.CAMPAIGN_ID = CCG.CAMPAIGN_ID AND CCG.CHANNEL_ID = ? ) " +
                    "GROUP BY " +
                    "  CCG.CHANNEL_ID, CH.NAME HAVING COUNT(DISTINCT C.ACCOUNT_ID) > 1 " +
                    "ORDER BY " +
                    "  COUNT(DISTINCT C.ACCOUNT_ID) DESC, CH.NAME ");

        query.setParameter(1, channelId);
        query.setParameter(2, channelId);
        query.setMaxResults(10);

        @SuppressWarnings({ "unchecked" })
        List<Object[]> sqlResult = query.getResultList();

        if (sqlResult == null || sqlResult.isEmpty()) {
            return null;
        }

        List<ChannelAlsoUsedTO> result = new LinkedList<ChannelAlsoUsedTO>();
        for (Object[] o : sqlResult) {
            long chId = ((Number) o[0]).longValue();
            String chName = o[1].toString();
            long count = ((Number) o[2]).longValue();

            ChannelAlsoUsedTO to = new ChannelAlsoUsedTO(chId, chName, count);
            result.add(to);
        }

        return result;
    }

    @Override
    public Channel find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        Channel channel = em.find(Channel.class, id);
        if (channel == null || channel instanceof BannedChannel) {
            throw new EntityNotFoundException("Entity with id=" + id + " not found");
        }

        return channel;
    }

    @Override
    public Channel findWithCategories(Long id) {
        Channel channel = find(id);
        channel.getCategories().size();
        return channel;
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('Channel', #id)")
    public Channel view(Long id) {
        return find(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "AdvertisingChannel.view", parameters = "find('Account', #accountId)")
    public Collection<ChannelTO> findChannelsForAccount(Long accountId) {
        Query query;
        if (userService.getMyUser().isDeletedObjectsVisible()) {
            query = em.createNamedQuery("Channel.getAllChannelsByAccount");
        } else {
            query = em.createNamedQuery("Channel.getNonDeletedChannelsByAccount");
        }
        return query
            .setParameter("accountId", accountId)
            .getResultList();
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('Channel', #channelId)")
    public Collection<CampaignAssociationTO> findCampaignAssociations(Long channelId, LocalDate dateFrom, LocalDate dateTo) {

        return loggingJdbcTemplate.query("select * from statqueries.channel_campaign_associations(?::bigint, ?::integer, ?::date, ?::date)", new Object[] {
                channelId,
                currentUserService.getUserId(),
                dateFrom,
                dateTo
        }, new RowMapper<CampaignAssociationTO>() {
            @Override
            public CampaignAssociationTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountTO advertiserTO = getAdvertiserTO(rs);
                return new CampaignAssociationTO.Builder()
                        .advertiser(advertiserTO)
                        .account(getAccountTO(rs, advertiserTO))
                        .group(getGroupTO(rs))
                        .campaign(getCampaignTO(rs))
                                // clicks, imps, ctr 19-21
                        .clicks(rs.getLong(19))
                        .impressions(rs.getLong(20))
                        .ctr(rs.getDouble(21))
                        .build();
            }

            private AccountTO getAccountTO(ResultSet rs, AccountTO advertiser) throws SQLException {
                AccountTO account;
                // 14-18 agency (if any)
                // agn_account_id numeric,
                // agn_name character varying,
                // agn_display_status_id numeric,
                // agn_status character,
                // agn_flags numeric,
                Long accountId = rs.getObject(14) == null ? null : rs.getLong(14);
                if (accountId == null) {
                    account = advertiser;
                } else {
                    String accountName = rs.getString(15);
                    Long accountDisplayStatusId = rs.getLong(16);
                    Character accountStatus = rs.getString(17).charAt(0);
                    Long flags = rs.getLong(18);
                    account = new AccountTO(accountId, accountName, accountStatus, flags);
                    account.setDisplayStatus(Account.getDisplayStatus(accountDisplayStatusId));
                }
                return account;
            }

            private AccountTO getAdvertiserTO(ResultSet rs) throws SQLException {
                // 9-13 advertiser
                // adv_account_id numeric,
                // adv_name character varying,
                // adv_display_status_id numeric,
                // adv_status character,
                // adv_flags numeric,

                Long advertiserId = rs.getLong(9);
                String advertiserName = rs.getString(10);
                Long advertiserDisplayStatusId = rs.getLong(11);
                Character advertiserStatus = rs.getString(12).charAt(0);
                Long flags = rs.getLong(13);
                AccountTO advertiser = new AccountTO(advertiserId, advertiserName, advertiserStatus, flags);
                advertiser.setDisplayStatus(Account.getDisplayStatus(advertiserDisplayStatusId));
                return advertiser;
            }

            private DisplayStatusEntityTO getCampaignTO(ResultSet rs) throws SQLException {
                // 5-8 campaign
                // campaign_id numeric,
                // campaign_name character varying,
                // campaign_display_status_id numeric,
                // campaign_status character,
                Long campaignId = rs.getLong(5);
                String campaignName = rs.getString(6);
                Long campaignDisplayStatusId = rs.getLong(7);
                Character campaignStatus = rs.getString(8).charAt(0);
                DisplayStatusEntityTO campaign = new DisplayStatusEntityTO();
                campaign.setId(campaignId);
                campaign.setName(campaignName);
                campaign.setDisplayStatus(Campaign.getDisplayStatus(campaignDisplayStatusId));
                campaign.setStatus(Status.valueOf(campaignStatus));
                return campaign;
            }

            private DisplayStatusEntityTO getGroupTO(ResultSet rs) throws SQLException {
                // 1-4 ccg
                // ccg_id numeric,
                // ccg_name character varying,
                // ccg_display_status_id numeric,
                // ccg_status character,
                Long ccgId = rs.getLong(1);
                String ccgName = rs.getString(2);
                Long ccgDisplayStatusId = rs.getLong(3);
                Character ccgStatus = rs.getString(4).charAt(0);
                DisplayStatusEntityTO ccg = new DisplayStatusEntityTO();
                ccg.setId(ccgId);
                ccg.setName(ccgName);
                ccg.setDisplayStatus(CampaignCreativeGroup.getDisplayStatus(ccgDisplayStatusId));
                ccg.setStatus(Status.valueOf(ccgStatus));
                return ccg;
            }
        });
    }

    @Override
    @Restrict(restriction = "Channel.findBehavioralDiscoverChannels", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findBehavioralDiscoverChannels(Long accountId, String query, int firstResult, int maxResults) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new com.foros.session.EntityTO(c.id, c.name, c.status) from Channel c where c.account.id = :accountId and ");
        sql.append(" upper(c.name) like :query ESCAPE '\\' and");

        List<AccountRole> roles = new ArrayList<AccountRole>(Arrays.asList(AccountRole.values()));
        CollectionUtils.filter(roles, new Filter<AccountRole>() {
            @Override
            public boolean accept(AccountRole element) {
                return advertisingRestrictions.canView(element);
            }
        });

        if (roles.isEmpty() && !discoverRestrictions.canView()) {
            return Collections.emptyList();
        }

        sql.append(" ( ");

        if (!roles.isEmpty()) {
            sql.append(" (c.class = BehavioralChannel and ((c.account.role in :accRoles )");
            sql.append(" or c.visibility = 'PUB')) ");
        } else {
            sql.append(" 1 = 2 ");
        }

        sql.append(" or ");

        // discover channels
        if (discoverRestrictions.canView()) {
            sql.append(" c.class = DiscoverChannel ");
        } else {
            sql.append(" 1 = 2 ");
        }
        sql.append(" )");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            sql.append(" and c.status <> 'D'");
        }

        sql.append(" order by ");
        sql.append("case status when 'I' then 2 ");
        sql.append("when 'D' then 3 ");
        sql.append("else 1 end ");
        sql.append(", upper(c.name) ");

        QueryWrapper<EntityTO> q = new JpaQueryWrapper<EntityTO>(em, sql.toString())
            .setParameter("accountId", accountId)
            .setParameter("query", SQLUtil.getLikeEscape(query));
        if (!roles.isEmpty()) {
            q.setParameter("accRoles", roles);
        }
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findAdvertisingByAccountAndStatuses(Long accountId, DisplayStatus[] displayStatuses) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new com.foros.session.EntityTO(c.id, c.name, c.status) from Channel c ");
        sql.append("where c.namespace = com.foros.model.channel.ChannelNamespace.ADVERTISING ");
        sql.append("and c.account.id = :accountId ");

        if (displayStatuses != null && displayStatuses.length > 0) {
            sql.append(" and c.displayStatusId  in ( ");

            for (DisplayStatus displayStatus : displayStatuses) {
                sql.append(" ").append(displayStatus.getId()).append(",");
            }
            sql = sql.delete(sql.length() - 1, sql.length());
            sql.append(" ) ");
        }
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            sql.append(" and c.status <> 'D' ");
        }

        sql.append("order by c.name");

        QueryWrapper<EntityTO> q = new JpaQueryWrapper<EntityTO>(em, sql.toString());
        q.setParameter("accountId", accountId);

        return q.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findAdvertisingChannelsByAccount(Long accountId) {
        return new NamedQueryWrapper<EntityTO>(em, "Channel.getAdvertisingChannelsByAccount")
            .setParameter("accountId", accountId)
            .getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findSupersededChannelsByAccountAndCountry(Long accountId, String countryCode,
            Long selectedId, Long selfId,
            String name, int maxResults) {

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(" NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Channel c ");
        sql.append(" WHERE c.account.id = :accountId ");
        sql.append(" AND c.namespace = com.foros.model.channel.ChannelNamespace.ADVERTISING ");
        sql.append(" AND c.country.countryCode = :countryCode ");
        sql.append(" AND UPPER(c.name) LIKE UPPER(:name) ESCAPE '\\' ");
        sql.append(" AND (c.status <> 'D' ");
        if (selectedId != null) {
            sql.append(" OR c.id = :selectedId ");
        }
        sql.append(") AND c.id != :selfId ");
        sql.append(" ORDER BY UPPER(c.name)");

        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em, sql.toString())
            .setParameter("accountId", accountId)
            .setParameter("countryCode", countryCode)
            .setLikeParameter("name", name)
            .setParameter("selfId", selfId);

        if (selectedId != null) {
            query.setParameter("selectedId", selectedId);
        }

        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<ChannelReportTO> findChannelsByAccountAndType(Long accountId, String name,
            List<Class<? extends Channel>> channelClasses,
            int maxResults) {
        return findChannelsByAccountTypeAndVisibility(accountId, name, channelClasses, null, maxResults);
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<ChannelReportTO> findChannelsByAccountTypeAndVisibility(Long accountId, String name,
            List<Class<? extends Channel>> channelClasses,
            ChannelVisibility visibility,
            int maxResults) {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(" NEW com.foros.session.channel.ChannelReportTO(c.id, c.name, c.status, c.class) FROM Channel c ");
        sql.append(" WHERE c.account.id=:accountId ");
        sql.append(visibility != null ? " AND c.visibility = :visibility " : "");
        sql.append(" AND UPPER(c.name) LIKE UPPER(:name) ESCAPE '\\' ");
        sql.append(" AND c.class IN :classes ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            sql.append(" AND c.status <> 'D' ");
        }
        sql.append(" ORDER BY CASE c.status WHEN 'D' THEN 2 ELSE CASE c.status WHEN 'I' THEN 1 ELSE 0 END END, UPPER(c.name)");

        QueryWrapper<ChannelReportTO> query = new JpaQueryWrapper<ChannelReportTO>(em, sql.toString())
            .setParameter("accountId", accountId)
            .setLikeParameter("name", name)
            .setPrimitiveArrayParameter("classes", channelClasses);

        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        if (visibility != null) {
            query.setParameter("visibility", visibility);
        }

        return query.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findByAccountAndStatus(Long accountId, DisplayStatus displayStatus,
            ChannelVisibilityCriteria visibilityCriteria,
            String countryCode, String name, int maxResults) {
        ChannelsFilter filter = new ChannelsFilter("c", "c.account", userService);
        filter.setUseJpa(true);
        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em,
            new ConditionStringBuilder(
                "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM BehavioralChannel c ")
                .append(" WHERE ")
                .append(" c.account.id = :accountId AND c.class != 'L' ")
                .append(" AND c.country.countryCode = :countryCode ")
                .append(displayStatus != null, " AND c.displayStatusId = :displayStatus")
                .append(" AND " + filter.buildFilterClause(visibilityCriteria))
                .append(" AND UPPER(c.name) LIKE UPPER(:name) ESCAPE '\\'")
                .append("  ORDER BY upper(c.name)")
                .toString())
            .setParameter("accountId", accountId)
            .setParameter("countryCode", countryCode)
            .oneIf(displayStatus != null).setParameter("displayStatus", displayStatus != null ? displayStatus.getId() : null)
            .setLikeParameter("name", name)
            .setMaxResults(maxResults);

        return query.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findDiscoverByAccountAndStatus(Long accountId, Long discoverChannelListId,
            DisplayStatus displayStatus, String countryCode,
            String name, int maxResults) {
        ChannelsFilter filter = new ChannelsFilter("c", "c.account", userService);
        filter.setUseJpa(true);
        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em,
            new ConditionStringBuilder(
                "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM DiscoverChannel c ")
                .append(" WHERE ")
                .append(" c.account.id = :accountId ")
                .append(" AND c.country.countryCode = :countryCode ")
                .append(displayStatus != null, " AND c.displayStatusId = :displayStatus")
                .append(discoverChannelListId != null, " AND c.channelList.id = :discoverChannelListId")
                .append(" AND UPPER(c.name) LIKE UPPER(:name) ESCAPE '\\'")
                .append("  ORDER BY upper(c.name)")
                .toString())
            .setParameter("accountId", accountId)
            .setParameter("countryCode", countryCode)
            .oneIf(displayStatus != null).setParameter("displayStatus", displayStatus != null ? displayStatus.getId() : null)
            .oneIf(discoverChannelListId != null).setParameter("discoverChannelListId", discoverChannelListId)
            .setLikeParameter("name", name)
            .setMaxResults(maxResults);

        return query.getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findDiscoverListsByAccount(Long accountId, String countryCode) {
        ChannelsFilter filter = new ChannelsFilter("c", "c.account", userService);
        filter.setUseJpa(true);
        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em,
            new ConditionStringBuilder(
                "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM DiscoverChannelList c ")
                .append(" WHERE ")
                .append(" c.account.id = :accountId ")
                .append(" AND c.country.countryCode = :countryCode ")
                .append(" ORDER BY upper(c.name)")
                .toString())
            .setParameter("accountId", accountId)
            .setParameter("countryCode", countryCode);

        return query.getResultList();
    }

    private AdvertisingChannelQuery createRestrictedAdvertisingQuery() {
        return createRestrictedAdvertisingQuery(new AdvertisingChannelQueryImpl());
    }

    private AdvertisingChannelQuery createRestrictedAdvertisingQuery(AdvertisingChannelQuery channelQuery) {
        if (currentUserService.isInternal()) {
            List<AccountRole> roles = new ArrayList<AccountRole>(Arrays.asList(AccountRole.values()));
            CollectionUtils.filter(roles, new Filter<AccountRole>() {
                @Override
                public boolean accept(AccountRole element) {
                    return advertisingRestrictions.canView(element);
                }
            });
            if (roles.isEmpty()) {
                throw new SecurityException("Can't view channels");
            }

            channelQuery.byAccountRolesOrPublic(roles);

            boolean isAccountManager = currentUserService.isAccountManager();
            boolean isInternalWithRestrictedAccess = currentUserService.isInternalWithRestrictedAccess();

            if (isAccountManager) {
                channelQuery.managedOrPublic(currentUserService.getUserId(), currentUserService.getAccountId());
            }

            if (isInternalWithRestrictedAccess) {
                channelQuery.restrictedByAccountIdsOrPublic(currentUserService.getAccessAccountIds());
            }
        } else {
            channelQuery.notDeleted();
            channelQuery.ownedOrPublic(currentUserService.getAccountId());
        }
        return channelQuery;
    }

    private DiscoverChannelQuery createDiscoverChannelQuery() {
        DiscoverChannelQueryImpl discoverChannelQueryImpl = new DiscoverChannelQueryImpl();
        if (currentUserService.isInternalWithRestrictedAccess()) {
            discoverChannelQueryImpl.restrictByInternalAccountIds(currentUserService.getAccessAccountIds());
        }
        return discoverChannelQueryImpl;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view")
    public PartialList<ChannelTO> search(
            String name, Long accountId, String countryCode,
            AccountSearchTestOption testOption,
            AdvertisingChannelType[] types,
            DisplayStatus[] displayStatuses,
            ChannelVisibilityCriteria visibility,
            String keyword,
            Long categoryChannelId,
            int from, int count) throws RemoteServiceException {

        List<Long> matchedIds = getChannelsMatchedByKeyword(keyword);
        if (matchedIds != null && matchedIds.isEmpty()) {
            // no matched keywords, result is empty
            return PartialList.emptyList();
        }

        AdvertisingChannelQuery query = createRestrictedAdvertisingQuery()
            .account(accountId)
            .nameWithEscape(name)
            .country(countryCode)
            .type(Arrays.asList(types))
            .visibility(visibility)
            .displayStatus(displayStatuses)
            .matchedIds(matchedIds)
            .hasCategoryChannel(categoryChannelId)
            .orderByName()
            .asTO();
        applyTestOption(query, testOption);

        final PartialList<ChannelTO> list = query.executor(executorService).partialList(from, count);

        return list;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view")
    public Collection<Channel> searchForExport(String name, Long accountId, String countryCode,
            AccountSearchTestOption testOption, AdvertisingChannelType[] types, DisplayStatus[] displayStatuses,
            ChannelVisibilityCriteria visibility, String keyword, int maxResultsCount)
            throws RemoteServiceException, TooManyTriggersException {

        List<Long> matchedIds = getChannelsMatchedByKeyword(keyword);
        if (matchedIds != null && matchedIds.isEmpty()) {
            // no matched keywords, result is empty
            return Collections.emptyList();
        }

        // CMP channels must not be exported
        if (visibility == ChannelVisibilityCriteria.CMP || visibility == ChannelVisibilityCriteria.NONE) {
            return Collections.emptyList();
        } else if (visibility == ChannelVisibilityCriteria.ALL) {
            visibility = ChannelVisibilityCriteria.NON_CPM;
        } else if (visibility == ChannelVisibilityCriteria.NON_PRIVATE) {
            visibility = ChannelVisibilityCriteria.PUBLIC;
        }

        AdvertisingChannelQuery query = new AdvertisingChannelQueryImpl() {
            @Override
            protected ResultTransformer createTOTransformer() {
                return new PartialAdvertisingChannelTransformer();
            }

            @Override
            protected ProjectionList createDefaultTOProjections() {
                ProjectionList result = super.createDefaultTOProjections();
                result.add(Projections.property("expression").as("expression"));
                return result;
            }
        };
        query = createRestrictedAdvertisingQuery(query)
            .account(accountId)
            .nameWithEscape(name)
            .country(countryCode)
            .type(Arrays.asList(types))
            .visibility(visibility)
            .displayStatus(displayStatuses)
            .matchedIds(matchedIds)
            .orderByName()
            .asTO();
        applyTestOption(query, testOption);

        Collection<Channel> result = query.executor(executorService).list();
        em.clear();

        Map<Long, BehavioralChannel> behavioralChannelsMap = new HashMap<>(result.size());
        List<ExpressionChannel> expressionChannels = new ArrayList<>(result.size());
        for (Channel channel : result) {
            if (channel instanceof ExpressionChannel) {
                expressionChannels.add((ExpressionChannel) channel);
            } else {
                behavioralChannelsMap.put(channel.getId(), (BehavioralChannel) channel);
            }
        }

        if (!behavioralChannelsMap.isEmpty()) {
            bulkChannelToolsService.setTriggers(behavioralChannelsMap);
        }
        if (!expressionChannels.isEmpty()) {
            expressionService.convertToHumanReadable(expressionChannels);
        }

        return result;
    }

    private AdvertisingChannelQuery applyTestOption(AdvertisingChannelQuery query, AccountSearchTestOption testOption) {
        // Test option can be EXCLUDE, ONLY_TEST or INCLUDE. TEST_FLAG is 0x01.
        if (AccountSearchTestOption.EXCLUDE.equals(testOption)) {
            query.excludeTestAccounts();
        }
        if (AccountSearchTestOption.ONLY_TEST.equals(testOption)) {
            query.onlyTestAccounts();
        }

        return query;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.view")
    @Validate(validation = "ChannelSelector.advertising", parameters = "#channelSelector")
    public Result<Channel> get(ChannelSelector channelSelector) throws RemoteServiceException {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(channelSelector.getChannelIds())
                && CollectionUtils.isNullOrEmpty(channelSelector.getAccountIds())
                && StringUtil.isPropertyEmpty(channelSelector.getName())
                && StringUtil.isPropertyEmpty(channelSelector.getCountryCode())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.advertisingChannel");
        }

        List<Long> channelIds;
        if (!StringUtil.isPropertyEmpty(channelSelector.getContent())) {
            channelIds = getIdsToSearch(channelSelector.getContent());
            if (CollectionUtils.isNullOrEmpty(channelIds)) {
                return new Result<Channel>(PartialList.<Channel> emptyList());
            }
            if (!CollectionUtils.isNullOrEmpty(channelSelector.getChannelIds())) {
                channelIds.retainAll(channelSelector.getChannelIds());
            }
        } else {
            channelIds = channelSelector.getChannelIds();
        }

        PartialList<Channel> channels = createRestrictedAdvertisingQuery()
            .channels(channelIds)
            .accounts(channelSelector.getAccountIds())
            .nameWithEscape(channelSelector.getName())
            .country(channelSelector.getCountryCode())
            .visibility(channelSelector.getVisibility())
            .type(channelSelector.getTypes())
            .orderByName()
            .asBean()
            .executor(executorService)
            .partialList(channelSelector.getPaging());

        em.clear();

        channels = fillTriggersAndShrink(channels);

        for (Channel channel : channels) {
            if (channel instanceof BehavioralChannel) {
                BehavioralChannel behavioralChannel = (BehavioralChannel) channel;

                if (currentUserService.isExternal()) {
                    behavioralChannel.setCategories(null);
                }

                for (BehavioralParameters parameter : behavioralChannel.getBehavioralParameters()) {
                    parameter.setVersion(null);
                }
            }

            if (!advertisingChannelRestrictions.canViewContent(channel)) {
                if (channel instanceof BehavioralChannel) {
                    BehavioralChannel behavioralChannel = (BehavioralChannel) channel;
                    behavioralChannel.setBehavioralParameters(null);
                }
                if (channel instanceof ExpressionChannel) {
                    ExpressionChannel expressionChannel = (ExpressionChannel) channel;
                    expressionChannel.setExpression(null);
                }
            }
        }

        return new Result<>(channels);
    }

    private PartialList<Channel> fillTriggersAndShrink(PartialList<Channel> channels) {
        List<Long> behavioralChannelIds = new ArrayList<>(channels.size());
        for (Channel channel : channels) {
            if (channel instanceof BehavioralChannel) {
                behavioralChannelIds.add(channel.getId());
            }
        }
        if (!behavioralChannelIds.isEmpty()) {
            Map<Long, Set<ChannelTrigger>> triggersByChannelId =
                    triggerService.getTriggersByChannelIds(behavioralChannelIds, true);
            for (int i = 0; i < channels.size(); i++) {
                Channel channel = channels.get(i);
                if (channel instanceof BehavioralChannel) {
                    Set<ChannelTrigger> triggers = triggersByChannelId.get(channel.getId());
                    if (triggers != null) {
                        ((BehavioralChannel) channel).resetTriggers(triggers);
                    } else {
                        List<Channel> list = channels.subList(0, i);
                        Paging paging = new Paging(channels.getFrom(), list.size());
                        return new PartialList<>(channels.getTotal(), paging, list);
                    }
                }
            }
        }
        return channels;
    }

    @Override
    @Restrict(restriction = "GeoChannel.view")
    @Validate(validation = "ChannelSelector.geo", parameters = "#channelSelector")
    public Result<ApiGeoChannelTO> getGeoChannels(GeoChannelSelector channelSelector) {
        PartialList<ApiGeoChannelTO> channels = (new GeoChannelListsQueryImpl())
                .channels(channelSelector.getChannelIds())
                .countries(channelSelector.getCountryCodes())
                .parentChannels(channelSelector.getParentChannelIds())
                .geoTypes(channelSelector.getGeoTypes())
                .asBean()
                .orderByName()
                .executor(executorService)
                .noCount()
                .partialList(channelSelector.getPaging());

        return new Result<>(channels);
    }

    @Override
    @Restrict(restriction = "DeviceChannel.get")
    @Validate(validation = "ChannelSelector.device", parameters = "#channelSelector")
    public Result<ApiDeviceChannelTO> getDeviceChannels(DeviceChannelSelector channelSelector) {
        PartialList<ApiDeviceChannelTO> channels = (new DeviceChannelListsQueryImpl())
                .channels(channelSelector.getChannelIds())
                .statuses(channelSelector.getChannelStatuses())
                .parentChannels(channelSelector.getParentChannelIds())
                .orderByName()
                .executor(executorService)
                .noCount()
                .partialList(channelSelector.getPaging());

        return new Result<>(channels);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.view")
    @Validate(validation = "ChannelSelector.discover", parameters = "#channelSelector")
    public Result<DiscoverChannel> getDiscover(DiscoverChannelSelector channelSelector) {
        if (CollectionUtils.isNullOrEmpty(channelSelector.getAccountIds())
                && CollectionUtils.isNullOrEmpty(channelSelector.getChannelIds())
                && StringUtil.isPropertyEmpty(channelSelector.getName())
                && StringUtil.isPropertyEmpty(channelSelector.getCountryCode())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.discoverChannel");
        }

        PartialList<DiscoverChannel> channels = createDiscoverChannelQuery()
            .accounts(channelSelector.getAccountIds())
            .channels(channelSelector.getChannelIds())
            .nameWithEscape(channelSelector.getName())
            .country(channelSelector.getCountryCode())
            .asBean()
            .executor(executorService)
            .partialList(channelSelector.getPaging());

        // pre-process
        Map<Long, Set<ChannelTrigger>> triggersByChannelId = triggerService.getTriggersByChannelIds(channelSelector.getChannelIds(), false);
        for (DiscoverChannel channel : channels) {
            channel.setNullUrlKeywords();
            Set<ChannelTrigger> triggers = triggersByChannelId.get(channel.getId());
            channel.resetTriggers(triggers);
        }

        return new Result<>(channels);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.list")
    public PartialList<DiscoverChannelTO> searchDiscover(String name, Long accountId, String countryCode, String language,
            int from, int count, String keyword, DisplayStatus... displayStatuses) throws RemoteServiceException {

        List<Long> matchedIds = getChannelsMatchedByKeyword(keyword);
        if (matchedIds != null && matchedIds.isEmpty()) {
            // no matched keywords, result is empty
            return PartialList.emptyList();
        }

        List<Long> displayStatusIds = null;
        if (displayStatuses != null) {
            displayStatusIds = new ArrayList<Long>();
            for (DisplayStatus displayStatuse : displayStatuses) {
                displayStatusIds.add(displayStatuse == null ? null : displayStatuse.getId());
            }
        }

        int total = searchDiscoverRowsCount(name, accountId, countryCode, language, matchedIds, displayStatusIds);
        if (total == 0) {
            // no matched rows, result is empty
            return PartialList.emptyList();
        }

        List<DiscoverChannelTO> result = searchDiscoverImpl(name, accountId, countryCode, language, from, count, matchedIds, displayStatusIds);
        return new PartialList<DiscoverChannelTO>(total, from, result);
    }

    private int searchDiscoverRowsCount(
            String name, Long accountId, String countryCode, String language, List<Long> matchedIds, List<Long> displayStatusIds) {
        return loggingJdbcTemplate.withAuthContext()
                .queryForObject("select * from statqueries.find_discover_channels_cnt(?, ?, ?, ?, ?, ?) ",
            new Object[] {
                    name == null ? "" : name,
                    accountId,
                    loggingJdbcTemplate.createArray("bigint", matchedIds),
                    countryCode == null ? "" : countryCode,
                    language == null ? "" : language,
                    loggingJdbcTemplate.createArray("int", displayStatusIds)
            }, new int[] {
                    Types.VARCHAR,
                    Types.INTEGER,
                    Types.ARRAY,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.ARRAY
            }, Integer.class);
    }

    private List<DiscoverChannelTO> searchDiscoverImpl(String name, Long accountId, String countryCode, String language,
            int from, int count, List<Long> matchedIds, List<Long> displayStatusIds) {
        return loggingJdbcTemplate.withAuthContext()
                .query("select * from statqueries.find_discover_channels(?, ?, ?, ?, ?, ?, ?, ?)",
            new Object[] {
                    name == null ? "" : name,
                    accountId,
                    loggingJdbcTemplate.createArray("bigint", matchedIds),
                    countryCode == null ? "" : countryCode,
                    language == null ? "" : language,
                    loggingJdbcTemplate.createArray("int", displayStatusIds),
                    from,
                    count
            },
            new int[] {
                    Types.VARCHAR,
                    Types.INTEGER,
                    Types.ARRAY,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.ARRAY,
                    Types.INTEGER,
                    Types.INTEGER
            }, new RowMapper<DiscoverChannelTO>() {
                @Override
                public DiscoverChannelTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new DiscoverChannelTO(
                        rs.getLong("channel_id"),
                        rs.getString("channel_name"),
                        rs.getLong("account_id"),
                        rs.getString("account_name"),
                        rs.getLong("account_display_status_id"),
                        rs.getLong("channel_display_status_id"),
                        rs.getLong("total_news"),
                        rs.getLong("daily_news"),
                        rs.getString("discover_query"),
                        rs.getString("country_code")
                    );
                }
            });
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.list")
    public PartialList<DiscoverChannelListTO> searchDiscoverLists(
            String name, Long accountId, String countryCode, String language,
            int from, int count, String keyword, DisplayStatus... displayStatuses) throws RemoteServiceException {

        List<Long> matchedIds = getChannelsMatchedByKeyword(keyword);
        if (matchedIds != null && matchedIds.isEmpty()) {
            // no matched keywords, result is empty
            return PartialList.emptyList();
        }

        DiscoverChannelListsQueryImpl discoverChannelListsQueryImpl = new DiscoverChannelListsQueryImpl();
        if (currentUserService.isInternalWithRestrictedAccess()) {
            discoverChannelListsQueryImpl.restrictByAccountIds(currentUserService.getAccessAccountIds());
        }
        return discoverChannelListsQueryImpl
            .nameWithEscape(name)
            .account(accountId)
            .country(countryCode)
            .language(language)
            .displayStatus(displayStatuses)
            .matchedIds(matchedIds)
            .orderByName()
            .asTO()
            .executor(executorService)
            .partialList(from, count);
    }

    @Override
    public PartialList<GeoChannelTO> searchGeoChannels(String name, String countryCode, int from, int count) {
        GeoChannelListsQuery query = new GeoChannelListsQueryImpl()
                .nameWithEscape(name)
                .country(countryCode)
                .orderByName()
                .notAddress();
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query = query.notDeleted();
        }

        return query.asTO()
                .executor(executorService)
                .partialList(from, count);
    }

    @Override
    public int getPendingAdvertisingChannelsCount() {
        return jdbcTemplate.withAuthContext().queryForObject(
            "select entityqueries.get_pending_adv_channels_count(?::bool)",
            Integer.class,
            currentUserService.getUser().isDeletedObjectsVisible()
            );
    }

    @Override
    public int getPendingDiscoverChannelsCount() {
        return jdbcTemplate.withAuthContext().queryForObject(
            "select entityqueries.get_pending_disc_channels_count(?::bool)",
            Integer.class,
            currentUserService.getUser().isDeletedObjectsVisible()
            );
    }

    private List<Long> getChannelsMatchedByKeyword(String keyword) throws RemoteServiceException {
        List<Long> matchedChannelIds = null;

        if (StringUtil.isPropertyNotEmpty(keyword)) {
            try {
                matchedChannelIds = matchChannels(keyword);
            } catch (RemoteServiceException e) {
                logger.log(Level.SEVERE, "Channel search failed with keyword '" + keyword + "'", e);
                throw e;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Channel search failed with keyword '" + keyword + "'", e);
                throw new RuntimeException(e);
            }

            if (matchedChannelIds.isEmpty()) {
                return new ArrayList<Long>();
            }
        }

        return matchedChannelIds;
    }

    private List<Long> matchChannels(String keyword) throws RemoteServiceException {
        List<Long> matchedChannelIds = new ArrayList<Long>();
        ChannelSearch channelSearchService;
        ChannelSearchResult[] results;

        try {
            channelSearchService = ServiceProvider.getInstance().getService(ChannelSearch.class);
            results = channelSearchService.wsearch(keyword);
            if (results.length > 0) {
                for (ChannelSearchResult result : results) {
                    matchedChannelIds.add(result.channel_id);
                }
            }
        } catch (RemoteServiceException e) {
            throw e;
        } catch (Throwable e) {
            throw new RemoteServiceException("remote service exception", e);
        }

        return matchedChannelIds;
    }

    @Override
    public List<TriggersChannel> findMatchedChannelsByIds(Set<Long> ids) {
        if (!ids.isEmpty()) {
            return new JpaQueryWrapper<TriggersChannel>(em, "SELECT c FROM Channel c WHERE c.id in :ids AND c.class in :classes ORDER BY UPPER(c.name)")
                .setPrimitiveArrayParameter("ids", ids)
                .setPrimitiveArrayParameter("classes", Arrays.asList(BehavioralChannel.class, DiscoverChannel.class, KeywordChannel.class))
                .getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public PopulatedMatchInfo match(String url, String keywords) throws RemoteServiceException {
        WMatchInfo matchResults;
        Integer countMaxChannels = configService.get(ConfigParameters.KWM_TOOL_MAX_CHANNELS);
        try {
            ChannelSearch channelSearchService = ServiceProvider.getInstance().getService(ChannelSearch.class);
            matchResults = channelSearchService.wmatch(url == null ? "" : url, keywords == null ? "" : keywords, countMaxChannels + 1);
        } catch (RemoteServiceException e) {
            throw e;
        } catch (Throwable t) {
            throw new RemoteServiceException("Failed to invoke remote service method match(String,String,Integer)", t);
        }

        if (matchResults == null) {
            return null;
        }

        Set<Long> channelIds = new HashSet<Long>();
        Set<Long> ccgIds = new HashSet<Long>();
        Set<Long> channelTriggerIds = new HashSet<Long>();

        boolean isNumberOfChannelsExceeded = false;
        boolean isNumberOfDiscoverChannelsExceeded = false;
        if (matchResults.channels.length > countMaxChannels) {
            matchResults.channels = Arrays.copyOf(matchResults.channels, countMaxChannels);
            isNumberOfChannelsExceeded = true;
        }

        if (matchResults.discover_channels.length > countMaxChannels) {
            matchResults.discover_channels = Arrays.copyOf(matchResults.discover_channels, countMaxChannels);
            isNumberOfDiscoverChannelsExceeded = true;
        }

        fillSets(matchResults, channelIds, ccgIds, channelTriggerIds);

        Map<Long, NamedTO> channelNames = getChannelNames(channelIds);
        Map<Long, NamedTO> ccgNames = getCcgNames(ccgIds);
        Map<Long, PopulatedTriggerInfo> triggerNames = getTriggerInfos(channelTriggerIds);

        List<PopulatedBehavioralChannelMatchInfo> behavioralChannelInfos = getBehavioralChannelInfos(matchResults,
            channelNames,
            ccgNames, triggerNames);

        List<PopulatedDiscoverChannelMatchInfo> discoverChannelInfos = getDiscoverChannelInfos(matchResults,
            channelNames,
            triggerNames);

        return new PopulatedMatchInfo(behavioralChannelInfos, discoverChannelInfos, countMaxChannels, isNumberOfChannelsExceeded, isNumberOfDiscoverChannelsExceeded);
    }

    private void fillSets(WMatchInfo matchResults, Set<Long> channelIds, Set<Long> ccgIds, Set<Long> channelTriggerIds) {
        for (ChannelMatchInfo channel : matchResults.channels) {
            channelIds.add(channel.channel_id);
            ccgIds.addAll(Arrays.asList(ArrayUtils.toObject(channel.ccgs)));
            channelTriggerIds.addAll(Arrays.asList(ArrayUtils.toObject(channel.triggers)));
        }

        for (WDiscoverChannelMatchInfo channel : matchResults.discover_channels) {
            channelIds.add(channel.channel_id);
            channelTriggerIds.addAll(Arrays.asList(ArrayUtils.toObject(channel.triggers)));
        }
    }

    private List<PopulatedBehavioralChannelMatchInfo> getBehavioralChannelInfos(WMatchInfo matchResults,
            Map<Long, NamedTO> channelNames, Map<Long, NamedTO> ccgNames, Map<Long, PopulatedTriggerInfo> triggerNames) {
        List<PopulatedBehavioralChannelMatchInfo> behavioralChannelInfos = new ArrayList<PopulatedBehavioralChannelMatchInfo>(matchResults.channels.length);

        for (ChannelMatchInfo channel : matchResults.channels) {
            List<PopulatedTriggerInfo> triggers = new ArrayList<PopulatedTriggerInfo>(channel.triggers.length);

            for (long channelTriggerId : channel.triggers) {
                PopulatedTriggerInfo triggerInfo = triggerNames.get(channelTriggerId);
                if (triggerInfo != null) {
                    triggers.add(triggerInfo);
                }
            }

            List<NamedTO> ccgs = new ArrayList<NamedTO>(channel.ccgs.length);

            for (long ccgId : channel.ccgs) {
                ccgs.add(ccgNames.get(ccgId));
            }

            behavioralChannelInfos.add(new PopulatedBehavioralChannelMatchInfo(channelNames.get(channel.channel_id), triggers, ccgs));
        }
        return behavioralChannelInfos;
    }

    private List<PopulatedDiscoverChannelMatchInfo> getDiscoverChannelInfos(WMatchInfo matchResults,
            Map<Long, NamedTO> channelNames,
            Map<Long, PopulatedTriggerInfo> triggerNames) {
        List<PopulatedDiscoverChannelMatchInfo> discoverChannelInfos =
                new ArrayList<PopulatedDiscoverChannelMatchInfo>(matchResults.discover_channels.length);

        for (WDiscoverChannelMatchInfo channel : matchResults.discover_channels) {
            List<PopulatedTriggerInfo> triggers = new ArrayList<PopulatedTriggerInfo>(channel.triggers.length);

            for (long channelTriggerId : channel.triggers) {
                triggers.add(triggerNames.get(channelTriggerId));
            }

            List<PopulatedNewsItemInfo> newsItems = new ArrayList<PopulatedNewsItemInfo>(channel.news_items.length);

            for (WNewsItemInfo newsItem : channel.news_items) {
                newsItems.add(new PopulatedNewsItemInfo(newsItem.news_id, newsItem.title, newsItem.link));
            }

            discoverChannelInfos.add(new PopulatedDiscoverChannelMatchInfo(channelNames.get(channel.channel_id), triggers, newsItems));
        }
        return discoverChannelInfos;
    }

    private Map<Long, PopulatedTriggerInfo> getTriggerInfos(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<Long, PopulatedTriggerInfo>();
        }

        final Map<Long, PopulatedTriggerInfo> res = new HashMap<Long, PopulatedTriggerInfo>();
        loggingJdbcTemplate.query("select * from trigger.get_channel_triggers_by_ids(?::bigint[])",
            new Object[] { loggingJdbcTemplate.createArray("bigint", ids) }, new RowCallbackHandler() {

                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    PopulatedTriggerInfo triggerInfo = new PopulatedTriggerInfo(
                        rs.getLong("channel_trigger_id"),
                        rs.getString("original_trigger"),
                        rs.getString("trigger_type").charAt(0));
                    res.put(triggerInfo.getChannelTriggerId(), triggerInfo);

                }
            });

        return res;
    }

    private Map<Long, NamedTO> getChannelNames(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<Long, NamedTO>();
        }

        Query q = em.createQuery("SELECT new com.foros.session.NamedTO(c.id, c.name) FROM Channel c " +
                " WHERE " + SQLUtil.formatINClause("c.id", ids));

        @SuppressWarnings("unchecked")
        List<NamedTO> rawResults = q.getResultList();

        Map<Long, NamedTO> res = new HashMap<Long, NamedTO>(rawResults.size());

        for (NamedTO rawResult : rawResults) {
            res.put(rawResult.getId(), rawResult);
        }

        return res;
    }

    private Map<Long, NamedTO> getCcgNames(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<Long, NamedTO>();
        }

        Query q = em.createQuery("SELECT new com.foros.session.NamedTO(ccg.id, ccg.name) FROM CampaignCreativeGroup ccg " +
                " WHERE " + SQLUtil.formatINClause("ccg.id", ids));

        @SuppressWarnings("unchecked")
        List<NamedTO> rawResults = q.getResultList();

        Map<Long, NamedTO> res = new HashMap<Long, NamedTO>(rawResults.size());

        for (NamedTO rawResult : rawResults) {
            res.put(rawResult.getId(), rawResult);
        }

        return res;
    }

    @Override
    public List<ExpressionAssociationTO> findExpressionAssociations(Long channelId) {
        List<Long> ids = loggingJdbcTemplate.withAuthContext()
                .queryForList("select entityqueries.get_expression_channels(? , ?)",
            new Object[] { channelId, currentUserService.getUser().isDeletedObjectsVisible() },
            new int[] { Types.BIGINT, Types.BOOLEAN }, Long.class);

        List<ExpressionAssociationTO> res = new ArrayList<ExpressionAssociationTO>(ids.size());
        if (!ids.isEmpty()) {
            Query expressionsQuery = em.createQuery("select e from ExpressionChannel e where e.id in (:ids) order by e.account.name, e.name");
            expressionsQuery.setParameter("ids", ids);
            @SuppressWarnings("unchecked")
            List<ExpressionChannel> expressions = expressionsQuery.getResultList();
            for (ExpressionChannel c : expressions) {
                res.add(new ExpressionAssociationTO(c));
            }
        }
        return res;
    }

    @Override
    public List<? extends Channel> resolveChannelTargets(List<String> channelNames, AdvertisingAccountBase defaultAccount, Country country) {
        List<Channel> result = new ArrayList<>(channelNames.size());
        ValidationContext context = ValidationUtil.validationContext().withPath("channelNames").build();
        DuplicateChecker<String> nameDuplicateChecker = DuplicateChecker.create(String.class);
        DuplicateChecker<Channel> channelDuplicateChecker = DuplicateChecker.create(new EntityIdFetcher<Channel>());

        if  (channelNames.isEmpty()) {
            context.addConstraintViolation("errors.field.required");
        }

        int i = 0;
        for (String channelFullName: channelNames) {
            ValidationContext subContext = context
                    .subContext(channelFullName)
                    .withIndex(i++)
                    .build();

            if (StringUtil.isPropertyEmpty(channelFullName)) {
                subContext.addConstraintViolation("errors.field.required");
                continue;
            }

            if (!nameDuplicateChecker.check(subContext, "", channelFullName)) {
                continue;
            }

            String accountName = ExpressionHelper.parseAccountName(channelFullName);
            String channelName = ExpressionHelper.parseChannelName(channelFullName);

            if (StringUtil.isPropertyEmpty(channelName)) {
                subContext.addConstraintViolation("errors.field.required");
                continue;
            }

            Channel channel;
            if (StringUtil.isPropertyEmpty(accountName)) {
                channel = resolveDefaultOrPublicChannelTarget(subContext, country, defaultAccount.getId(), channelName);
            }  else {
                Account account = findAccountByName(accountName);
                if (account == null) {
                    subContext.addConstraintViolation("errors.channelAccountNameNotFound");
                    continue;
                }
                channel = resolveChannelTarget(subContext, country, account.getId(), channelName);
            }

            if (channel == null) {
                continue;
            }

            if (!channelDuplicateChecker.check(subContext, "", channel)) {
                continue;
            }

            expressionChannelValidations.validateChannel(subContext, defaultAccount, country, channel, "channel");

            if (!subContext.ok()) {
                continue;
            }

            result.add(channel);
        }

        context.throwIfHasViolations();

        return result;
    }

    @Override
    public Channel findChannelTarget(Long id) {
        Channel channel = em.find(Channel.class, id);
        if (channel.getNamespace() != ChannelNamespace.ADVERTISING) {
            throw new EntityNotFoundException("Advertising channel with id = " + id + " not found!");
        }

        return channel;
    }

    private Account findAccountByName(String accountName) {
        try {
            return (Account) em.createQuery("select a from Account a where a.name = :name and a.agency is null and a.status <> 'D'")
                    .setParameter("name", accountName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Channel resolveChannelTarget(ValidationContext context, Country country, Long accountId, String channelName) {
        try {
            return findChannelTarget(accountId, channelName, country);
        } catch (NoResultException e) {
            context.addConstraintViolation("errors.field.channelNotFound");
            return null;
        }
    }

    private Channel findChannelTarget(Long accountId, String channelName, Country country) {
        return (Channel) em.createQuery("select c from Channel c " +
                " where c.account.id = :accountId and c.name = :channelName and c.country.countryCode = :country " +
                " and c.namespace = 'A' and c.status <> 'D'")
                .setParameter("accountId", accountId)
                .setParameter("channelName", channelName)
                .setParameter("country", country.getCountryCode())
                .getSingleResult();
    }

    private Channel resolveDefaultOrPublicChannelTarget(ValidationContext context, Country country, Long defaultAccountId, String channelName) {
        try {
            return findChannelTarget(defaultAccountId, channelName, country);
        } catch (NoResultException e) {
            // continue to public channels
        }

        //noinspection unchecked
        List<Channel> channels = em.createQuery("select c from Channel c " +
                " where c.name = :channelName and c.country.countryCode = :country " +
                " and c.visibility = 'PUB'  and c.namespace = 'A' and c.status <> 'D'")
                .setParameter("channelName", channelName)
                .setParameter("country", country.getCountryCode())
                .getResultList();

        if (channels.size() == 0) {
            context.addConstraintViolation("errors.field.channelNotFound");
            return null;
        }

        if (channels.size() > 1) {
            StringBuilder matches = new StringBuilder();
            boolean first = true;
            for (Channel channel : channels) {
                if (!first) {
                    matches.append(", ");
                }
                first = false;
                matches.append(channel.getAccount().getName());
                matches.append('|');
                matches.append(channel.getName());
            }
            context.addConstraintViolation("errors.field.multipleChannelsFound")
                    .withParameters(matches);
            return null;
        }

        return channels.get(0);
    }
}
