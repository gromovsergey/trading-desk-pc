package com.foros.session.security.auditLog;

import com.foros.model.AuditLogRecord;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.Identifiable;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.OracleJob;
import com.foros.model.Status;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.ResultType;
import com.foros.model.security.User;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.UtilityService;
import com.foros.session.fileman.FileManager;
import com.foros.session.reporting.ReportType;
import com.foros.session.security.AuditLogRecordTO;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.jpa.DetachedList;
import com.foros.util.jpa.JpaQueryWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.GregorianChronology;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


@Stateless(name = "SearchAuditService")
@Interceptors(RestrictionInterceptor.class)
public class SearchAuditServiceBean implements SearchAuditService {

    private static final int MAX_TOTAL = 1000;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private UtilityService utilityService;

    @Override
    @Restrict(restriction = "AuditLog.view", parameters = {"#objectType", "#actionType", "#objectId"})
    public DetachedList<AuditLogRecord> getHistory(ObjectType objectType, ActionType actionType, Long objectId, int firstRow, int maxAuditLogRecords) {

        List<Integer> forbiddenActionIds = new LinkedList<Integer>();

        String query = "select al from AuditLogRecord al " +
            " where al.objectTypeId = :objectTypeId " +
                (actionType != null ? " and al.actionType = :actionTypeId " : "") +
                (!forbiddenActionIds.isEmpty() ? " and al.actionType not in :forbiddenActionIds " : "") +
                (objectId != null ? " and al.objectId = :objectId " : "") +
            " order by al.logDate desc, al.id desc";

        return new JpaQueryWrapper<AuditLogRecord>(em, query)
                .setParameter("objectTypeId", objectType.getId())
                .oneIf(actionType != null).setParameter("actionTypeId", actionType != null ? actionType.getId() : null)
                .oneIf(!forbiddenActionIds.isEmpty()).setArrayParameter("forbiddenActionIds", forbiddenActionIds)
                .oneIf(objectId != null).setParameter("objectId", objectId)
                .getDetachedList(firstRow, maxAuditLogRecords);
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'audit'")
    public AuditLogRecord viewLogForReport(Long logId) {
        if (logId == null) {
            throw new EntityNotFoundException("Entity with null id not found");
        }

        try {
            return jdbcTemplate.queryForObject("select * from report.auditlogrecord(?::int)",
                    new Object[] { logId },
                    new RowMapper<AuditLogRecord>() {
                        @Override
                        public AuditLogRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                            AuditLogRecord logRecord = new AuditLogRecord();

                            logRecord.setId(rs.getLong("log_id"));
                            logRecord.setLogDate(rs.getTimestamp("log_date"));
                            logRecord.setUserId(rs.getLong("user_id"));
                            logRecord.setObjectId(SQLUtil.nullSafeGet(rs, "object_id", Long.class));
                            logRecord.setActionType(ActionType.valueOf(rs.getInt("action_type_id")));
                            logRecord.setIP(rs.getString("ip"));
                            logRecord.setActionDescription(rs.getString("action_descr"));
                            logRecord.setFinanceJob(OracleJob.findByOrdinal(SQLUtil.nullSafeGet(rs, "job_id", Integer.class)));
                            logRecord.setSuccess(rs.getBoolean("success"));
                            logRecord.setObjectAccountId(SQLUtil.nullSafeGet(rs, "object_account_id", Long.class));

                            return logRecord;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("LogRecord with id = " + logId + " not found");
        }
    }

    @Override
    @Restrict(restriction = "AuditLog.view", parameters = "find('AuditLogRecord', #logId)")
    public AuditLogRecord view(Long logId) {
        if (logId == null) {
            throw new EntityNotFoundException("Entity with null id not found");
        }

        AuditLogRecord entity = em.find(AuditLogRecord.class, logId);

        if (entity == null) {
            throw new EntityNotFoundException("Entity with id = " + logId + " not found");
        }
        return entity;
    }

    @Override
    public EntityBase findEntity(ObjectType objectType, Long objectId) {
        Class<?> objectClass = objectType.getObjectClass();
        if (!EntityBase.class.isAssignableFrom(objectClass)) {
            throw new EntityNotFoundException(objectClass.getSimpleName() + " with id=" + objectId + " not found");
        }
        if (!EntityBase.class.isAssignableFrom(objectClass)) {
            throw new EntityNotFoundException(objectClass.getSimpleName() + " with id=" + objectId + " not found");
        }
        //noinspection unchecked
        return utilityService.find((Class<? extends EntityBase>) objectClass, objectId);
    }

    @Override
    public String getObjectName(ObjectType objectType, Long objectId) {
        if (ObjectType.PredefinedReport == objectType) {
            return StringUtil.getLocalizedString("reports." + ReportType.byId(objectId) + "Report");
        }

        if (ObjectType.FileManager == objectType) {
            return StringUtil.getLocalizedString(FileManager.Folder.valueOf(objectId).getKey());
        }

        if (ObjectType.BirtReport == objectType) {
            return null;
        }

        if (ObjectType.PlacementsBlacklist == objectType) {
            Object entity = findEntity(ObjectType.Country, objectId);
            return StringUtil.getLocalizedString("admin.placementsBlacklist") + " (" +
                    StringUtil.getLocalizedString("global.country." + ((Country) entity).getCountryCode() + ".name") + ")";
        }
        
        Object entity = findEntity(objectType, objectId);

        if (!PersistenceUtils.isInitialized(entity)) {
            PersistenceUtils.EntityInfo entityInfo = PersistenceUtils.getClassInfo(entity);
            return entityInfo.getType().getSimpleName() + ": id = " + entityInfo.getId();
        }

        if (entity instanceof IdNameEntity) {
            return ((IdNameEntity) entity).getName();
        }

        if (entity instanceof User) {
            return ((User) entity).getEmail();
        }

        if (entity instanceof Country) {
            return StringUtil.getLocalizedString("global.country." + ((Country) entity).getCountryCode() + ".name");
        }

        if (entity instanceof CTRAlgorithmData) {
            return entity.getClass().getSimpleName() + "[" + StringUtil.getLocalizedString("global.country." + ((CTRAlgorithmData) entity).getCountryCode() + ".name") + "]";
        }

        if (entity instanceof LocalizableNameEntity) {
            return ((LocalizableNameEntity) entity).getName().getDefaultName();
        }

        if (entity instanceof Identifiable) {
            return entity.getClass().getSimpleName() + ": id = " + ((Identifiable) entity).getId();
        }

        return entity.getClass().getSimpleName() + " [unnamed entity]";
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'audit'")
    public DetachedList<AuditLogRecordTO> searchLogs(final AuditReportParameters parameters) {
        DateTimeZone timeZone = DateTimeZone.forTimeZone(CurrentUserSettingsHolder.getTimeZone());

        LocalDateTime dateFrom = adjustDateTime(timeZone, parameters.getDateFrom());
        LocalDateTime dateTo = adjustDateTime(timeZone, parameters.getDateTo());
        long firstResult = getFirstResult(parameters.getPage());

        return jdbcTemplate.query(
                "select * from report.audit_report(" +
                        "?::timestamp," + /* p_from_date */
                        "?::timestamp," + /* p_to_date */
                        "?::int[]," +     /* p_account_role_ids */
                        "?::varchar," +   /* p_account_name */
                        "?::varchar," +   /* p_email */
                        "?::int[]," +     /* p_object_type_ids */
                        "?::int," +       /* p_action_type_id */
                        "?::boolean," +   /* p_success */
                        "?::boolean," +   /* p_jobs_only */
                        "?::bigint," +    /* p_first */
                        "?::bigint," +    /* p_max */
                        "?::bigint" +     /* p_rows_in_resultset */
                        ")",
                new Object[]{
                        dateFrom,
                        dateTo,
                        jdbcTemplate.createArray("int", parameters.getAccountRoleIds()),
                        parameters.getAccountName(),
                        parameters.getEmail(),
                        jdbcTemplate.createArray("int", parameters.getObjectTypeIds()),
                        parameters.getActionType() != null ? parameters.getActionType().getId() : null,
                        parameters.getResultType() != null ? (parameters.getResultType() == ResultType.SUCCESS) : null,
                        parameters.isOracleJobsOnly(),
                        firstResult,
                        SEARCH_PAGE_SIZE,
                        MAX_TOTAL + 1
                },
                new AuditReportCallbackHandler(timeZone)
        );
    }

    private LocalDateTime adjustDateTime(DateTimeZone timeZone, LocalDateTime dateTime) {
        return dateTime.toDateTime(timeZone).withZone(DateTimeZone.forID("GMT")).toLocalDateTime();
    }

    private long getFirstResult(Long page) {
        if (page == null) {
            return 0;
        }

        return (page - 1) * SEARCH_PAGE_SIZE;
    }

    private static class AuditReportCallbackHandler implements ResultSetExtractor<DetachedList<AuditLogRecordTO>> {
        private final GregorianChronology chronology;

        public AuditReportCallbackHandler(DateTimeZone timeZone) {
            chronology = GregorianChronology.getInstance(timeZone);
        }

        @Override
        public DetachedList<AuditLogRecordTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<AuditLogRecordTO> auditRecords = new ArrayList<>(SEARCH_PAGE_SIZE);
            int total = 0;
            while (rs.next()) {
                if (total == 0) {
                    total = rs.getInt("cnt");
                }

                AuditLogRecordTO to = new AuditLogRecordTO();

                to.setId(rs.getLong("log_id"));
                to.setAccountId(SQLUtil.nullSafeGet(rs, "account_id", Long.class));
                if (to.getAccountId() != null) {
                    to.setAccountStatus(Status.valueOf(rs.getString("account_status").charAt(0)));
                    to.setAccountName(rs.getString("account_name"));
                }
                to.setLogDate(new LocalDateTime(rs.getTimestamp("log_date"), chronology));
                to.setUserId(SQLUtil.nullSafeGet(rs, "user_id", Long.class));
                if (to.getUserId() != null) {
                    to.setUserStatus(Status.valueOf(rs.getString("user_status").charAt(0)));
                    to.setUserLogin(rs.getString("user_email"));
                }
                to.setObjectType(ObjectType.valueOf(SQLUtil.nullSafeGet(rs, "object_type_id", Integer.class)));
                to.setObjectId(SQLUtil.nullSafeGet(rs, "object_id", Long.class));
                to.setActionType(ActionType.valueOf(rs.getInt("action_type_id")));
                to.setFinanceJob(OracleJob.findByOrdinal(SQLUtil.nullSafeGet(rs, "job_id", Integer.class)));
                to.setSuccess(rs.getBoolean("success"));
                to.setObjectAccountId(SQLUtil.nullSafeGet(rs, "object_account_id", Long.class));
                to.setObjectAccountName(rs.getString("object_account_name"));

                auditRecords.add(to);
            }

            boolean totalHasMore;
            if (total > MAX_TOTAL) {
                total = MAX_TOTAL;
                totalHasMore = true;
            } else {
                totalHasMore = false;
            }

            return new DetachedList<AuditLogRecordTO>(auditRecords, total, totalHasMore);
        }

    }
}
