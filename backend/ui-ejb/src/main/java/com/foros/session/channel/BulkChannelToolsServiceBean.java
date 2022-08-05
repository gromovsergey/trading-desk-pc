package com.foros.session.channel;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.config.ConfigService;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.TooManyRowsException;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.account.AccountService;
import com.foros.session.channel.exceptions.ChannelNotFoundExpressionException;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.exceptions.UndistinguishableExpressionException;
import com.foros.session.channel.exceptions.UnreachableExpressionException;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.channel.service.ExpressionService;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.channel.AdvertisingChannelQueryImpl;
import com.foros.util.Stats;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.VersionCollisionException;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.ValidationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "BulkChannelToolsService")
@Interceptors({ RestrictionInterceptor.class, PersistenceExceptionInterceptor.class })
public class BulkChannelToolsServiceBean implements BulkChannelToolsService {
    public static final int MAX_FETCH_SIZE = 500;
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AccountService accountService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private BehavioralChannelService behavioralChannelService;

    @EJB
    private ExpressionChannelService expressionChannelService;

    @EJB
    private ExpressionService expressionService;

    @EJB
    private ConfigService config;

    @EJB
    private ValidationService validationService;

    @EJB
    private TriggerService triggerService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private QueryExecutorService executorService;

    private static final Set<String> FATAL_PATHS = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add("account");
            add("account.name");
            add("name");
            add("country");
            add("country.countryCode");
        }
    });

    @Override
    @Restrict(restriction = "AdvertisingChannel.export", parameters = "find('Account', #accountId)")
    public Collection<? extends Channel> findForExport(
            Long accountId, AdvertisingChannelType channelType, Collection<Long> ids, int maxResultSize)
            throws TooManyRowsException {
        Collection<? extends Channel> channels;
        switch (channelType) {
        case BEHAVIORAL:
            channels = findBehavioralChannels(ids, accountId, maxResultSize);
            break;
        case EXPRESSION:
            channels = findExpressionChannels(ids, accountId, maxResultSize);
            break;
        default:
            throw new IllegalArgumentException("Bad channel type: " + channelType);
        }
        return channels;
    }

    private Collection<Channel> findExpressionChannels(Collection<Long> ids, Long accountId, int maxResultSize)
            throws TooManyRowsException {
        final Account channelsAccount = em.find(Account.class, accountId);

        List<Channel> channels = jdbcTemplate.query(
                "select ch.channel_id, ch.name, ch.status, ch.description, ch.country_code, ch.expression from channel ch " +
                " where ch.account_id = ? AND ch.channel_type = 'E' and ch.channel_id = any(?) AND ch.status <> 'D' " +
                " order by ch.name",
                new Object[]{
                        accountId,
                        jdbcTemplate.createArray("bigint", ids)
                },
                new RowMapper<Channel>() {
                    @Override
                    public Channel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ExpressionChannel channel = new ExpressionChannel();
                        channel.setId(rs.getLong("channel_id"));
                        channel.setName(rs.getString("name"));
                        channel.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                        channel.setDescription(rs.getString("description"));
                        channel.setCountry(new Country(rs.getString("country_code")));
                        channel.setExpression(convertExpressionToHuman(rs.getString("expression")));
                        channel.setAccount(channelsAccount);
                        return channel;
                    }
                });

        if (channels.size() > maxResultSize) {
            throw new TooManyRowsException();
        }
        return channels;
    }

    private String convertExpressionToHuman(String expression) {
        try {
            return expressionService.convertToHumanReadable(expression);
        } catch (ExpressionConversionException e) {
            return expression;
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression: " + expression, e);
        }
    }

    private Collection<BehavioralChannel> findBehavioralChannels(Collection<Long> ids, Long accountId, int maxResultSize)
            throws TooManyRowsException {
        final Account channelsAccount = em.find(Account.class, accountId);

        List<BehavioralChannel> list = jdbcTemplate.query(
                "select ch.channel_id, ch.name, ch.status, ch.description, ch.country_code  " +
                " from channel ch " +
                " where ch.account_id = ? and ch.channel_type = 'B' " +
                " and ch.channel_id = any(?) " +
                " and ch.status <> 'D' order by  ch.name",
                new Object[]{
                        accountId,
                        jdbcTemplate.createArray("bigint", ids)
                },
                new RowMapper<BehavioralChannel>() {
                    @Override
                    public BehavioralChannel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        BehavioralChannel channel = new BehavioralChannel();
                        channel.setId(rs.getLong("channel_id"));
                        channel.setName(rs.getString("name"));
                        channel.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                        channel.setDescription(rs.getString("description"));
                        channel.setCountry(new Country(rs.getString("country_code")));
                        channel.setAccount(channelsAccount);
                        return channel;
                    }
                }
        );

        if (list.size() > maxResultSize) {
            throw new TooManyRowsException();
        }

        Map<Long, BehavioralChannel> channels = new HashMap<>(list.size());
        for (BehavioralChannel channel : list) {
            channels.put(channel.getId(), channel);
        }
        setTriggers(channels);

        return channels.values();
    }

    @Override
    public void setTriggers(Map<Long, BehavioralChannel> channels) throws TooManyTriggersException {
        Map<Long, Set<BehavioralParameters>> paramsMap = findBehavioralParameters(channels.keySet());
        Map<Long, Set<ChannelTrigger>> triggersMap = findTriggers(new ArrayList<>(channels.keySet()));

        for (Channel channel : channels.values()) {
            Set<BehavioralParameters> params = paramsMap.get(channel.getId());
            if (params != null) {
                ((BehavioralChannel) channel).getBehavioralParameters().addAll(params);
            }

            Set<ChannelTrigger> triggers = triggersMap.get(channel.getId());
            ((BehavioralChannel) channel).resetTriggers(triggers);
        }
    }

    private Map<Long, Set<ChannelTrigger>> findTriggers(List<Long> ids) throws TooManyTriggersException {
        if (ids.isEmpty()) {
            return new HashMap<Long, Set<ChannelTrigger>>(ids.size());
        }
        return triggerService.getTriggersByChannelIds(ids, false);
    }

    private Map<Long, Set<BehavioralParameters>> findBehavioralParameters(Collection<Long> ids) {
        final Map<Long, Set<BehavioralParameters>> paramsMap = new HashMap<Long, Set<BehavioralParameters>>(ids.size());
        if (ids.isEmpty()) {
            return paramsMap;
        }

        String sql = "select bp.channel_id, bp.trigger_type, bp.minimum_visits, bp.time_from, bp.time_to " +
                " from BehavioralParameters bp  where bp.channel_id = any(?)";

        jdbcTemplate.query(new PreparedStatementCreatorFactory(sql,
            new int[] { Types.ARRAY }).
            newPreparedStatementCreator(Arrays.asList(jdbcTemplate.createArray("bigint", ids))),
            new RowMapper() {

                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long channelId = (long) rs.getInt(1);
                    Set<BehavioralParameters> set = paramsMap.get(channelId);
                    if (set == null) {
                        set = new HashSet<BehavioralParameters>();
                    }
                    BehavioralParameters bp = new BehavioralParameters();
                    bp.setTriggerType(rs.getString(2).charAt(0));
                    bp.setMinimumVisits((long) rs.getInt(3));
                    bp.setTimeFrom((long) rs.getInt(4));
                    bp.setTimeTo((long) rs.getInt(5));
                    set.add(bp);
                    paramsMap.put(channelId, set);
                    return set;
                }
            });

        return paramsMap;
    }

    @Override
    public ValidationResultTO validateAll(AdvertisingChannelType channelType, List<? extends Channel> channels) {
        validateAll(channels, channelType);
        ValidationResultTO result = fillValidationResult(channels, channelType);
        String validationId = saveResults(accountService.getMyAccount().getId(), channels);
        result.setId(validationId);
        return result;
    }

    private void validateAll(List<? extends Channel> channels, AdvertisingChannelType channelType) {
        if (channels.isEmpty()) {
            return;
        }

        AccountChannelsMap accountChannelsMap = new AccountChannelsMap();
        ExpressionService.ConverterContext converterContext = expressionService.newContext();
        DuplicateChecker<Channel> duplicateChecker = DuplicateChecker.create(new DuplicateChecker.IdentifierFetcher<Channel>() {
            @Override
            public Object fetch(Channel entity) {
                return Arrays.asList(entity.getAccount().getName(), entity.getName(), entity.getCountry().getCountryCode());
            }
        });

        for (Channel channel : channels) {
            UploadContext uploadContext = UploadUtils.getUploadContext(channel);

            if (uploadContext.isFatal() || CollectionUtils.containsAny(uploadContext.getWrongPaths(), FATAL_PATHS)) {
                uploadContext.setFatal();
                continue;
            }

            Account account = accountChannelsMap.findExistingAccount(channel);
            if (account == null) {
                uploadContext.addFatal("errors.entity.notFound")
                    .withPath("account.name");
                continue;
            }

            if (!advertisingChannelRestrictions.canUpload(account)) {
                uploadContext.addFatal("errors.operation.not.permitted")
                    .withPath("account");
                continue;
            }
            channel.getAccount().setId(account.getId());

            ExistingChannelTO existingTO = accountChannelsMap.findExistingChannel(channel);

            // update entity
            if (existingTO != null && existingTO.getChannelType() != channelType) {
                uploadContext
                    .addFatal("errors.channelAlreadyExists")
                    .withParameters(channel.getName())
                    .withPath("name");
                continue;
            }

            if (!duplicateChecker.check(channel)) {
                uploadContext
                    .addFatal("errors.duplicate.name")
                    .withParameters(channel.getName())
                    .withPath("name");
                continue;
            }

            if (existingTO != null) {
                uploadContext.mergeStatus(UploadStatus.UPDATE);
                Long id = existingTO.getId();
                channel.setId(id);
                channel.setVersion(existingTO.getVersion());
            } else {
                uploadContext.mergeStatus(UploadStatus.NEW);
            }
        }

        int lastFetched = -1;
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);

            if (i > lastFetched) {
                lastFetched = fetch(channels, channelType, lastFetched);
            }

            UploadContext uploadContext = UploadUtils.getUploadContext(channel);

            if (uploadContext.isFatal()) {
                continue;
            }

            if (uploadContext.getStatus() == UploadStatus.UPDATE) {
                Channel existing = em.find(channelType.getType(), channel.getId());

                if (Status.DELETED == existing.getStatus()) {
                    UploadUtils.getUploadContext(channel)
                        .addError("errors.entity.deleted");
                    continue;
                }
            }

            String expression = null;
            if (AdvertisingChannelType.EXPRESSION == channelType) {
                expression = ((ExpressionChannel) channel).getExpression();
                convertExpressionFromHuman(converterContext, (ExpressionChannel) channel);
            }

            // validate fields
            ValidationContext context = validationService.validate(
                ValidationStrategies.exclude(uploadContext.getWrongPaths()),
                "BulkChannel.createOrUpdate",
                channel
                );

            Set<ConstraintViolation> violations = context.getConstraintViolations();
            UploadUtils.setErrors(channel, violations);

            if (AdvertisingChannelType.EXPRESSION == channelType) {
                ((ExpressionChannel) channel).setExpression(expression);
            }
        }
    }

    private int fetch(List<? extends Channel> channels, AdvertisingChannelType channelType, int lastFetched) {
        List<Long> channelIds = new ArrayList<>(Math.min(channels.size(), MAX_FETCH_SIZE));
        int i;
        for (i = lastFetched + 1; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            if (channel.getId() != null) {
                channelIds.add(channel.getId());
            }

            if (channelIds.size() >= MAX_FETCH_SIZE) {
                break;
            }
        }

        if (!channelIds.isEmpty()) {
            List<Channel> fetchedList = new AdvertisingChannelQueryImpl()
                .matchedIds(new ArrayList<Long>(channelIds))
                .asBean()
                .executor(executorService)
                .list();

            if (channelType == AdvertisingChannelType.BEHAVIORAL) {
                Map<Long, Set<ChannelTrigger>> triggers = triggerService.getTriggersByChannelIds(channelIds, false);
                for (Channel channel : fetchedList) {
                    Set<ChannelTrigger> channelTriggers = triggers.get(channel.getId());
                    ((BehavioralChannel) channel).resetTriggers(channelTriggers);
                }
            }
        }

        return i;
    }

    private void checkExpressions(Collection<ExpressionChannel> channels) {
        ExpressionService.ConverterContext converterContext = expressionService.newContext();
        for (ExpressionChannel expressionChannel : channels) {
            convertExpressionFromHuman(converterContext, expressionChannel);
        }
    }

    private void convertExpressionFromHuman(ExpressionService.ConverterContext converterContext, ExpressionChannel channel) {
        String expression = channel.getExpression();
        if (StringUtil.isPropertyNotEmpty(expression) && channel.getCountry() != null
                && channel.getCountry().getCountryCode() != null) {
            try {
                channel.setExpression(expressionService.convertFromHumanReadable(converterContext, expression, channel.getCountry().getCountryCode()));
            } catch (ExpressionConversionException e) {
                ValidationContext context = ValidationUtil.validationContext(channel).build();
                if (e instanceof UnreachableExpressionException) {
                    context.addConstraintViolation("errors.wrong.cdml")
                        .withPath("expression");
                } else if (e instanceof ChannelNotFoundExpressionException) {
                    context.addConstraintViolation("errors.channelNotFound")
                        .withPath("expression")
                        .withParameters(e.getName());
                } else if (e instanceof UndistinguishableExpressionException) {
                    context.addConstraintViolation(e.getMessage())
                        .withPath("expression")
                        .withParameters(e.getName());
                } else {
                    context.addConstraintViolation("errors.expression")
                        .withPath("expression")
                        .withParameters(expression);
                }
                UploadUtils.setErrors(channel, context.getConstraintViolations());
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public Collection<Channel> getValidatedResults(String validationResultId) {
        FileSystem fs = getBulkPP(accountService.getMyAccount().getId()).createFileSystem();
        ObjectInputStream ois = null;
        List<Channel> channels = null;
        try {
            InputStream is = fs.readFile(getFileName(validationResultId, "channels"));
            ois = new ObjectInputStream(is);
            channels = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
        return channels;
    }

    @Override
    @Restrict(restriction = "AdvertisingChannel.upload")
    public void createOrUpdateAll(AdvertisingChannelType channelType, String validationResultId) {
        Long accountId = accountService.getMyAccount().getId();
        try {
            startProcessing(accountId, validationResultId);
            Collection<? extends Channel> channels = getValidatedResults(validationResultId);
            switch (channelType) {
            case EXPRESSION:
                checkExpressions((Collection<ExpressionChannel>) channels);
                expressionChannelService.createOrUpdateAll(null, (Collection<ExpressionChannel>) channels);
                break;
            case BEHAVIORAL:
                behavioralChannelService.createOrUpdateAll(null, (Collection<BehavioralChannel>) channels);
                break;
            default:
                throw new IllegalArgumentException("Bad channel type: " + channelType);
            }
            markProcessed(accountId, validationResultId);
        } catch (OptimisticLockException | VersionCollisionException | BusinessException e) {
            throwReuploadRequired(e);
        } finally {
            endProcessing(accountId, validationResultId);
        }
    }

    private void throwReuploadRequired(RuntimeException e) {
        String msg = StringUtil.getLocalizedString("channel.upload.version");
        throw new BusinessException(msg, e);
    }

    private void endProcessing(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String lock = getFileName(validationResultId, "lock");
        fs.delete(lock);
    }

    private void markProcessed(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String submitted = getFileName(validationResultId, "submitted");
        fs.lock(submitted);
    }

    private void startProcessing(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String lock = getFileName(validationResultId, "lock");
        if (!fs.lock(lock)) {
            throw ConstraintViolationException.newBuilder("channel.upload.inProgress").build();
        }

        String submitted = getFileName(validationResultId, "submitted");
        if (fs.checkExist(submitted)) {
            throw ConstraintViolationException.newBuilder("channel.upload.alreadySubmitted").build();
        }
    }

    private ValidationResultTO fillValidationResult(Collection<? extends Channel> channels, AdvertisingChannelType channelType) {
        ValidationResultTO validationResult = new ValidationResultTO();
        validationResult.setChannelType(channelType);
        for (Channel channel : channels) {
            addValidationResult(channel, validationResult, validationResult.getChannels());
        }
        return validationResult;
    }

    private String saveResults(Long accountId, Collection<? extends Channel> channels) {
        for (Channel channel : channels) {
            UploadContext uploadContext = UploadUtils.getUploadContext(channel);
            uploadContext.getErrors(); // forced flush
        }
        UUID uuid = UUID.randomUUID();
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        ObjectOutputStream oos = null;
        try {
            OutputStream os = fs.openFile(getFileName(uuid, "channels"));
            oos = new ObjectOutputStream(os);
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(oos);
        }
        return uuid.toString();
    }

    private String getFileName(String validationResultId, String suffix) {
        return getFileName(UUID.fromString(validationResultId), suffix);
    }

    private String getFileName(UUID uuid, String suffix) {
        return uuid.toString() + "." + suffix;
    }

    private PathProvider getBulkPP(Long accountId) {
        return pathProviderService.getBulkUpload().getNested(accountId.toString(), OnNoProviderRoot.AutoCreate);
    }

    private void addValidationResult(EntityBase entity, ValidationResultTO validationResult, Stats stats) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        switch (context.getStatus()) {
        case NEW:
            stats.setCreated(stats.getCreated() + 1);
            break;
        case UPDATE:
            stats.setUpdated(stats.getUpdated() + 1);
            break;
        case REJECTED:
            validationResult.setLineWithErrors(validationResult.getLineWithErrors() + 1);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    class ChannelIdentifierFetcher {
        private String name;
        private String countryCode;

        public ChannelIdentifierFetcher(String name, String countryCode) {
            this.name = name != null ? name.toLowerCase() : null;
            this.countryCode = countryCode;
        }

        private BulkChannelToolsServiceBean getOuterType() {
            return BulkChannelToolsServiceBean.this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ChannelIdentifierFetcher other = (ChannelIdentifierFetcher) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (countryCode == null) {
                if (other.countryCode != null)
                    return false;
            } else if (!countryCode.equals(other.countryCode))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }

    private class AccountChannels {
        private Account account;
        private Map<ChannelIdentifierFetcher, ExistingChannelTO> accountChannelsByIdentifier;

        public AccountChannels(String accountName) {
            account = findAccountByName(accountName);
            accountChannelsByIdentifier = generateAccountChannelsMap(find(account));
        }

        public Account getAccount() {
            return account;
        }

        public ExistingChannelTO findExistingChannel(ChannelIdentifierFetcher identifier) {
            return accountChannelsByIdentifier.get(identifier);
        }

        private Account findAccountByName(String accountName) {
            if (StringUtil.isPropertyEmpty(accountName)) {
                return null;
            }
            try {
                return (Account) em.createNamedQuery("Account.findByName")
                    .setParameter("name", accountName)
                    .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }

        private List<ExistingChannelTO> find(Account account) {
            if (account == null || account.getId() == null) {
                return Collections.emptyList();
            }

            String sql = "select c.id, c.name, c.country.countryCode, c.class, c.version " +
                    " from Channel c " +
                    " where c.namespace = 'A' AND c.status <> 'D' AND c.account.id = :accountId";

            //noinspection unchecked
            List<Object[]> list = em.createQuery(sql)
                .setParameter("accountId", account.getId())
                .getResultList();

            List<ExistingChannelTO> res = new ArrayList<ExistingChannelTO>(list.size());
            for (Object[] arr : list) {
                Long id = (Long) arr[0];
                String name = (String) arr[1];
                String countryCode = (String) arr[2];
                AdvertisingChannelType channelType = AdvertisingChannelType.byAlias((String) arr[3]);
                Timestamp version = (Timestamp) arr[4];

                res.add(new ExistingChannelTO(id, name, countryCode, channelType, version));
            }

            return res;
        }

        private Map<ChannelIdentifierFetcher, ExistingChannelTO> generateAccountChannelsMap(List<ExistingChannelTO> channels) {
            Map<ChannelIdentifierFetcher, ExistingChannelTO> result = new HashMap<ChannelIdentifierFetcher, ExistingChannelTO>(channels.size());
            for (ExistingChannelTO channel : channels) {
                result.put(new ChannelIdentifierFetcher(channel.getName(), channel.getCountryCode()), channel);
            }

            return result;
        }
    }

    private class AccountChannelsMap {
        Map<String, AccountChannels> accountChannelsMap = new HashMap<>();

        public Account findExistingAccount(Channel channel) {
            return findAccountChannels(channel).getAccount();
        }

        public ExistingChannelTO findExistingChannel(Channel channel) {
            String channelName = channel.getName();
            String countryCode = channel.getCountry().getCountryCode();
            return findAccountChannels(channel).findExistingChannel(new ChannelIdentifierFetcher(channelName, countryCode));
        }

        private AccountChannels findAccountChannels(Channel channel) {
            String accountName = channel.getAccount().getName();
            AccountChannels accountChannels = accountChannelsMap.get(accountName);
            if (accountChannels == null) {
                accountChannels = new AccountChannels(accountName);
                accountChannelsMap.put(accountName, accountChannels);
            }
            return accountChannels;
        }
    }

    private static class ExistingChannelTO {
        private Long id;
        private String name;
        private String countryCode;
        private AdvertisingChannelType channelType;
        private Timestamp version;

        private ExistingChannelTO(Long id, String name, String countryCode, AdvertisingChannelType channelType, Timestamp version) {
            this.id = id;
            this.name = name;
            this.countryCode = countryCode;
            this.channelType = channelType;
            this.version = version;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public AdvertisingChannelType getChannelType() {
            return channelType;
        }

        public Timestamp getVersion() {
            return version;
        }
    }
}
