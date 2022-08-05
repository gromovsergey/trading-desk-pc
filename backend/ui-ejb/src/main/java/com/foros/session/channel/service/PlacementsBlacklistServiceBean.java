package com.foros.session.channel.service;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.PlacementBlacklistChannel;
import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.BlacklistReason;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.model.channel.placementsBlacklist.PlacementsBlacklistWrapper;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.PlacementsBlacklistResult;
import com.foros.session.channel.PlacementsBlacklistValidationResultTO;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.EntityUtils;
import com.foros.util.Stats;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.jpa.DetachedList;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationStrategies;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;


@Stateless(name = "PlacementsBlacklistService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class, CaptureChangesInterceptor.class})
public class PlacementsBlacklistServiceBean implements PlacementsBlacklistService {
    private static final int DELETE_BATCH_SIZE = 10000;
    private static final int INSERT_BATCH_SIZE = 1000;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private UserService userService;

    @EJB
    private CountryService countryService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ValidationService validationService;

    @EJB
    protected AuditService auditService;

    @EJB
    protected DisplayStatusService displayStatusService;

    @Restrict(restriction = "PlacementsBlacklist.view")
    public DetachedList<PlacementBlacklist> getPlacementsBlacklist(String url, Country country, int from, int count) {
        List<PlacementBlacklist> result = jdbcTemplate.query(
                "select * from trigger.blacklisted_url_stats(?, ?, ?, ?)",
                new Object[]{
                        country.getCountryCode(),
                        url,
                        count,
                        from
                },
                new RowMapper<PlacementBlacklist>() {
                    @Override
                    public PlacementBlacklist mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PlacementBlacklist res = new PlacementBlacklist();
                        res.setId(rs.getLong("channel_trigger_id"));
                        res.setUrl(rs.getString("url"));
                        res.setSizeName(rs.getLong("size_id") == 0 ? StringUtil.getLocalizedString("admin.placementsBlacklist.allSizes") : rs.getString("size_name"));
                        res.setReason(BlacklistReason.bitsToSet(rs.getLong("reason")));
                        res.setDateAdded(rs.getDate("timestamp_added"));
                        res.setUser(new User(rs.getLong("user_id")));
                        res.getUser().setFirstName(rs.getString("first_name"));
                        res.getUser().setLastName(rs.getString("last_name"));

                        return res;
                    }
                }
        );

        return new DetachedList<>(result, getPlacementsBlacklistTotalCount(url, country));
    }

    @Restrict(restriction = "PlacementsBlacklist.update")
    public PlacementsBlacklistValidationResultTO validateAll(List<PlacementBlacklist> placements, Country country) {
        validateAllImpl(placements, country);
        PlacementsBlacklistValidationResultTO result = fillValidationResult(placements);
        String validationId = saveResults(userService.getMyUser().getAccount().getId(), placements, country);
        result.setId(validationId);
        return result;
    }

    @Restrict(restriction = "PlacementsBlacklist.update")
    public void createOrDropAll(String validationResultId) {
        PlacementsBlacklistResult placementsBlacklistResult = getValidatedResultsImpl(validationResultId);
        Country country = countryService.find(placementsBlacklistResult.getCountryCode());
        List<PlacementBlacklist> placementsBlacklist = placementsBlacklistResult.getPlacements();

        List<PlacementBlacklist> placementsToDrop = new ArrayList<>(placementsBlacklist.size());
        List<PlacementBlacklist> placementsToCreate = new ArrayList<>(placementsBlacklist.size());
        for (PlacementBlacklist placement : placementsBlacklist) {
            if (placement.getAction() == BlacklistAction.REMOVE) {
                placementsToDrop.add(placement);
            } else if (placement.getAction() == BlacklistAction.ADD) {
                placementsToCreate.add(placement);
            } else {
                throw new RuntimeException("Unsupported blacklist action: " + placement.getAction());
            }
        }

        Set<PlacementBlacklistChannel> channels = new HashSet<>();
        channels.addAll(createAllImpl(country, placementsToCreate));
        channels.addAll(dropAllImpl(country, placementsToDrop));

        updateStatusAllImpl(channels);
    }

    @Restrict(restriction = "PlacementsBlacklist.update")
    public void dropAll(Country country, Collection<PlacementBlacklist> placementsBlacklist) {
        for (PlacementBlacklist placement : placementsBlacklist) {
            if (placement.getAction() != BlacklistAction.REMOVE) {
                throw new RuntimeException("Unsupported blacklist action: " + placement.getAction());
            }
        }
        updateStatusAllImpl(dropAllImpl(country, placementsBlacklist));
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public Collection<PlacementBlacklist> getValidatedResults(String validationResultId) {
        return getValidatedResultsImpl(validationResultId).getPlacements();
    }

    public PlacementsBlacklistResult getValidatedResultsImpl(String validationResultId) {
        FileSystem fs = getBulkPP(userService.getMyUser().getAccount().getId()).createFileSystem();
        ObjectInputStream ois = null;
        PlacementsBlacklistResult placementsResult = null;
        try {
            InputStream is = fs.readFile(getFileName(validationResultId, "placementsBlacklist"));
            ois = new ObjectInputStream(is);
            placementsResult = (PlacementsBlacklistResult) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
        return placementsResult;
    }

    private int getPlacementsBlacklistTotalCount(String url, Country country) {
        return jdbcTemplate.queryForObject(
                "select * from trigger.blacklisted_url_stats_cnt(?::varchar, ?::varchar)",
                Integer.class,
                country.getCountryCode(), url
        );
    }

    private Set<PlacementBlacklistChannel> createAllImpl(Country country, Collection<PlacementBlacklist> placementsBlacklist) {
        if (placementsBlacklist.isEmpty()) {
            return Collections.emptySet();
        }

        Date currentDate = new Date();
        Map<Long, PlacementBlacklistChannel> channelsBySizeId = new HashMap<>();
        List<PlacementBlacklist> placementsToAdd = new ArrayList<>(placementsBlacklist.size());

        for (PlacementBlacklist placement : placementsBlacklist) {
            Long sizeId = placement.getSizeName() == null ? 0l : creativeSizeService.findByName(placement.getSizeName()).getId();
            PlacementBlacklistChannel channel = channelsBySizeId.get(sizeId);
            if (channel == null) {
                channel = findChannel(country, sizeId);
                if (channel == null) {
                    channel = new PlacementBlacklistChannel();
                    channel.setTriggersVersion(new Timestamp(System.currentTimeMillis()));
                    channel.setCountry(country);
                    channel.setSizeId(sizeId);
                    channel.setName(generateChannelName(country, sizeId));
                }
                channelsBySizeId.put(sizeId, channel);
            }

            placement.setChannel(channel);
            placement.setDateAdded(currentDate);

            placementsToAdd.add(placement);
        }

        Collection<PlacementBlacklistChannel> channels = channelsBySizeId.values();
        createPlacementBlacklistChannels(channels);
        addPlacementBlacklist(country, placementsToAdd);

        return new HashSet<>(channels);
    }

    private Set<PlacementBlacklistChannel> dropAllImpl(Country country, Collection<PlacementBlacklist> placementsBlacklist) {
        if (placementsBlacklist.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> placementIdsToRemove = EntityUtils.getEntityIds(placementsBlacklist);
        Set<PlacementBlacklistChannel> channels = new HashSet<>(findChannelsByPlacementIds(placementIdsToRemove));
        removePlacementBlacklist(country, placementIdsToRemove);

        return channels;
    }

    private void updateStatusAllImpl(Set<PlacementBlacklistChannel> channels) {
        for (PlacementBlacklistChannel channel : channels) {
            if (isChannelNotEmpty(channel)) {
                channel.setStatus(Status.ACTIVE);
            } else {
                channel.setStatus(Status.DELETED);
            }
            channel.setTriggersVersion(new Timestamp(System.currentTimeMillis()));
            channel = em.merge(channel);
            displayStatusService.update(channel);
        }
    }

    private boolean isChannelNotEmpty(PlacementBlacklistChannel channel) {
        return jdbcTemplate.queryForObject(
                "select exists(select 1 from placementblacklist pb where pb.channel_id = ?)", Boolean.class, channel.getId()
        );
    }

    private PlacementBlacklistChannel findChannel(Country country, Long sizeId) {
        Query query = em.createQuery("SELECT c FROM PlacementBlacklistChannel c WHERE c.country.countryCode = :countryCode AND c.sizeId = :sizeId")
                .setParameter("countryCode", country.getCountryCode())
                .setParameter("sizeId", sizeId);
        //noinspection unchecked
        List<PlacementBlacklistChannel> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() > 1) {
            throw new RuntimeException("Several entries with value (country=" + country.getCountryCode() +
                    ", sizeId=" + sizeId + ") were found in 'channel' table");
        }
        return result.get(0);
    }

    private List<PlacementBlacklistChannel> findChannelsByPlacementIds(final Collection<Long> placementIds) {
        List<Long> channelIds = jdbcTemplate.queryForList(
                "select distinct(pb.channel_id) from placementblacklist pb where pb.channel_trigger_id = any(?)",
                Long.class,
                jdbcTemplate.createArray("int8", placementIds)
        );

        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        Query query = em.createQuery("SELECT c FROM PlacementBlacklistChannel c WHERE c.id in :ids")
                .setParameter("ids", channelIds);
        //noinspection unchecked
        return query.getResultList();
    }

    private void createPlacementBlacklistChannels(Collection<PlacementBlacklistChannel> channels) {
        for (PlacementBlacklistChannel channel : channels) {
            if (channel.getId() == null) {
                prePersistChannelCreate(channel);
                em.persist(channel);
            }
        }
    }

    private void validateAllImpl(List<PlacementBlacklist> placements, Country country) {
        if (placements.isEmpty()) {
            return;
        }

        Map<String, Set<PlacementBlacklist>> placementsBySizeNames = new HashMap<>(placements.size());
        for (PlacementBlacklist placement : placements) {
            String sizeName = placement.getSizeName() != null ? placement.getSizeName().trim() : null;
            Set<PlacementBlacklist> placementsBySizeName = placementsBySizeNames.get(sizeName);
            if (placementsBySizeName == null) {
                placementsBySizeName = new HashSet<>();
                placementsBySizeNames.put(sizeName, placementsBySizeName);
            }

            if (!placementsBySizeName.add(placement)) {
                UploadUtils.getUploadContext(placement)
                            .addFatal("admin.placementsBlacklist.error.duplicatedEntry")
                            .withParameters(placement.getUrl());
            }
        }

        for (Map.Entry<String, Set<PlacementBlacklist>> entry : placementsBySizeNames.entrySet()) {
            String sizeName = entry.getKey();
            Set<PlacementBlacklist> placementsBySizeName = entry.getValue();
            Map<String, Long> placementIds = findPlacementIds(country, sizeName);

            for (PlacementBlacklist placement : placementsBySizeName) {
                UploadContext uploadContext = UploadUtils.getUploadContext(placement);

                Long existingId = placement.getUrl() == null ? null : placementIds.get(placement.getUrl().toLowerCase());
                if (existingId != null) {
                    // UPDATE = REMOVE
                    uploadContext.mergeStatus(UploadStatus.UPDATE);
                    placement.setId(existingId);
                } else {
                    uploadContext.mergeStatus(UploadStatus.NEW);
                }

                // validate fields
                ValidationContext context = validationService.validate(
                        ValidationStrategies.exclude(uploadContext.getWrongPaths()),
                        "PlacementsBlacklist.createOrDrop",
                        placement
                );

                Set<ConstraintViolation> violations = context.getConstraintViolations();
                UploadUtils.setErrors(placement, violations);
            }
        }
    }

    private PlacementsBlacklistValidationResultTO fillValidationResult(List<PlacementBlacklist> placements) {
        PlacementsBlacklistValidationResultTO validationResult = new PlacementsBlacklistValidationResultTO();
        for (PlacementBlacklist placement : placements) {
            addValidationResult(placement, validationResult, validationResult.getPlacementsBlacklist());
        }
        return validationResult;
    }

    private void addValidationResult(EntityBase entity, PlacementsBlacklistValidationResultTO validationResult, Stats stats) {
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

    private String saveResults(Long accountId, List<PlacementBlacklist> placements, Country country) {
        for(PlacementBlacklist placement: placements) {
            UploadContext uploadContext = UploadUtils.getUploadContext(placement);
            uploadContext.getErrors(); // forced flush
        }
        UUID uuid = UUID.randomUUID();
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        ObjectOutputStream oos = null;
        try {
            OutputStream os = fs.openFile(getFileName(uuid, "placementsBlacklist"));
            oos = new ObjectOutputStream(os);
            oos.writeObject(new PlacementsBlacklistResult(placements, country.getCountryCode()));
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

    private String generateChannelName(Country country, Long sizeId) {
        return "Country " + country.getCountryCode() + " + SizeId " + sizeId;
    }

    private void prePersistChannelCreate(PlacementBlacklistChannel channel) {
        channel.setVisibility(ChannelVisibility.PUB);
        ChannelFieldsPreparer.initializeStatuses(channel);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
    }

    private Map<String, Long> findPlacementIds(Country country, String sizeName) {
        String concreteSizeQuery = "select channel_trigger_id as id, lower(url) as urlLower from placementblacklist pb" +
                " inner join channel c on pb.channel_id = c.channel_id" +
                " inner join creativesize cs on cs.size_id = c.size_id" +
                " where c.country_code = ? and cs.name = ?";
        String allSizesQuery = "select channel_trigger_id as id, lower(url) as urlLower from placementblacklist pb" +
                " inner join channel c on pb.channel_id = c.channel_id" +
                " where c.country_code = ? and c.size_id = 0";

        SqlRowSet rs = sizeName == null ? jdbcTemplate.queryForRowSet(allSizesQuery, country.getCountryCode()) :
                jdbcTemplate.queryForRowSet(concreteSizeQuery, country.getCountryCode(), sizeName);
        Map<String, Long> result = new HashMap<>();
        while (rs.next()) {
            String urlLower = rs.getString("urlLower");
            Long prev = result.put(urlLower, rs.getLong("id"));
            if (prev != null) {
                throw new RuntimeException("Several entries with value (country=" + country.getCountryCode() +
                        ", url=" + urlLower + ", sizeName=" + sizeName + ") were found in 'placementblacklist' table");
            }
        }
        return result;
    }

    private void removePlacementBlacklist(Country country, Collection<Long> placementIdsToRemove) {
        PlacementsBlacklistWrapper placementsBlacklistWrapper = new PlacementsBlacklistWrapper(country.getCountryId());
        placementsBlacklistWrapper.setOldPlacements(findPlacements(placementIdsToRemove));

        jdbcTemplate.batchUpdate("delete from public.placementblacklist where channel_trigger_id = ?",
                placementIdsToRemove,
                DELETE_BATCH_SIZE,
                new ParameterizedPreparedStatementSetter<Long>() {
                    @Override
                    public void setValues(PreparedStatement ps, Long argument) throws SQLException {
                        ps.setLong(1, argument);
                    }
                });

        auditService.audit(placementsBlacklistWrapper, ActionType.UPDATE);
    }

    private void addPlacementBlacklist(Country country, List<PlacementBlacklist> placementsToAdd) {
        PlacementsBlacklistWrapper placementsBlacklistWrapper = new PlacementsBlacklistWrapper(country.getCountryId());
        placementsBlacklistWrapper.setPlacements(placementsToAdd);

        jdbcTemplate.batchUpdate("insert into public.placementblacklist (channel_id, url, reason, user_id) values (?, ?, ?, ?)",
                placementsToAdd,
                INSERT_BATCH_SIZE,
                new ParameterizedPreparedStatementSetter<PlacementBlacklist>() {
                    @Override
                    public void setValues(PreparedStatement ps, PlacementBlacklist argument) throws SQLException {
                        ps.setLong(1, argument.getChannel().getId());
                        ps.setString(2, argument.getUrl());
                        ps.setLong(3, BlacklistReason.setToBits(argument.getReason()));
                        ps.setLong(4, argument.getUser().getId());
                    }
                });

        auditService.audit(placementsBlacklistWrapper, ActionType.UPDATE);
    }

    private List<PlacementBlacklist> findPlacements(final Collection<Long> placementIds) {
        if (placementIds.isEmpty()) {
            return Collections.emptyList();
        }

        return jdbcTemplate.query(
                "select pb.channel_trigger_id as id, cs.name as sizeName, lower(pb.url) as urlLower, pb.reason, pb.timestamp_added, pb.user_id" +
                        "    from placementblacklist pb" +
                        "    inner join channel c on pb.channel_id = c.channel_id" +
                        "    left join creativesize cs on cs.size_id = c.size_id" +
                        "    where pb.channel_trigger_id = any(?)",
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setArray(1, ps.getConnection().createArrayOf("int8", placementIds.toArray()));
                    }
                },
                new RowMapper<PlacementBlacklist>() {
                    @Override
                    public PlacementBlacklist mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PlacementBlacklist pb = new PlacementBlacklist();
                        pb.setId(rs.getLong("id"));
                        pb.setSizeName(rs.getString("sizeName"));
                        if (rs.wasNull()) {
                            pb.setSizeName(StringUtil.getLocalizedString("admin.placementsBlacklist.allSizes"));
                        }
                        pb.setUrl(rs.getString("urlLower"));
                        pb.setReason(BlacklistReason.bitsToSet(rs.getLong("reason")));
                        pb.setDateAdded(rs.getDate("timestamp_added"));
                        pb.setUser(new User(rs.getLong("user_id")));
                        return pb;
                    }
                }
        );
    }
}
