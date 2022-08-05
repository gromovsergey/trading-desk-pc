package app.programmatic.ui.audienceresearch.service;

import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import com.foros.rs.client.model.advertising.channel.Visibility;
import com.foros.rs.client.model.entity.EntityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import app.programmatic.ui.account.dao.AccountRepository;
import app.programmatic.ui.account.dao.model.AccountDisplayStatus;
import app.programmatic.ui.account.dao.model.AccountEntity;
import app.programmatic.ui.audienceresearch.dao.AudienceResearchChannelRepository;
import app.programmatic.ui.audienceresearch.dao.AudienceResearchRepository;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearch;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearchChannel;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearchStat;
import app.programmatic.ui.audienceresearch.tool.PostgreChannelPairUserType;
import app.programmatic.ui.audienceresearch.view.AudienceResearchView;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.dao.ChannelRepository;
import app.programmatic.ui.channel.dao.model.ChannelDisplayStatus;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.channel.dao.model.ChannelType;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.model.Status;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.localization.service.LocalizationService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

@Service
@Validated
public class AudienceResearchServiceImpl implements AudienceResearchService {
    private static final String SERVICE_CHANNELS_NAME_PREFIX = "Auto-generated Audience Research Service Channel ";

    @Autowired
    private AudienceResearchRepository audienceResearchRepository;

    @Autowired
    private AudienceResearchChannelRepository audienceResearchChannelRepository;

    @Autowired
    @Qualifier("statDataOperations")
    private JdbcOperations statDataJdbcOperations;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LocalizationService localizationService;

    @Override
    @Transactional(readOnly = true)
    @Restrict(restriction = "audienceResearch.view")
    public AudienceResearch findEager(Long id) {
        AudienceResearch result = id != null ? audienceResearchRepository.findById(id).orElse(null) : null;
        if (result == null) {
            throw new EntityNotFoundException(id);
        }

        result.getChannels().size();
        result.getAdvertisers().size();

        return result;
    }

    @Override
    @Restrict(restriction = "audienceResearch.view")
    public List<AudienceResearchView> findAll() {
        return audienceResearchRepository.findByStatusNotOrderByTargetChannelName(Status.DELETED.getLetter()).stream()
                .map(e -> {
                    AudienceResearchView view = new AudienceResearchView();
                    view.setId(e.getId());
                    view.setChannelName(e.getTargetChannel().getName());
                    view.setDisplayStatus(e.getTargetChannel().getMajorDisplayStatus());
                    view.setStartDate(e.getStartDate());
                    return view;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Restrict(restriction = "audienceResearch.view")
    public List<AudienceResearchView> findForExternal(Long accountId) {
        List<AudienceResearchView> resultList = statDataJdbcOperations.query("select " +
                        "  ar.ar_id, c.channel_id, c.name, c.display_status_id, ar.start_date " +
                        "from audienceresearch ar " +
                        "inner join channel c on c.channel_id = ar.target_channel_id " +
                        "inner join aradvertiser a using (ar_id) " +
                        "where " +
                        "  a.account_id = ? " +
                        "  and ar.status != 'D'",
                new Object[]{accountId},
                (ResultSet rs, int ind) -> {
                    AudienceResearchView view = new AudienceResearchView();
                    view.setId(rs.getLong("ar_id"));
                    view.setChannelId(rs.getLong("channel_id"));
                    view.setChannelName(rs.getString("name"));
                    Long displayStatusId = rs.getLong("display_status_id");
                    view.setDisplayStatus(ChannelDisplayStatus.valueOf(displayStatusId.intValue()).getMajorStatus());
                    view.setStartDate(rs.getTimestamp("start_date"));
                    return view;
                }
        );

        Map<Long, String> localizedNames = localizationService.findRuChannelLocalizationsByIds(
                resultList.stream()
                .map(e -> e.getChannelId())
                .collect(Collectors.toList()).toArray(new Long[resultList.size()])
        );

        resultList.stream().forEach(e -> {
            if (localizedNames.containsKey(e.getChannelId())) {
                e.setChannelName(localizedNames.get(e.getChannelId()));
            }
        });

        return resultList.stream()
                .sorted((param1, param2) -> param1.getChannelName().compareTo(param2.getChannelName()))
                .collect(Collectors.toList());
    }

    @Override
    @Restrict(restriction = "audienceResearch.edit")
    public List<ChannelEntity> findChannels(String text, ChannelType type, boolean internalOnly, int maxRows) {
        return statDataJdbcOperations.query(
                "select * from statqueries.get_audience_research_channels(?::text, ?::character, ?::boolean, ?::text, ?::int, ?::int)",
                new Object[]{
                        text,
                        type != null ? type.name() : null,
                        internalOnly,
                        LOCALE_RU.getCountry(),
                        authorizationService.getAuthUser().getId(),
                        maxRows
                },
                (ResultSet rs, int ind) -> {
                    ChannelEntity channel = new ChannelEntity();
                    channel.setId(rs.getLong("channel_id"));
                    channel.setName(rs.getString("channel_name"));
                    channel.setDisplayStatus(ChannelDisplayStatus.valueOf(rs.getInt("channel_display_status_id")));
                    AccountEntity account = new AccountEntity();
                    account.setId(rs.getLong("account_id"));
                    account.setName(rs.getString("account_name"));
                    account.setDisplayStatus(AccountDisplayStatus.valueOf(rs.getInt("account_display_status_id")));
                    channel.setAccount(account);
                    return channel;
                }
        );
    }

    @Override
    public AudienceResearchStat getStat(Long audienceResearchId, Long expressionChannelId) {
        AudienceResearch audienceResearch = audienceResearchRepository.findById(audienceResearchId).orElse(null);
        ExpressionChannel expressionChannel = channelService.findExpressionAsAdmin(expressionChannelId);

        List<Long> ids = findChannelsFromExpression(expressionChannel);
        List<com.foros.rs.client.model.advertising.channel.Channel> channels = channelService.findChannels(ids);

        String autoGeneratedChannelsNamePrefix = SERVICE_CHANNELS_NAME_PREFIX + audienceResearchId + "-" + expressionChannel.getId() + "-";
        List<IdName> autoGeneratedChannels = channelService.findByName(
                autoGeneratedChannelsNamePrefix, LOCALE_RU.getCountry(), ChannelType.E, Visibility.PRI
        );

        Map<Long, Long> channelIdsMap = new LinkedHashMap<>();
        ids.stream().forEach(v -> {
            List<IdName> autoGeneratedChannel = autoGeneratedChannels.stream()
                    .filter(c -> c.getName().equals(autoGeneratedChannelsNamePrefix + v))
                    .collect(Collectors.toList());
            if (autoGeneratedChannel.size() > 0) {
                channelIdsMap.put(v, autoGeneratedChannel.get(0).getId());
            }
        });
        List<String> dates = new ArrayList<>();
        List<List<BigDecimal>> values = new ArrayList<>();
        statDataJdbcOperations.query(
                "select * from statqueries.get_audience_research(?::bigint, ?::bigint, ?::statqueries.channel_pair[])",
                new Object[]{
                        audienceResearch.getTargetChannel().getId(),
                        expressionChannelId,
                        getIdsArray(channelIdsMap)
                },
                (ResultSet rs) -> {
                    String date = rs.getString("sdate");
                    Long channelId = rs.getLong("channel_id");
                    BigDecimal affinityIndex = rs.getBigDecimal("affinity_index");

                    if (!dates.contains(date)) {
                        dates.add(date);
                        List<BigDecimal> dateValues = new ArrayList<>(Collections.nCopies(ids.size(), BigDecimal.ZERO));
                        dateValues.set(ids.indexOf(channelId), affinityIndex);
                        values.add(dateValues);
                    } else {
                        List<BigDecimal> dateValues = values.get(values.size() - 1);
                        dateValues.set(ids.indexOf(channelId), affinityIndex);
                    }
                }
        );

        AudienceResearchStat stat = new AudienceResearchStat();

        String localizedName = localizationService.findRuChannelLocalizationsByIds(expressionChannel.getId()).get(expressionChannel.getId());
        stat.setChannelName(localizedName != null ? localizedName : expressionChannel.getName());

        Map<Long, String> localizedNames = localizationService.findRuChannelLocalizationsByIds(ids.toArray(new Long[ids.size()]));
        stat.setTicks(channels.stream()
                .map(c -> {
                    if (localizedNames.containsKey(c.getId())) {
                        return localizedNames.get(c.getId());
                    }
                    return c.getName();
                })
                .collect(Collectors.toList())
        );

        stat.setDates(dates);
        stat.setValues(values);
        if (dates.size() > 0) {
            stat.setLastDate(dates.get(dates.size() - 1));
            stat.setLastValues(values.get(dates.size() - 1));
        }

        return stat;
    }

    private Array getIdsArray(Map<Long, Long> ids) {
        if (ids == null) {
            return null;
        }
        List<PostgreChannelPairUserType> pairs = new ArrayList<>(ids.size());
        for (Map.Entry<Long, Long> entry : ids.entrySet()) {
            pairs.add(new PostgreChannelPairUserType(entry.getKey(), entry.getValue()));
        }
        return statDataJdbcOperations.execute((Connection con) -> con.createArrayOf("channel_pair", pairs.toArray()));
    }

    @Override
    @Transactional
    @Restrict(restriction = "audienceResearch.edit")
    public Long create(AudienceResearch audienceResearch) {
        audienceResearch.setStatus(Status.ACTIVE);
        audienceResearch.setStartDate(new Timestamp(System.currentTimeMillis()));

        ChannelEntity channel = channelRepository.findById(audienceResearch.getTargetChannel().getId()).orElse(null);
        audienceResearch.setTargetChannel(channel);
        audienceResearch.setAdvertisers(fetchAccountsByIds(audienceResearch.getAdvertisers()));

        Long id = audienceResearchRepository.save(audienceResearch).getId();

        audienceResearch.getChannels()
                .forEach(c -> prePersistChannel(c, audienceResearch));
        audienceResearchChannelRepository.saveAll(audienceResearch.getChannels());

        createServiceChannels(
                id,
                audienceResearch.getTargetChannel().getId(),
                audienceResearch.getChannels().stream()
                    .map(c -> c.getChannel().getId())
                    .collect(Collectors.toList()));

        return id;
    }

    @Override
    @Transactional
    @Restrict(restriction = "audienceResearch.edit")
    public Long update(AudienceResearch audienceResearch) {
        AudienceResearch existing = audienceResearchRepository.findById(audienceResearch.getId()).orElse(null);
        audienceResearch.setStatus(existing.getStatus());
        audienceResearch.setTargetChannel(existing.getTargetChannel());
        audienceResearch.setAdvertisers(fetchAccountsByIds(audienceResearch.getAdvertisers()));

        // find channels to delete
        Set<Long> researchChannelIds = audienceResearch.getChannels().stream()
                .filter(c -> c.getId() != null)
                .map(c -> c.getId())
                .collect(Collectors.toSet());

        List<Long> deleteIds = existing.getChannels().stream()
            .filter(c -> c.getId() != null && !researchChannelIds.contains(c.getId()))
            .map(c -> {
                c.setStatus(Status.DELETED);
                return c.getChannel().getId();
            })
            .collect(Collectors.toList());
        deleteServiceChannels(audienceResearch.getId(), deleteIds);

        // find channels to add or update
        Set<Long> existingResearchChannelIds = existing.getChannels().stream()
                .filter(c -> c.getId() != null)
                .map(c -> c.getId())
                .collect(Collectors.toSet());

        // find channels to update
        audienceResearch.getChannels().stream()
                .filter(c -> existingResearchChannelIds.contains(c.getId()))
                .forEach( c -> {
                    AudienceResearchChannel existingChannel = audienceResearchChannelRepository.findById(c.getId()).orElse(null);
                    existingChannel.setChartType(c.getChartType());
                    existingChannel.setSortOrder(c.getSortOrder());
                });

        // find channels to add
        List<AudienceResearchChannel> addResearchChannels = audienceResearch.getChannels().stream()
                .filter(c -> !existingResearchChannelIds.contains(c.getId()))
                .map( c -> prePersistChannel(c, audienceResearch))
                .collect(Collectors.toList());
        audienceResearchChannelRepository.saveAll(addResearchChannels);

        createServiceChannels(audienceResearch.getId(),
                              audienceResearch.getTargetChannel().getId(),
                              addResearchChannels.stream()
                                      .map(c -> c.getChannel().getId())
                                      .collect(Collectors.toList()));

        Timestamp startDate = audienceResearch.getChannels().stream()
                .map(c -> c.getStartDate())
                .sorted()
                .findAny().get();
        audienceResearch.setStartDate(startDate);

        return audienceResearchRepository.save(audienceResearch).getId();
    }

    @Override
    @Restrict(restriction = "audienceResearch.edit")
    public Long updateYesterdayComment(String comment, Long id) {
        AudienceResearchChannel channel = audienceResearchChannelRepository.findById(id).orElse(null);
        channel.setYesterdayComment(comment);
        audienceResearchChannelRepository.save(channel);
        return channel.getId();
    }

    @Override
    @Restrict(restriction = "audienceResearch.edit")
    public Long updateTotalComment(String comment, Long id) {
        AudienceResearchChannel channel = audienceResearchChannelRepository.findById(id).orElse(null);
        channel.setTotalComment(comment);
        audienceResearchChannelRepository.save(channel);
        return channel.getId();
    }

    @Override
    @Transactional
    @Restrict(restriction = "audienceResearch.edit")
    public Long delete(Long id) {
        AudienceResearch audienceResearch = audienceResearchRepository.findById(id).orElse(null);
        audienceResearch.setStatus(Status.DELETED);

        audienceResearch.getChannels().forEach(c -> {
            c.setStatus(Status.DELETED);
        });

        deleteServiceChannels(id);

        return id;
    }

    private void createServiceChannels(Long researchId, Long targetChannelId, List<Long> channelIds) {
        if (channelIds.isEmpty()) {
            return;
        }

        List<ExpressionChannel> serviceChannels = new ArrayList<>();

        List<com.foros.rs.client.model.advertising.channel.Channel> channels = channelService.findChannels(channelIds);
        for (com.foros.rs.client.model.advertising.channel.Channel channel : channels) {
            List<Long> ids = findChannelsFromExpression(channel);
            for (Long id : ids) {
                ExpressionChannel serviceChannel = new ExpressionChannel();
                serviceChannel.setName(SERVICE_CHANNELS_NAME_PREFIX + researchId + "-" + channel.getId() + "-" + id);
                serviceChannel.setCountry(LOCALE_RU.getCountry());
                serviceChannel.setAccount(new EntityLink());
                serviceChannel.getAccount().setId(authorizationService.getAuthUser().getAccountId());
                serviceChannel.setExpression(targetChannelId + "&" + id);
                serviceChannels.add(serviceChannel);
            }
        }

        channelService.createOrUpdateExpressions(serviceChannels);
    }

    private List<Long> findChannelsFromExpression(com.foros.rs.client.model.advertising.channel.Channel channel) {
        if (!(channel instanceof ExpressionChannel)) {
            return new ArrayList<>();
        }
        return Arrays.stream(((ExpressionChannel) channel).getExpression().split("\\|"))
                .map(c -> {
                    try {
                        return Long.parseLong(c);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(c -> c!= null)
                .collect(Collectors.toList());
    }

    private void deleteServiceChannels(Long researchId, List<Long> expressionChannelIds) {
        if (expressionChannelIds.isEmpty()) {
            return;
        }

        List<com.foros.rs.client.model.advertising.channel.Channel> expressionChannels = channelService.findChannels(expressionChannelIds);
        List<ExpressionChannel> serviceChannels = expressionChannels.stream()
                .map(c -> findServiceChannelIds(researchId, c))
                .flatMap(List::stream)
                .map(id -> instantiateDeletedChannel(id))
                .collect(Collectors.toList());

        channelService.createOrUpdateExpressions(serviceChannels);
    }

    private void deleteServiceChannels(Long researchId) {
        List<ExpressionChannel> serviceChannels = findServiceChannelIds(researchId, null).stream()
                .map(id -> instantiateDeletedChannel(id))
                .collect(Collectors.toList());
        channelService.createOrUpdateExpressions(serviceChannels);
    }

    private List<Long> findServiceChannelIds(Long researchId, com.foros.rs.client.model.advertising.channel.Channel expressionChannel) {
        return channelService.findAllChannels(
                SERVICE_CHANNELS_NAME_PREFIX + researchId + "-" + (expressionChannel != null ? expressionChannel.getId() + "-" : ""),
                authorizationService.getAuthUser().getAccountId(),
                ChannelType.E,
                null
        ).stream()
                .map(c -> c.getId())
                .collect(Collectors.toList());
    }

    private List<AccountEntity> fetchAccountsByIds(List<AccountEntity> ids) {
        Iterable<AccountEntity> accounts = accountRepository.findAllById(
                ids.stream()
                    .map( a -> a.getId() )
                    .collect(Collectors.toList())
        );

        List<AccountEntity> result = new ArrayList<>(ids.size());
        accounts.forEach(result::add);

        return result;
    }

    private AudienceResearchChannel prePersistChannel(AudienceResearchChannel channel,
                                                      AudienceResearch audienceResearch) {
        channel.setStatus(Status.ACTIVE);
        channel.setAudienceResearch(audienceResearch);
        channel.setStartDate(new Timestamp(System.currentTimeMillis()));
        channel.setChannel(channelRepository.findById(channel.getChannel().getId()).orElse(null));

        return channel;
    }

    private ExpressionChannel instantiateDeletedChannel(Long serviceChannelId) {
        ExpressionChannel serviceChannel = new ExpressionChannel();
        serviceChannel.setId(serviceChannelId);
        serviceChannel.setStatus(com.foros.rs.client.model.entity.Status.DELETED);

        return serviceChannel;
    }
}
