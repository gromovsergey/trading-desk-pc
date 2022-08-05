package com.foros.session.channel.service;

import static com.foros.util.SQLUtil.quote;
import static com.foros.util.StringUtil.trimAndLower;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.admin.GlobalParam;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.TooManyRowsException;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.KeywordChannelCsvTO;
import com.foros.session.channel.KeywordChannelTO;
import com.foros.session.channel.TriggerService;
import com.foros.session.frequencyCap.FrequencyCapMerger;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.VersionCollisionException;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.jpa.DetachedList;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.jpa.NativeQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.hibernate.FlushMode;
import org.springframework.jdbc.core.RowCallbackHandler;

@Stateless(name = "KeywordChannelService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class KeywordChannelServiceBean extends AbstractCategoryOwnedChannelServiceBean implements KeywordChannelService {

    private static final int CHANNEL_BATCH_SIZE = 1000;

    @EJB
    private AuditService auditService;

    @EJB
    private TriggerService triggerService;

    @EJB
    private GlobalParamsService globalParamsService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private ValidationService validationService;

    @EJB
    LoggingJdbcTemplate jdbcTemplate;

    private BehavioralParametersMerger<KeywordChannel> behavioralParametersMerger = new BehavioralParametersMerger<KeywordChannel>() {
        @Override
        protected EntityManager getEM() {
            return em;
        }
    };

    private FrequencyCapMerger<KeywordChannel> frequencyCapMerger = new FrequencyCapMerger<KeywordChannel>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    @Override
    public KeywordChannel findById(Long id) {
        if (id == null) {
            throw new EntityNotFoundException();
        }
        KeywordChannel channel = em.find(KeywordChannel.class, id);
        if (channel == null) {
            throw new EntityNotFoundException();
        }
        PersistenceUtils.initialize(channel.getBehavioralParameters());
        return channel;
    }

    @Override
    @Restrict(restriction = "KeywordChannel.view", parameters = "find('Channel', #id)")
    public KeywordChannel view(Long id) {
        KeywordChannel channel = findById(id);
        PersistenceUtils.initialize(channel.getCategories());
        return channel;
    }

    @Override
    @Interceptors({ AutoFlushInterceptor.class, CaptureChangesInterceptor.class })
    public Map<String, Long> findOrCreate(Long accountId, String countryCode, KeywordTriggerType keywordTriggerType, Set<String> keywords) {
        if (keywords.isEmpty()) {
            return Collections.emptyMap();
        }

        // Trimmed lower cased channelName -> channelId
        Map<String, Long> channels = find(accountId, countryCode, keywordTriggerType, keywords);

        if (channels.size() == keywords.size()) {
            return channels;
        }
        BehavioralParameters defaultParameters = findDefaultParameters(keywordTriggerType.getTriggerType());
        for (String keyword : keywords) {
            String trimmed = trimAndLower(keyword);
            if (!channels.containsKey(trimmed)) {
                Long channelId = create(accountId, countryCode, keywordTriggerType, trimmed, defaultParameters).getId();
                channels.put(trimmed, channelId);
            }
        }

        return channels;
    }

    private KeywordChannel create(Long accountId, String countryCode, KeywordTriggerType triggerType,
                                  String keyword, BehavioralParameters defaultParameters) {
        final KeywordChannel channel;
        channel = new KeywordChannel();
        channel.setAccount(em.find(InternalAccount.class, accountId));
        channel.setName(keyword);
        Country country = em.find(Country.class, countryCode);
        String language = country.getLanguage();
        channel.setCountry(country);
        channel.setLanguage(language != null ? language : "en");

        channel.setStatusChangeDate(new Date());
        channel.setStatus(Status.ACTIVE);
        //actual QA status will be set from keyword trigger
        channel.setQaStatus(ApproveStatus.HOLD);
        channel.setDisplayStatus(Channel.PENDING_FOROS);

        channel.setTriggerType(triggerType);

        BehavioralParameters cloned = cloneParameters(defaultParameters);

        cloned.setChannel(channel);
        channel.setBehavioralParameters(Collections.singleton(cloned));
        em.persist(channel);

        auditService.audit(channel, ActionType.CREATE);
        displayStatusService.update(channel);
        triggerService.addToBulkTriggersUpdate(channel);

        return channel;
    }

    private BehavioralParameters cloneParameters(BehavioralParameters value) {
        BehavioralParameters cloned = new BehavioralParameters();
        cloned.setMinimumVisits(value.getMinimumVisits());
        cloned.setTimeFrom(value.getTimeFrom());
        cloned.setTimeTo(value.getTimeTo());
        cloned.setTriggerType(value.getTriggerType());
        cloned.setWeight(value.getWeight());
        return cloned;
    }

    // Returns a map of keyword (in lower case) -> channelId.
    private Map<String, Long> find(Long accountId, String countryCode, KeywordTriggerType triggerType, Collection<String> keywords) {
        keywords = CollectionUtils.convert(new Converter<String, String>() {
            @Override
            public String item(String value) {
                return trimAndLower(value);
            }
        }, keywords);
        IdsMapRowCallbackHandler handler = new IdsMapRowCallbackHandler();
        jdbcTemplate.query(
                "select c.name as name, c.channel_id as id from Channel c" +
                "  where c.account_id=? and c.channel_type=? and c.country_code=? and c.trigger_type=? and lower(c.name) = any(?::varchar[])",
                new Object[]{
                        accountId,
                        'K',
                        countryCode,
                        triggerType.getLetter(),
                        jdbcTemplate.createArray("varchar", keywords)},
                handler);
        return handler.getIdsMap();
    }

    private BehavioralParameters findDefaultParameters(TriggerType triggerType) {
        String hql = "from BehavioralParameters where id in :ids and triggerType = :triggerType";
        QueryWrapper<BehavioralParameters> query = new JpaQueryWrapper<BehavioralParameters>(em, hql);
        query.setArrayParameter("ids", getDefaultParameterIds());
        query.setParameter("triggerType", triggerType.getLetter());
        return query.getSingleResult();
    }

    private Set<Long> getDefaultParameterIds() {
        GlobalParam page = getPageParam();
        GlobalParam search = getSearchParam();

        Set<Long> defaultParameterIds = new HashSet<Long>(2);
        defaultParameterIds.add(StringUtil.toLong(page.getValue(), true));
        defaultParameterIds.add(StringUtil.toLong(search.getValue(), true));
        return defaultParameterIds;
    }

    @Override
    @Restrict(restriction = "KeywordChannel.update", parameters = "find('KeywordChannel', #channel.id)")
    @Validate(validation = "KeywordChannel.update", parameters = "#channel")
    @Interceptors({ AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public Long update(KeywordChannel channel) {
        KeywordChannel existing = findById(channel.getId());
        return updateInternal(channel, existing);
    }

    private Long updateInternal(KeywordChannel channel, KeywordChannel existing) {
        channel.retainChanges(
                "behavioralParameters",
                "frequencyCap"
        );
        // prepare
        ChannelFieldsPreparer.prepareBehavioralParameters(channel);

        // set frequency caps
        FrequencyCap frequencyCap = channel.getFrequencyCap();
        if (frequencyCap != null && frequencyCap.isEmpty()) {
            channel.setFrequencyCap(null);
        }

        // merge
        frequencyCapMerger.merge(channel, existing);
        behavioralParametersMerger.merge(channel, existing);
        existing = em.merge(channel);
        // post update
        auditService.audit(existing, ActionType.UPDATE);

        return existing.getId();
    }

    @Override
    @Restrict(restriction = "KeywordChannel.view")
    public DetachedList<KeywordChannelTO> search(int firstRow, int maxResults, String name, Long accountId, String countryCode,
                                                DisplayStatus... displayStatuses) {
        StringBuilder queryBuilder = new StringBuilder(
                "select ch.channel_id, ch.name as channelName, ch.trigger_type," +
                        " ac.account_id, ac.name as accountName, ac.display_status_id as accDispStatusId," +
                        " ch.country_code, ch.display_status_id" +
                        " from channel ch left join account ac on ch.account_id = ac.account_id where ch.channel_type = 'K'");
        queryBuilder = generateConditions(queryBuilder, name, accountId, countryCode, displayStatuses)
                .append(" order by ch.name, ch.trigger_type");

        QueryWrapper<Object[]> query = new NativeQueryWrapper<Object[]>(em, queryBuilder.toString());
        if (StringUtil.isPropertyNotEmpty(name)) {
            query.setParameter("name", SQLUtil.getLikeEscape(name));
        }
        if (StringUtil.isPropertyNotEmpty(countryCode)) {
            query.setParameter("countryCode", countryCode);
        }
        if (accountId != null) {
            query.setParameter("accountId", accountId);
        }

        DetachedList<Object[]> list = query.getDetachedList(firstRow, maxResults);
        List<KeywordChannelTO> result = new ArrayList<KeywordChannelTO>(list.size());
        for (Object[] row : list) {
            KeywordChannelTO channelTO = new KeywordChannelTO();
            channelTO.setId(((Number) row[0]).longValue());
            channelTO.setName((String) row[1]);
            channelTO.setTriggerType(KeywordTriggerType.byLetter((Character) row[2]));
            channelTO.getAccount().setId(((Number) row[3]).longValue());
            channelTO.getAccount().setName((String) row[4]);
            channelTO.getAccount().setDisplayStatus(Account.getDisplayStatus(((Number) row[5]).longValue()));
            channelTO.setCountryCode((String) row[6]);
            DisplayStatus displayStatus = Channel.getDisplayStatus(((Number) row[7]).longValue());
            channelTO.setDisplayStatus(displayStatus);
            result.add(channelTO);
        }
        Collections.sort(result, new IdNameComparator());
        return new DetachedList<KeywordChannelTO>(result, list.getTotal());
    }

    @Override
    @Restrict(restriction = "KeywordChannel.view")
    public Collection<KeywordChannelCsvTO> export(int maxResultSize, String name, Long accountId, String countryCode,
                                                  DisplayStatus... displayStatuses) throws TooManyRowsException {
        StringBuilder queryBuilder = new StringBuilder(
                "select ch.channel_id, ch.name as channelName, ch.trigger_type, ac.name as accountName, ch.country_code, " +
                        " fc.period, fc.window_length, fc.window_count, fc.life_count" +
                        " from channel ch" +
                        " left join account ac on ch.account_id = ac.account_id" +
                        " left join freqcap fc on ch.freq_cap_id = fc.freq_cap_id" +
                        " where ch.channel_type = 'K'");
        queryBuilder = generateConditions(queryBuilder, name, accountId, countryCode, displayStatuses).append(" order by ch.name");
        Query query = fillConditions(em.createNativeQuery(queryBuilder.toString()), name, accountId, countryCode);

        @SuppressWarnings("unchecked")
        List<Object[]> list = query.setMaxResults(maxResultSize).getResultList();

        if (list.size() == 0) {
            return Collections.emptyList();
        }

        queryBuilder = new StringBuilder("select bp.channel_id, bp.trigger_type, bp.minimum_visits, bp.time_from, bp.time_to" +
                " from behavioralparameters bp left join channel ch on ch.channel_id = bp.channel_id where ch.channel_type = 'K'");
        queryBuilder = generateConditions(queryBuilder, name, accountId, countryCode, displayStatuses);
        query = fillConditions(em.createNativeQuery(queryBuilder.toString()), name, accountId, countryCode);

        @SuppressWarnings("unchecked")
        List<Object[]> params = query.getResultList();

        Map<Long, Set<BehavioralParameters>> paramsMap = new HashMap<Long, Set<BehavioralParameters>>(list.size());

        for (Object[] row : params) {
            Long channelId = ((Number) row[0]).longValue();
            Set<BehavioralParameters> set = paramsMap.get(channelId);
            if (set == null) {
                set = new HashSet<BehavioralParameters>();
            }
            BehavioralParameters bp = new BehavioralParameters();
            bp.setTriggerType((Character) row[1]);
            bp.setMinimumVisits(((Number) row[2]).longValue());
            bp.setTimeFrom(((Number) row[3]).longValue());
            bp.setTimeTo(((Number) row[4]).longValue());
            set.add(bp);
            paramsMap.put(channelId, set);
        }

        List<KeywordChannelCsvTO> result = new ArrayList<KeywordChannelCsvTO>(list.size());
        for (Object[] row : list) {
            Set<BehavioralParameters> set = paramsMap.get(((Number) row[0]).longValue());

            FrequencyCap fc = new FrequencyCap();
            fc.setPeriod(row[5] != null ? ((Number) row[5]).intValue() : null);
            fc.setWindowLength(row[6] != null ? ((Number) row[6]).intValue() : null);
            fc.setWindowCount(row[7] != null ? ((Number) row[7]).intValue() : null);
            fc.setLifeCount(row[8] != null ? ((Number) row[8]).intValue() : null);

            result.add(new KeywordChannelCsvTO((String) row[1], (Character) row[2], (String) row[3], (String) row[4], set, fc));
        }
        return result;
    }

    private StringBuilder generateConditions(StringBuilder queryBuilder, String name, Long accountId, String countryCode,
                                             DisplayStatus... displayStatuses) {
        if (currentUserService.isInternalWithRestrictedAccess()) {
            queryBuilder.append(" and ").append(SQLUtil.formatINClause("ch.account_id", currentUserService.getAccessAccountIds()));
        }

        if (StringUtil.isPropertyNotEmpty(name)) {
            queryBuilder.append(" and UPPER(ch.name) like :name ESCAPE '\\'");
        }
        if (StringUtil.isPropertyNotEmpty(countryCode)) {
            queryBuilder.append(" and ch.country_code = :countryCode");
        }
        if (accountId != null) {
            queryBuilder.append(" and ch.account_id = :accountId");
        }
        if (displayStatuses != null && displayStatuses.length > 0) {
            queryBuilder.append(" and ch.display_status_id in (");
            for (DisplayStatus displayStatus : displayStatuses) {
                queryBuilder.append(displayStatus.getId()).append(",");
            }
            queryBuilder = queryBuilder.deleteCharAt(queryBuilder.length() - 1).append(")");
        }
        return queryBuilder;
    }

    private Query fillConditions(Query query, String name, Long accountId, String countryCode) {
        if (StringUtil.isPropertyNotEmpty(name)) {
            query.setParameter("name", SQLUtil.getLikeEscape(name));
        }
        if (StringUtil.isPropertyNotEmpty(countryCode)) {
            query.setParameter("countryCode", countryCode);
        }
        if (accountId != null) {
            query.setParameter("accountId", accountId);
        }
        return query;
    }

    @Override
    public GlobalParam getSearchParam() {
        return globalParamsService.find(GlobalParamsService.KEYWORD_CHANNEL_SEARCH_BEHAV_PARAMS_ID);
    }

    @Override
    public GlobalParam getPageParam() {
        return globalParamsService.find(GlobalParamsService.KEYWORD_CHANNEL_PAGE_BEHAV_PARAMS_ID);
    }

    @Override
    public BehavioralParameters getDefaultParameters(GlobalParam param) {
        if (param != null && param.getValue() != null) {
            Long id = Long.parseLong(param.getValue());
            return em.find(BehavioralParameters.class, id);
        }
        return null;
    }

    @Override
    public DefaultKeywordSettingsTO findDefaultKeywordSettings() {
        DefaultKeywordSettingsTO settingsTO = new DefaultKeywordSettingsTO();
        putParams(settingsTO, getSearchParam(), TriggerType.SEARCH_KEYWORD.getLetter());
        putParams(settingsTO, getPageParam(), TriggerType.PAGE_KEYWORD.getLetter());
        return settingsTO;
    }

    private void putParams(DefaultKeywordSettingsTO settingsTO, GlobalParam globalParam, Character triggerType) {
        if (globalParam == null) {
            return;
        }

        settingsTO.getVersions().put(triggerType, globalParam.getVersion());
        BehavioralParameters parameters = getDefaultParameters(globalParam);
        if (parameters != null) {
            settingsTO.getBehavioralParameters().add(parameters);
        }
    }

    @Override
    @Restrict(restriction = "GlobalParams.update")
    @Validate(validation = "KeywordChannel.defaultParameters", parameters = "#settingsTO")
    public void updateDefaultParameters(DefaultKeywordSettingsTO settingsTO) {
        Timestamp searchVersion = settingsTO.getVersions().get(TriggerType.SEARCH_KEYWORD.getLetter());
        Timestamp pageVersion = settingsTO.getVersions().get(TriggerType.PAGE_KEYWORD.getLetter());

        Collection<BehavioralParameters> behavioralParameters = settingsTO.getBehavioralParameters();

        BehavioralParameters searchParam = TriggerType.findBehavioralParameters(behavioralParameters, TriggerType.SEARCH_KEYWORD);
        BehavioralParameters pageParam = TriggerType.findBehavioralParameters(behavioralParameters, TriggerType.PAGE_KEYWORD);

        updateDefaultParameters(GlobalParamsService.KEYWORD_CHANNEL_SEARCH_BEHAV_PARAMS_ID, searchVersion, searchParam);
        updateDefaultParameters(GlobalParamsService.KEYWORD_CHANNEL_PAGE_BEHAV_PARAMS_ID, pageVersion, pageParam);
    }

    private void updateDefaultParameters(String name, Timestamp version, BehavioralParameters value) {
        GlobalParam param = globalParamsService.find(name);

        if ((param != null) && (!param.getVersion().equals(version))) {
            throw new VersionCollisionException();
        }

        String id = null;
        if (value != null) {
            Long existingId = (param == null || param.getValue() == null ? null : Long.parseLong(param.getValue()));
            if (existingId == null) {
                value.setId(null);
                em.persist(value);
            } else {
                BehavioralParameters existing = em.find(BehavioralParameters.class, existingId);
                value.setId(existingId);
                value.setVersion(existing.getVersion());
                em.merge(value);
            }

            id = value.getId().toString();
        } else {
            BehavioralParameters existing = getDefaultParameters(param);
            if (existing != null) {
                em.remove(existing);
            }
        }

        if (param == null) {
            em.persist(new GlobalParam(name, id));
        } else {
            param.setValue(id);
            PersistenceUtils.performHibernateLock(em, param);
        }
    }

    @Override
    @Restrict(restriction = "KeywordChannel.update")
    @Validate(validation = "KeywordChannel.updateAll", parameters = "#channels")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void updateAll(List<KeywordChannel> channels) {
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        List<KeywordChannel> channelsSorted = new ArrayList<KeywordChannel>(channels);
        // Sorting is necessary to avoid deadlock when 2 transaction update(insert) the same list of channels in different order
        Collections.sort(channelsSorted, new Comparator<KeywordChannel>() {
            @Override
            public int compare(KeywordChannel o1, KeywordChannel o2) {
                int res = o1.getName().compareTo(o2.getName());
                if (res == 0) {
                    res = getAccountName(o1).compareTo(getAccountName(o2));
                }
                if (res == 0) {
                    res = getCountryCode(o1).compareTo(getCountryCode(o2));
                }
                return res;
            }
        });

        Set<ConstraintViolation> violations = new TreeSet<ConstraintViolation>(new Comparator<ConstraintViolation>() {
            Pattern pattern = Pattern.compile("channels\\[(\\d+)\\].*");
            @Override
            public int compare(ConstraintViolation o1, ConstraintViolation o2) {
                Matcher matcher1 = pattern.matcher(o1.getPropertyPath().toString());
                Matcher matcher2 = pattern.matcher(o2.getPropertyPath().toString());
                if (matcher1.matches() && matcher2.matches()) {
                    try {
                        Integer line1 = Integer.parseInt(matcher1.group(1));
                        Integer line2 = Integer.parseInt(matcher2.group(1));
                        return line1.compareTo(line2);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
                return 0;
            }
        });

        for (int i = 0; i <= (channelsSorted.size() - 1) / CHANNEL_BATCH_SIZE; i++) {
            int fromIndex = i * CHANNEL_BATCH_SIZE;
            int toIndex = Math.min((i + 1) * CHANNEL_BATCH_SIZE, channelsSorted.size());
            List<KeywordChannel> subList = channelsSorted.subList(fromIndex, toIndex);

            List<KeywordChannel> existingList = findExisting(subList);

            Map<ChannelKey, KeywordChannel> existingMap = new HashMap<ChannelKey, KeywordChannel>();
            for (KeywordChannel channel : existingList) {
                ChannelKey key = new ChannelKey(channel.getName(), channel.getTriggerType(), channel.getAccount().getName(), channel.getCountry().getCountryCode());
                existingMap.put(key, channel);
            }

            for (KeywordChannel channel : subList) {
                ChannelKey key = new ChannelKey(channel.getName(), channel.getTriggerType(), getAccountName(channel), getCountryCode(channel));
                KeywordChannel existing = existingMap.get(key);

                violations.addAll(validationService.validate("KeywordChannel.atBulk", existing, channel).getConstraintViolations());
                if (violations.isEmpty()) {
                    channel.setId(existing.getId());
                    channel.setVersion(existing.getVersion());
                    updateInternal(channel, existing);
                }
            }

            if (violations.isEmpty()) {
                PersistenceUtils.flushAndClear(em);
            }
        }

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);
    }

    private String getAccountName(KeywordChannel channel) {
        return channel.getAccount().getName();
    }

    private String getCountryCode(KeywordChannel channel) {
        return channel.getAccount().getCountry().getCountryCode();
    }

    private List<KeywordChannel> findExisting(List<KeywordChannel> channels) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT c FROM KeywordChannel c JOIN FETCH c.behavioralParameters WHERE ");

        String accountName = null;
        String countryCode = null;
        for (KeywordChannel channel : channels) {
            if (!getAccountName(channel).equals(accountName)) {
                if (accountName != null) {
                    queryBuilder.append(")))) OR ");
                }
                accountName = getAccountName(channel);
                countryCode = getCountryCode(channel);
                queryBuilder.append("(c.account.name = ").append(quote(accountName));
                queryBuilder.append(" AND ((c.country.countryCode = ").append(quote(countryCode));
                queryBuilder.append(" AND c.name IN (").append(quote(channel.getName()));
            } else {
                if (!getCountryCode(channel).equals(countryCode)) {
                    countryCode = getCountryCode(channel);
                    queryBuilder.append(")) OR (c.country.countryCode = ").append(quote(countryCode));
                    queryBuilder.append(" AND c.name IN (").append(quote(channel.getName()));
                } else {
                    queryBuilder.append(", ").append(quote(channel.getName()));
                }
            }
        }
        queryBuilder.append("))))");

        //noinspection unchecked
        return em.createQuery(queryBuilder.toString()).getResultList();
    }

    private static class ChannelKey {

        private String name;
        private KeywordTriggerType triggerType;
        private String accountName;
        private String countryCode;

        private ChannelKey(String name, KeywordTriggerType triggerType, String accountName, String countryCode) {
            this.name = name;
            this.triggerType = triggerType;
            this.accountName = accountName;
            this.countryCode = countryCode;
        }

        public String getName() {
            return name;
        }

        public KeywordTriggerType getTriggerType() {
            return triggerType;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ChannelKey)) {
                return false;
            }

            ChannelKey key = (ChannelKey) o;

            return getName() != null && getName().equals(key.getName())
                    && getTriggerType() != null && getTriggerType().equals(key.getTriggerType())
                    && getAccountName() != null && getAccountName().equals(key.getAccountName())
                    && getCountryCode() != null && getCountryCode().equals(key.getCountryCode());

        }

        @Override
        public int hashCode() {
            int hash = 0;
            hash += (getName() != null ? getName().hashCode() : 0);
            hash += (getTriggerType() != null ? getTriggerType().hashCode() : 0);
            hash += (getAccountName() != null ? getAccountName().hashCode() : 0);
            hash += (getCountryCode() != null ? getCountryCode().hashCode() : 0);
            return hash;
        }
    }

    private class IdsMapRowCallbackHandler implements RowCallbackHandler {
        private Map<String, Long> idsMap = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            idsMap.put(trimAndLower(rs.getString("name")), rs.getLong("id"));
        }

        public Map<String, Long> getIdsMap() {
            return idsMap;
        }
    }
}
