package app.programmatic.ui.channel.service;

import static com.foros.rs.client.model.advertising.channel.ChannelType.BEHAVIORAL;
import static com.foros.rs.client.model.advertising.channel.ChannelType.EXPRESSION;
import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;
import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE;
import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE_SELECTOR;

import com.foros.rs.client.model.advertising.channel.*;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.service.AdvertisingChannelService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.account.dao.model.AccountDisplayStatus;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.dao.model.*;
import app.programmatic.ui.channel.dao.model.Channel;
import app.programmatic.ui.channel.dao.model.ChannelType;
import app.programmatic.ui.channel.dao.model.KeywordTypeHitsTO.KeywordHits;
import app.programmatic.ui.channel.tool.ChannelBuilder;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.foros.service.ForosChannelService;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.file.service.FileService;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.dao.model.LocalizationLanguage;
import app.programmatic.ui.localization.dao.model.LocalizationObjectKey;
import app.programmatic.ui.localization.service.LocalizationService;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;
import app.programmatic.ui.restriction.service.RestrictionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChannelServiceImpl implements ChannelService {
    private static final String SPECIAL_CHANNEL_PREFIX = "SPECIAL CHANNEL FOR LI ";
    private static final int TOP_KEYWORDS_COUNT = 5;

    private static final String RU_SYSTEM_TRUE_CHANNEL_NAME = "RU.SYSTEM.TRUE";
    private static Long RU_SYSTEM_TRUE_CHANNEL_ID;
    private static final String RU_SYSTEM_FALSE_CHANNEL_NAME = "RU.SYSTEM.FALSE";
    private static Long RU_SYSTEM_FALSE_CHANNEL_ID;
    private static final String CHANNELS_ACCOUNT_NAME = "Ext System Channels Internal Account";
    private static Long CHANNELS_ACCOUNT_ID;

    final private ForosChannelService forosService;
    final private JdbcOperations jdbcOperations;
    final private RestrictionService restrictionService;
    final private AuthorizationService authorizationService;
    final private LocalizationService localizationService;

    @Autowired
    private FileService fileService;

    @Autowired
    public ChannelServiceImpl(ForosChannelService forosService,
                              JdbcOperations jdbcOperations,
                              RestrictionService restrictionService,
                              AuthorizationService authorizationService,
                              LocalizationService localizationService) {
        this.forosService = forosService;
        this.jdbcOperations = jdbcOperations;
        this.restrictionService = restrictionService;
        this.authorizationService = authorizationService;
        this.localizationService = localizationService;

        init();
    }

    private void init() {
        synchronized (ChannelServiceImpl.class) {
            if (RU_SYSTEM_TRUE_CHANNEL_ID == null) {
                RU_SYSTEM_TRUE_CHANNEL_ID = findSingleByName(ChannelType.E, LOCALE_RU.getCountry(), RU_SYSTEM_TRUE_CHANNEL_NAME);
            }
            if (RU_SYSTEM_FALSE_CHANNEL_ID == null) {
                RU_SYSTEM_FALSE_CHANNEL_ID = findSingleByName(ChannelType.B, LOCALE_RU.getCountry(), RU_SYSTEM_FALSE_CHANNEL_NAME);
            }
            if (CHANNELS_ACCOUNT_ID == null) {
                CHANNELS_ACCOUNT_ID = findChannelsAccountByName(CHANNELS_ACCOUNT_NAME);
            }
        }
    }

    private Long findSingleByName(ChannelType channelType, String countryCode, String name) {
        return jdbcOperations.queryForObject("select channel_id from channel where channel_type = ? and country_code = ? and name = ?",
                new Object[]{ channelType.toString(), countryCode, name },
                (ResultSet rs, int ind) -> rs.getLong("channel_id"));
    }

    private Long findChannelsAccountByName(String name) {
        return jdbcOperations.queryForObject("select account_id from account where name = ? and status = 'A'",
                new Object[] { name },
                (ResultSet rs, int ind) -> rs.getLong("account_id"));
    }

    public static Long getRuSystemTrueChannelId() {
        return RU_SYSTEM_TRUE_CHANNEL_ID;
    }

    public static Long getRuSystemFalseChannelId() {
        return RU_SYSTEM_FALSE_CHANNEL_ID;
    }

    @Override
    public BehavioralChannel findBehavioralUnchecked(Long id) {
        List<com.foros.rs.client.model.advertising.channel.Channel> result = findImpl(Collections.singletonList(id), BEHAVIORAL);
        return result.isEmpty() ? null : (BehavioralChannel)result.get(0);
    }

    @Override
    public ExpressionChannel findExpressionUnchecked(Long id) {
        List<com.foros.rs.client.model.advertising.channel.Channel> result = findImpl(Collections.singletonList(id), EXPRESSION);
        return result.isEmpty() ? null : (ExpressionChannel)result.get(0);
    }

    @Override
    public ExpressionChannel findExpressionAsAdmin(Long id) {
        ChannelSelector selector = new ChannelSelector();
        selector.setChannelIds(Collections.singletonList(id));
        selector.setPaging(MAX_RESULTS_SIZE_SELECTOR);
        return (ExpressionChannel)(forosService.getAdminChannelService().get(selector).getEntities()).get(0);
    }

    @Override
    public List<com.foros.rs.client.model.advertising.channel.Channel> findChannels(List<Long> ids) {
        return findImpl(ids, null);
    }

    @Override
    public List<BehavioralChannel> findAllBehavioral(List<Long> ids) {
        ChannelSelector selector = new ChannelSelector();
        selector.setChannelIds(ids);
        selector.setPaging(MAX_RESULTS_SIZE_SELECTOR);

        return forosService.getChannelService().get(selector).getEntities().stream()
                .map( channel -> (BehavioralChannel)channel )
                .collect(Collectors.toList());
    }

    private List<com.foros.rs.client.model.advertising.channel.Channel> findImpl(List<Long> ids,
            com.foros.rs.client.model.advertising.channel.ChannelType channelType) {
        ChannelSelector selector = new ChannelSelector();
        selector.setChannelIds(ids);
        selector.setType(channelType);
        selector.setPaging(MAX_RESULTS_SIZE_SELECTOR);

        return forosService.getChannelService().get(selector).getEntities();
    }

    @Override
    public ExpressionChannelStat fetchExpressionStat(Long id) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_ADVERTISING_CHANNEL, id);

        return new ExpressionChannelStat(
            jdbcOperations.query("select * from statqueries.channel_activities_detailed(?)",
                    new Object[] { id },
                    (ResultSet rs, int ind) -> {
                        ExpressionHitsTO result = new ExpressionHitsTO();

                        result.setDate(rs.getDate("sdate").toLocalDate().atStartOfDay());
                        result.setTotalUniques(rs.getLong("total_uniques"));
                        result.setActiveDailyUniques(rs.getLong("active_daily_uniques"));
                        result.setImps(rs.getLong("served_imps"));
                        result.setClicks(rs.getLong("served_clicks"));

                        return result;
                    })
        );
    }

    @Override
    public BehavioralChannelStat fetchBehavioralStat(Long id) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_ADVERTISING_CHANNEL, id);

        return new BehavioralChannelStat(
                fetchKeywordHits(id),
                fetchKeywordTypeHits(id)
        );
    }

    private List<KeywordTypeHitsTO> fetchKeywordTypeHits(Long id) {
        return jdbcOperations.query("select * from statqueries.channel_activities_detailed(?)",
                new Object[] { id },
                (ResultSet rs, int ind) -> {
                    KeywordTypeHitsTO result = new KeywordTypeHitsTO();

                    result.setDate(rs.getDate("sdate").toLocalDate().atStartOfDay());
                    result.setPageKeywordHits(new KeywordHits(
                            rs.getLong("match_page_keywords"),
                            preparePercentValue(rs.getBigDecimal("match_page_keywords_pc"))
                    ));
                    result.setSearchKeywordHits(new KeywordHits(
                            rs.getLong("match_search_keywords"),
                            preparePercentValue(rs.getBigDecimal("match_search_keywords_pc"))
                    ));
                    result.setUrlHits(new KeywordHits(
                            rs.getLong("match_urls"),
                            preparePercentValue(rs.getBigDecimal("match_urls_pc"))
                    ));
                    result.setUrlKeywordHits(new KeywordHits(
                            rs.getLong("match_url_keywords"),
                            preparePercentValue(rs.getBigDecimal("match_url_keywords_pc"))
                    ));
                    result.setTotalHits(rs.getLong("match_total"));
                    result.setTotalUniques(rs.getLong("total_uniques"));
                    result.setActiveDailyUniques(rs.getLong("active_daily_uniques"));
                    result.setImps(rs.getLong("served_imps"));
                    result.setClicks(rs.getLong("served_clicks"));

                    return result;
        });
    }

    private static BigDecimal preparePercentValue(BigDecimal initialValue) {
        return initialValue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private List<KeywordHitsTO> fetchKeywordHits(Long id) {
        KeywordTypeNameHolder type = new KeywordTypeNameHolder();

        return jdbcOperations.query("select * from statqueries.triggers_all_types(?::bigint, 'A', 'hits', 'desc', ?::int, 1)",
                new Object[] { id, TOP_KEYWORDS_COUNT },
                (ResultSet rs, int ind) -> {
                    KeywordHitsTO result = new KeywordHitsTO();

                    result.setKeyword(rs.getString("original_trigger"));
                    result.setType(type.byLetter(rs.getString("trigger_type")));
                    result.setHits(rs.getLong("hits"));
                    result.setImps(rs.getLong("impressions"));
                    result.setClicks(rs.getLong("clicks"));
                    result.setCtr(rs.getBigDecimal("ctr"));

                    return result;
                });
    }

    @Override
    public List<Channel> findByAccountId(Long accountId) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_ADVERTISING_CHANNELS, accountId);

        List<Long> availableIds = jdbcOperations.query(
                "select channel_id from statqueries.get_account_non_autogenerated_channels(?::int, ?)",
                new Object[] { accountId, Boolean.FALSE },
                (ResultSet rs, int ind) -> rs.getLong("channel_id"));
        return findByIdsUnrestricted(availableIds);
    }

    @Override
    @Restrict(restriction = "channel.searchInternal")
    public List<Channel> findAllChannels(String name, Long accountId, ChannelType type, ChannelVisibility visibility) {
        return jdbcOperations.query(
                "select * from statqueries.get_all_non_autogenerated_channels(?::text, ?::int, ?::character, ?::text, ?::text, ?::boolean, ?::int)",
                new Object[]{
                        name != null ? name.trim() : null,
                        accountId,
                        type != null ? type.name() : null,
                        visibility != null ? visibility.name() : null,
                        LOCALE_RU.getCountry(),
                        false,
                        authorizationService.getAuthUser().getId()
                },
                (ResultSet rs, int ind) -> {
                    Channel channel = new Channel();
                    channel.setId(rs.getLong("channel_id"));
                    channel.setName(rs.getString("channel_name"));
                    channel.setAccountId(rs.getLong("account_id"));
                    channel.setAccountName(rs.getString("account_name"));
                    channel.setDisplayStatus(ChannelDisplayStatus.valueOf(rs.getInt("channel_display_status_id")).getMajorStatus());
                    channel.setAccountDisplayStatus(AccountDisplayStatus.valueOf(rs.getInt("account_display_status_id")).getMajorStatus());
                    channel.setType(ChannelType.valueOf(rs.getString("channel_type")));
                    channel.setVisibility(ChannelVisibility.valueOf(rs.getString("visibility")));
                    return channel;
                }
        );
    }

    @Override
    public List<Channel> findByIdsForExternal(Long extAccountId, List<Long> ids) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_ADVERTISING_CHANNELS, extAccountId);

        List<Channel> channels = findByIdsWithUniqUsersInfoUnrestricted(ids);
        Optional<Channel> notViewable = channels.stream()
                .filter(channel -> !channel.getAccountId().equals(extAccountId) &&
                                    !channel.getAccountId().equals(CHANNELS_ACCOUNT_ID))
                .findAny();
        if (notViewable.isPresent()) {
            throw new EntityNotFoundException(notViewable.get().getId());
        }

        return channels;
    }

    @Override
    public List<Channel> findByIdsUnrestricted(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> limitedIds = ids.size() <= MAX_RESULTS_SIZE ? ids : ids.subList(0, MAX_RESULTS_SIZE);
        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", limitedIds.toArray()));
        return jdbcOperations.query("select c.channel_id, c.name as channel_name, " +
                        "coalesce(c.name, dr.value) as localized_name, " +
                        "c.channel_type, c.country_code, c.account_id, a.name as account_name, c.display_status_id as channel_display_status_id, " +
                        "c.visibility as channel_visibility, -1 as user_count from channel c " +
                        "  inner join account a using (account_id) " +
                        "  left join dynamicresources as dr on dr.key = 'Channel.' ||  c.channel_id::text and dr.lang = ?" +
                        "  where c.channel_id = any(?)" +
                        "  order by c.name",
                new Object[] { LOCALE_RU.getLanguage(), idsArray },
                (ResultSet rs, int ind) -> ChannelBuilder.buildLocalized(rs)
        );
    }

    @Override
    public List<Channel> findByIdsWithUniqUsersInfoUnrestricted(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> limitedIds = ids.size() <= MAX_RESULTS_SIZE ? ids : ids.subList(0, MAX_RESULTS_SIZE);
        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", limitedIds.toArray()));
        return jdbcOperations.query("select c.channel_id, c.name as channel_name, " +
                        "statqueries.get_localized_advertising_channel_paths(c.channel_id, ?) as localized_name, " +
                        "c.channel_type, c.country_code, c.account_id, a.name as account_name, c.display_status_id as channel_display_status_id, " +
                        "c.visibility as channel_visibility, user_count from channel c " +
                        "  inner join account a using (account_id) " +
                        "  left join (" +
                        "        select ccia.channel_id," +
                        "               avg(date_users) user_count" +
                        "        from (" +
                        "            select cci.channel_id," +
                        "                   sum(cci.active_user_count) date_users" +
                        "            from channelinventory cci " +
                        "            where cci.channel_id = any(?)" +
                        "              and cci.sdate between current_date - 30 and current_date - 1" +
                        "            group by cci.channel_id," +
                        "                     cci.sdate" +
                        "        ) ccia" +
                        "        group by 1" +
                        "    ) cidata using (channel_id)" +
                        "  where c.channel_id = any(?)" +
                        "  order by c.name",
                new Object[] { LOCALE_RU.getLanguage(), idsArray, idsArray },
                (ResultSet rs, int ind) -> ChannelBuilder.buildLocalized(rs)
        );
    }

    @Override
    public List<ChannelStat> getStatsByLineItemId(Long lineItemId) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CCG, findCcgId(lineItemId));

        Long standaloneAccountId = findStandaloneAccountId(lineItemId);
        ChangeChannelStatusChecker statusChecker = new ChangeChannelStatusChecker(restrictionService, standaloneAccountId);

        try {
            return jdbcOperations.query("select * from statqueries.channel_stats_for_line_item(?::int, ?::date, ?::date, ?::text)",
                    new Object[]{
                            lineItemId,
                            null,
                            null,
                            LOCALE_RU.getLanguage()
                    },
                    (ResultSet rs, int rowNum) -> ChannelBuilder.buildStatForLineItem(rs, statusChecker)
            );
        } finally {
            statusChecker.doRemoteRequest();
        }
    }

    @Override
    public List<ChannelStat> getStatsByFlightId(Long flightId) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CAMPAIGN, findCampaignId(flightId));

        return jdbcOperations.query("select * from statqueries.channel_stats_for_flight(?::int, ?::date, ?::date, ?::text)",
                new Object[] {
                    flightId,
                    null,
                    null,
                    LOCALE_RU.getLanguage()
                },
                (ResultSet rs, int rowNum) -> ChannelBuilder.buildStatForFlight(rs)
        );
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.channel.validation.ForosExpressionChannelViolationsServiceImpl")
    public Long createOrUpdate(ExpressionChannel channel) {
        return createOrUpdateImpl(Collections.singletonList(channel), forosService.getChannelService()).get(0);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.channel.validation.ForosBehavioralChannelViolationsServiceImpl")
    public Long createOrUpdate(BehavioralChannel channel) {
        return createOrUpdateImpl(Collections.singletonList(channel), forosService.getChannelService()).get(0);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.channel.validation.ForosExpressionChannelViolationsServiceImpl")
    public Long createOrUpdateAsAdmin(ExpressionChannel channel) {
        return createOrUpdateImpl(Collections.singletonList(channel)).get(0);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.channel.validation.ForosBehavioralChannelViolationsServiceImpl")
    public Long createOrUpdateAsAdmin(BehavioralChannel channel) {
         return createOrUpdateImpl(Collections.singletonList(channel)).get(0);
    }

    @Override
    public List<Long> createOrUpdate(List<BehavioralChannel> channels) {
        return createOrUpdateImpl(channels);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.channel.validation.ForosExpressionChannelViolationsServiceImpl")
    public List<Long> createOrUpdateExpressions(List<ExpressionChannel> channels) {
        return createOrUpdateImpl(channels);
    }

    private List<Long> createOrUpdateImpl(List<? extends com.foros.rs.client.model.advertising.channel.Channel> channels) {
        return createOrUpdateImpl(channels, forosService.getAdminChannelService());
    }

    private List<Long> createOrUpdateImpl(List<? extends com.foros.rs.client.model.advertising.channel.Channel> channels,
                                          AdvertisingChannelService advertisingChannelService) {
        if (channels.isEmpty()) {
            return Collections.emptyList();
        }

        List<Operation<com.foros.rs.client.model.advertising.channel.Channel>> operations = channels.stream()
                .map( channel -> {
                    Operation<com.foros.rs.client.model.advertising.channel.Channel> channelOperation = new Operation<>();
                    channelOperation.setEntity(channel);
                    channelOperation.setType(channel.getId() == null ? OperationType.CREATE : OperationType.UPDATE);

                    return channelOperation;})
                .collect(Collectors.toList());

        Operations<com.foros.rs.client.model.advertising.channel.Channel> channelOperations = new Operations<>();
        channelOperations.setOperations(operations);

        OperationsResult result = advertisingChannelService.perform(channelOperations);
        return result.getIds();
    }

    @Override
    public List<Long> filterActive(List<Long> ids) {
        if (ids.isEmpty()) {
            return ids;
        }

        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
        return jdbcOperations.query("select channel_id from channel where status = 'A' and channel_id = any(?)",
                                    new Object[] { idsArray },
                                    (ResultSet rs, int rowNum) -> rs.getLong("channel_id"));
    }

    @Override
    @Restrict(restriction = "channel.search")
    public List<Channel> searchByName(String countryCode, Long accountId, String name) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_ADVERTISING_CHANNELS, accountId);

        return jdbcOperations.query("select * from statqueries.find_spa_advertising_channels" +
                                    "(?::int, ?::text, ?::text, ?::bigint[], ?::boolean, ?::boolean, ?::boolean, ?::boolean)",
                new Object[] {
                    accountId,
                    countryCode,
                    name,
                    null,
                    Boolean.TRUE,
                    Boolean.TRUE,
                    Boolean.TRUE,
                    Boolean.TRUE
                },
                (ResultSet rs, int rowNum) -> ChannelBuilder.build(rs)
            )
        .stream()
        .limit(MAX_RESULTS_SIZE)
        .collect(Collectors.toList());
    }

    @Override
    @Restrict(restriction = "channel.search")
    public Collection<ChannelNameId> searchByNames(Set<ChannelName> names) {
        if (names.isEmpty()) {
            return Collections.emptyList();
        }

        Array accountNamesArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("text",
                names.stream().map( cn -> cn.getAccountName() ).collect(Collectors.toList()).toArray()));
        Array channelNamesArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("text",
                names.stream().map( cn -> cn.getName() ).collect(Collectors.toList()).toArray()));
        Array namesArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("text",
                names.stream().map( cn -> cn.getAccountName() + "::" + cn.getName() ).collect(Collectors.toList()).toArray()));

        return jdbcOperations.query("select c.channel_id, c.name as channel_name, a.name as account_name " +
                                    "  from channel c inner join account a using(account_id) where " +
                                    "  a.name = any(?) and " +
                                    "  c.name = any(?) and " +
                                    "  (a.name || '::' || c.name) = any(?)",
                                    new Object[] {
                                         accountNamesArray,
                                         channelNamesArray,
                                         namesArray },
                                    (ResultSet rs, int ind) -> new ChannelNameId(
                                            rs.getString("channel_name"), rs.getString("account_name"), rs.getLong("channel_id")));
    }

    @Override
    public Collection<ChannelNode> getChannelNodeList(Long parentId, String language) {
        return jdbcOperations.query("select * from entityqueries.get_advertising_child_channels_for_external(?::bigint, ?)",
                new Object[] { parentId, language },
                (ResultSet rs, int ind) -> new ChannelNode(rs.getLong("channel_id"),
                                                               rs.getString("channel_name"),
                                                               rs.getString("localized_name"),
                                                               rs.getBoolean("has_children")
                        )
                );
    }

    @Override
    public Collection<ChannelNode> getChannelRubricNodeList(String source, String countryCode, String language) {
        return jdbcOperations.query("select * from entityqueries.get_advertising_root_channels_for_external(?::int, ?, ?, ?)",
                new Object[] { CHANNELS_ACCOUNT_ID, countryCode, source, language },
                (ResultSet rs, int ind) -> new ChannelNode(rs.getLong("channel_id"),
                                                               rs.getString("channel_name"),
                                                               rs.getString("localized_name"),
                                                               rs.getBoolean("has_children")
                        )
                );
    }

    @Override
    public List<IdName> findByName(String name, String countryCode, ChannelType channelType, Visibility visibility) {
        return jdbcOperations.query("select channel_id, name " +
                        "from channel " +
                        "where " +
                        "name like ? " +
                        "and country_code = ? " +
                        (channelType != null ? " and channel_type = '" + channelType + "' " : "") +
                        (visibility != null ? " and visibility = '" + visibility + "' " : "") +
                        "and status != 'D'",
                new Object[]{
                        "%" + name + "%",
                        countryCode
                },
                (ResultSet rs, int ind) ->
                        new IdName(rs.getLong("channel_id"), rs.getString("name")));
    }

    @Override
    @Transactional
    public Long createSpecialChannel(Long lineItemId) {
        if (lineItemId == null) {
            throw new NullPointerException();
        }

        String channelName = getSpecialChannelName(lineItemId);
        ExpressionChannel newSpecialChannel = newSpecialChannel(channelName);
        Long id = createOrUpdateAsAdmin(newSpecialChannel);
        localizeSpecialChannel(id);

        return id;
    }

    private ExpressionChannel newSpecialChannel(String name) {
        ExpressionChannel newChannel = new ExpressionChannel();
        newChannel.setName(name);
        newChannel.setExpression(RU_SYSTEM_FALSE_CHANNEL_ID.toString());
        newChannel.setCountry(LOCALE_RU.getCountry());
        newChannel.setStatus(Status.ACTIVE);
        newChannel.setVisibility(ChannelVisibility.PUB.toString());

        EntityLink accountLink = new EntityLink();
        accountLink.setId(CHANNELS_ACCOUNT_ID);
        newChannel.setAccount(accountLink);

        return newChannel;
    }

    private void localizeSpecialChannel(Long id) {
        Localization localization = new Localization();

        localization.setKey(LocalizationObjectKey.CHANNEL.getPrefix() + id);
        localization.setLang(LocalizationLanguage.valueOf(LOCALE_RU.getLanguage()));
        localization.setValue(MessageInterpolator.getDefaultMessageInterpolator().interpolate("flight.specialChannelName"));

        localizationService.updateLocalizationsUnrestricted(Collections.singletonList(localization));
    }

    private static String getSpecialChannelName(Long lineItemId) {
        return SPECIAL_CHANNEL_PREFIX + String.valueOf(lineItemId);
    }

    @Override
    public boolean checkSpecialChannelConstraints(Long channelId, Long lineItemId) {
        if (channelId == null) {
            throw new NullPointerException("ChannelServiceImpl.checkSpecialChannelConstraints(Long channelId, Long lineItemId): " +
                    "'channelId' must not be null");
        }

        if (lineItemId == null) {
            // New Line Item
            return jdbcOperations.queryForObject(
                    String.format("select exists(select 1 from channel c left join flight f on f.special_channel_id = c.channel_id " +
                                  "where c.channel_id=%d and c.name like '%s%%' and c.account_id=%d and f.flight_id is null)",
                            channelId, SPECIAL_CHANNEL_PREFIX, CHANNELS_ACCOUNT_ID),
                    Boolean.class);
        }

        return jdbcOperations.queryForObject(
                String.format("select exists(select 1 from channel where channel_id=%d and name='%s' and account_id=%d)",
                        channelId, getSpecialChannelName(lineItemId), CHANNELS_ACCOUNT_ID),
                Boolean.class);
    }

    public MajorDisplayStatus changeStatus(Long channelId, ChannelOperation operation) {
        com.foros.rs.client.model.advertising.channel.Channel channel = toEmptyForosChannel(channelId);
        ForosHelper.changeEntityStatus(channel, ForosHelper.isChangeStatusOperation(operation));
        createOrUpdateImpl(Collections.singletonList(channel));

        return findChannelStatus(channelId);
    }

    private MajorDisplayStatus findChannelStatus(Long channelId) {
        Integer displayStatusId = jdbcOperations.queryForObject(
                "select display_status_id from channel where channel_id = ?",
                new Object[] { channelId },
                Integer.class);
        return ChannelDisplayStatus.valueOf(displayStatusId).getMajorStatus();
    }

    private com.foros.rs.client.model.advertising.channel.Channel toEmptyForosChannel(Long channelId) {
        String channelType = jdbcOperations.queryForObject("select channel_type from channel where channel_id = ?",
                                                           new Object [] { channelId },
                                                           String.class);

        com.foros.rs.client.model.advertising.channel.Channel result;

        switch (channelType) {
            case "A":
                result = new AudienceChannel();
                break;
            case "B":
                result = new BehavioralChannel();
                break;
            case "E":
                result = new ExpressionChannel();
                break;
            default:
                throw new IllegalArgumentException("Unexpected channel type: " + channelType);
        }

        result.setId(channelId);
        return result;
    }

    private Long findCcgId(Long lineItemId) {
        return jdbcOperations.queryForObject("select ccg_id from flightccg where flight_id = ?",
                                             new Object[] {lineItemId},
                                             Long.class);
    }

    private Long findCampaignId(Long flightId) {
        return jdbcOperations.queryForObject("select campaign_id from campaignallocation ca " +
                                                      "inner join flight f using(io_id) " +
                                                      "where flight_id = ?",
                                             new Object[] {flightId},
                                             Long.class);
    }

    private Long findStandaloneAccountId(Long lineItemId) {
        return jdbcOperations.queryForObject("select coalesce(a.agency_account_id, a.account_id) from account a " +
                                             "  inner join insertionorder io on io.account_id = a.account_id " +
                                             "  inner join flight f using(io_id) " +
                                             "  inner join flight li on f.flight_id = li.parent_id " +
                                             "  where li.flight_id = ?",
                new Object[] {lineItemId},
                Long.class);
    }

    private static class ChangeChannelStatusChecker implements ChannelBuilder.ChangeStatusChecker {
        private RestrictionService restrictionService;
        private Long accountId;
        private ArrayList<ChannelStat> forRemoteRequest = new ArrayList<>();

        public ChangeChannelStatusChecker(RestrictionService restrictionService, Long accountId) {
            this.restrictionService = restrictionService;
            this.accountId = accountId;
        }

        public void fillCanChange(ChannelStat channelStat) {
            channelStat.setStatusChangeable(false);
            if (accountId.equals(channelStat.getAccountId())) {
                forRemoteRequest.add(channelStat);
            }
        }

        public void doRemoteRequest() {
            if(forRemoteRequest.isEmpty()) {
                return;
            }

            RestrictionCommandBuilder builder = new RestrictionCommandBuilder();

            for (ChannelStat channelStat : forRemoteRequest) {
                builder.add(Restriction.UPDATE_ADVERTISING_CHANNEL, channelStat.getId());
            }

            Iterator<ChannelStat> it = forRemoteRequest.iterator();
            for (Boolean predicate : restrictionService.isPermitted(builder)) {
                it.next().setStatusChangeable(predicate);
            }
        }
    }

    private class KeywordTypeNameHolder {
        private String page;
        private String search;
        private String url;
        private String urlKeyword;

        public String byLetter(String letter) {
            switch (letter) {
                case "P": return getPage();
                case "S": return getSearch();
                case "U": return getUrl();
                case "R": return getUrlKeyword();
                default: throw new IllegalArgumentException("Unexpected letter for KeywordType: " + letter);
            }
        }

        public String getPage() {
            if (page == null) {
                page = interpolate(KeywordType.PAGE_KEYWORD);
            }
            return page;
        }

        public String getSearch() {
            if (search == null) {
                search = interpolate(KeywordType.SEARCH_KEYWORD);
            }
            return search;
        }

        public String getUrl() {
            if (url == null) {
                url = interpolate(KeywordType.URL);
            }
            return url;
        }

        public String getUrlKeyword() {
            if (urlKeyword == null) {
                urlKeyword = interpolate(KeywordType.URL_KEYWORD);
            }
            return urlKeyword;
        }

        private String interpolate(KeywordType type) {
            return MessageInterpolator.getDefaultMessageInterpolator().interpolate(type.getDescriptionKey());
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "channel.uploadReport")
    public void uploadChannelReport(MultipartFile file) {
        try {
            fileService.uploadChannelReport(file, CHANNELS_ACCOUNT_ID);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "channel.uploadReport")
    public void uploadChannelReport(MultipartFile file, Long accountId) {
        try {
            fileService.uploadChannelReport(file, accountId);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "channel.downloadReport")
    public byte[] downloadChannelReport(String name) {
        return fileService.downloadChannelReport(CHANNELS_ACCOUNT_ID, name);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    @Restrict(restriction = "channel.downloadReport", parameters="accountId")
    public byte[] downloadChannelReport(String name, Long accountId) {
        return fileService.downloadChannelReport(accountId, name);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public List<String> channelReportList() {
        try {
            return fileService.channelReportList(CHANNELS_ACCOUNT_ID);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public List<String> channelReportList(Long accountId) {
        try {
            return fileService.channelReportList(accountId);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }
}
