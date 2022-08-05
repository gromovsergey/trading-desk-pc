package com.foros.session.birt;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.Country;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportInstanceState;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.report.birt.BirtReportSessionState;
import com.foros.model.report.birt.BirtReportType;
import com.foros.model.security.ActionType;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.AccessRestrictedException;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.NamedTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.admin.FileManagerRestrictions;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.ContentSourceSupport;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.restrictions.FileManagerRestrictionUtils;
import com.foros.session.fileman.restrictions.FileNameRestrictionImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.RandomUtil;
import com.foros.util.bean.Filter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "BirtReportService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class})
public class BirtReportServiceBean implements BirtReportService {

    @EJB
    PathProviderService pathProviderService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private AuditService auditService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Resource
    private SessionContext sessionContext;

    @EJB
    private UserRoleService userRoleService;

    @EJB
    private UserService userService;

    @EJB
    private FileManagerRestrictions fileManagerRestrictions;

    @EJB
    private ConfigService config;

    @EJB
    private BirtReportRestrictions birtReportRestrictions;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    @Validate(validation = "BirtReport.create", parameters = {"#report", "#userRoles", "#is", "#fileSize"})
    @Restrict(restriction = "BirtReport.create")
    @Interceptors({CaptureChangesInterceptor.class})
    public Long create(BirtReport report, List<UserRole> userRoles, InputStream is, long fileSize) throws IOException, FileManagerException {
        em.persist(report);

        auditService.audit(report, ActionType.CREATE);
        em.flush();

        if (userRoles != null) {
            for (UserRole userRole : userRoles) {
                for (PolicyEntry policy : userRole.getPolicyEntries()) {
                    if (policy.getId() == null) {
                        policy.setParameter(report.getId().toString());
                    }
                }
            }
            userRoleService.mergePolicies(userRoles, "birt_report", report.getId().toString());
        }

        try {
            storeReportFile(is, report);
        } catch (FileManagerException fle) {
            throw fle;
        } catch (Exception e) {
            sessionContext.setRollbackOnly();
            throw new FileManagerException(e);
        }

        return report.getId();
    }

    @Override
    public void updateInvoiceReport(String countryCode, InputStream stream) throws FileManagerException {
        Country country = em.find(Country.class, countryCode);

        BirtReport report = country.getInvoiceReport();
        if (report == null) {
            report = createInvoiceReport(country);
            country.setInvoiceReport(report);
        }

        storeReportFile(stream, report);
    }

    private BirtReport createInvoiceReport(Country country) {
        BirtReport report = new BirtReport();
        report.setName("Invoice report for country: " + country.getCountryCode());
        report.setType(BirtReportType.INVOICE);

        em.persist(report);

        return report;
    }

    @Override
    @Restrict(restriction = "BirtReport.update", parameters = "#id")
    @Interceptors({CaptureChangesInterceptor.class})
    public void delete(Long id) {
        BirtReport report = find(id);
        em.remove(report);
        auditService.audit(report, ActionType.DELETE);
        userRoleService.removePolicy("birt_report", id.toString());
        try {
            deleteReportFile(report);
        } catch (Exception e) {
            //ignore if exception occurs while deletion of stored report file
        }
    }

    private BirtReport find(Long id) {
        BirtReport res = em.find(BirtReport.class, id);
        if (res == null) {
            throw new EntityNotFoundException("Custom Report with id=" + id + " not found");
        }

        return res;
    }

    @Override
    @Restrict(restriction = "BirtReport.get", parameters = "#id")
    public BirtReport get(Long id) {
        BirtReport report = find(id);
        if (!isTemplateExists(report)) {
            throw ConstraintViolationException.newBuilder("birtReports.error.fileNotFound").build();
        }
        return report;
    }

    private boolean isTemplateExists(BirtReport report) {
        return getReportsFS().checkExist(report.getTemplateFile());
    }

    @Override
    @Restrict(restriction = "BirtReport.update", parameters = "#id")
    public BirtReport findForUpdate(Long id) {
        return find(id);
    }

    @Override
    public boolean checkTemplateExists(BirtReport report) {
        return isTemplateExists(report);
    }

    @Override
    public String getFullTemplatePath(BirtReport report) {
        FileNameRestrictionImpl.INSTANCE.checkFileName(report.getTemplateFile());
        return pathProviderService.getReports().getPath(report.getTemplateFile()).getPath();
    }

    @Override
    public String getReportDocumentAbsoluteFileName(BirtReportInstance instance) {
        return getReportDocumentAbsoluteFile(instance).getAbsolutePath();
    }

    private File getReportDocumentAbsoluteFile(BirtReportInstance instance) {
        FileNameRestrictionImpl.INSTANCE.checkFileName(instance.getDocumentFileName());
        return pathProviderService.getReportDocuments().getPath(instance.getDocumentFileName());
    }

    @Override
    public String getReportAbsoluteFileName(BirtReport report) {
        return getReportAbsoluteFile(report).getAbsolutePath();
    }

    private File getReportAbsoluteFile(BirtReport report) {
        FileNameRestrictionImpl.INSTANCE.checkFileName(report.getTemplateFile());
        return pathProviderService.getReports().getPath(report.getTemplateFile());
    }

    @Override
    @Restrict(restriction = "BirtReport.update", parameters = "#report.id")
    public ContentSource readTemplate(BirtReport report) {
        return ContentSourceSupport.create(getReportsFS(), report.getTemplateFile());
    }

    @Override
    public List<BirtReportTO> getIndex() {
        //noinspection unchecked
        List<BirtReportTO> result = new ArrayList<BirtReportTO>(em.createNamedQuery("BirtReport.getIndex").getResultList());
        CollectionUtils.filter(result, new Filter<BirtReportTO>() {
            @Override
            public boolean accept(BirtReportTO to) {
                to.setUpdatable(restrictionService.isPermitted("BirtReport.update", to.getId()));
                to.setViewable(restrictionService.isPermitted("BirtReport.get", to.getId()));
                return to.isUpdatable() || to.isViewable();
            }
        });

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<NamedTO> getAllReportNames() {
        return em.createQuery("select new com.foros.session.NamedTO(cr.id, cr.name) from BirtReport cr").getResultList();
    }

    @Override
    public void refresh(Long id) {
        em.refresh(find(id));
    }

    @Override
    @Validate(validation = "BirtReport.update", parameters = {"#report", "#updatedUserRoles", "#removedUserRoles", "#updateFile", "#is", "#fileSize"})
    @Restrict(restriction = "BirtReport.update", parameters = "#report.id")
    @Interceptors({CaptureChangesInterceptor.class})
    public void update(BirtReport report, List<UserRole> updatedUserRoles, List<UserRole> removedUserRoles, boolean updateFile, InputStream is, long fileSize) throws IOException, FileManagerException {
        report.unregisterChange("id");

        BirtReport existingReport = find(report.getId());
        EntityUtils.copy(existingReport, report);

        List<UserRole> userRoles = new ArrayList<UserRole>(updatedUserRoles);
        userRoles.addAll(removedUserRoles);

        userRoleService.mergePolicies(userRoles, "birt_report", report.getId().toString());

        if (updateFile) {
            existingReport.setVersion(new Timestamp(System.currentTimeMillis()));
        }
        auditService.audit(existingReport, ActionType.UPDATE);

        em.flush();

        if (updateFile) {
            try {
                PersistenceUtils.performHibernateLock(em, existingReport);
                storeReportFile(is, existingReport);
            } catch (FileManagerException fle) {
                throw fle;
            } catch (Exception e) {
                sessionContext.setRollbackOnly();
                throw new FileManagerException(e);
            }
        }
    }

    private void deleteReportFile(BirtReport report) throws IOException {
        FileSystem fs = getReportsFS();
        fs.delete(report.getTemplateFile());
    }

    private FileSystem getReportsFS() {
        return pathProviderService.createFileSystem(pathProviderService.getReports());
    }

    private void storeReportFile(InputStream is, BirtReport report) throws FileManagerException {
        FileSystem fs = getReportsFS();
        //add file system restriction class(es)
        fs.setFileRestriction(FileManagerRestrictionUtils.getUploadMaxFilesInDir(config));
        fs.setFileManagerRestrictions(fileManagerRestrictions);

        try {
            fs.writeFile("", report.getTemplateFile(), is);
        } catch (IOException e) {
            throw new FileManagerException(e);
        }
    }

    @Override
    public BirtReportInstance findCachedInstance(Long reportId, String parametersHash) {
        String query = "select cri from BirtReportInstance cri where" +
                " cri.report.id = :reportId" +
                " and cri.parametersHash = :hash" +
                " and cri.created > current_timestamp - to_interval_sec(cri.report.instanceCacheTime)" +
                " and cri.state <> com.foros.model.report.birt.BirtReportInstanceState.DELETED";

        List<BirtReportInstance> result = em
                .createQuery(query, BirtReportInstance.class)
                .setParameter("reportId", reportId)
                .setParameter("hash", parametersHash)
                .getResultList();

        return fetchInstanceFromList(result);
    }

    private BirtReportInstance fetchInstanceFromList(List<BirtReportInstance> result) {
        for (BirtReportInstance instance : result) {
            if (!isReportDocumentExists(instance)) {
                instance.setState(BirtReportInstanceState.DELETED);
            } else {
                return instance;
            }
        }

        return null;
    }

    private boolean isReportDocumentExists(BirtReportInstance instance) {
        return getReportDocumentAbsoluteFile(instance).exists();
    }

    @Override
    public void createInstance(BirtReportInstance instance) {
        instance.setReport(em.find(BirtReport.class, instance.getReport().getId()));
        instance.setState(BirtReportInstanceState.NORMAL);
        em.persist(instance);
    }

    @Override
    public void deleteInstance(Long id) {
        em.remove(em.find(BirtReportInstance.class, id));
    }

    @Override
    public List<BirtReportInstance> findInstances(Long reportId) {
        //noinspection unchecked
        return em
                .createNamedQuery("BirtReportInstance.findByReportId")
                .setParameter("reportId", reportId)
                .getResultList();
    }

    private List<BirtReportInstance> findInstances(List<Long> ids) {
        ArrayList<BirtReportInstance> result = new ArrayList<BirtReportInstance>();

        for (Long id : ids) {
            result.add(em.find(BirtReportInstance.class, id));
        }

        return result;
    }

    private List<BirtReportSession> findSessions(List<Long> sessionsIds) {
        ArrayList<BirtReportSession> result = new ArrayList<BirtReportSession>();

        for (Long id : sessionsIds) {
            result.add(em.find(BirtReportSession.class, id));
        }

        return result;
    }


    @Override
    public void clearInstance(BirtReportInstance instance) {
        File file = pathProviderService.getReportDocuments().getPath(instance.getDocumentFileName());

        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void clearSession(BirtReportSession session) {
        String path = getReportImagesFolder(session);
        File file = new File(path);

        if (file.exists()) {
            FileUtils.deleteFile(file);
        }
    }

    @Override
    @Restrict(restriction = "BirtReport.get", parameters = "#report.id")
    public BirtReportSession createSession(BirtReport report) {
        BirtReportSession session = new BirtReportSession();
        session.setUser(userService.getMyUser());
        session.setReport(em.find(BirtReport.class, report.getId()));
        session.setSessionId(generateSessionId());
        session.setState(BirtReportSessionState.CREATED);

        em.persist(session);

        return session;
    }

    @Override
    @Restrict(restriction = "BirtReport.viewSession", parameters = "#id")
    public BirtReportSession findSession(Long id) {
        return em.find(BirtReportSession.class, id);
    }

    private String generateSessionId() {
        return RandomUtil.getRandomString(80, RandomUtil.Alphabet.LETTERS, RandomUtil.Alphabet.NUMBERS);
    }

    @Override
    public BirtReportSession findSession(String sessionId) {
        //noinspection unchecked
        List<BirtReportSession> result = em
                .createNamedQuery("BirtReportSession.findBySessionId")
                .setParameter("sessionId", sessionId)
                .getResultList();

        return fetchSessionFromList(result);
    }

    @Override
    public BirtReportSession findStartedSessionByReportIdAndParametersHash(Long reportId, String parametersHash) {
        //noinspection unchecked
        List<BirtReportSession> result = em
                .createNamedQuery("BirtReportSession.findStartedByReportIdAndParametersHash")
                .setParameter("reportId", reportId)
                .setParameter("parametersHash", parametersHash)
                .getResultList();

        return fetchSessionFromList(result);
    }

    private BirtReportSession fetchSessionFromList(List<BirtReportSession> sessions) {
        if (sessions.isEmpty()) {
            return null;
        }

        BirtReportSession session = sessions.get(0);

        if (!isTemplateExists(session.getReport())) {
            throw new EntityNotFoundException("Your session was broken, please reload page.");
        }

        BirtReportInstance instance = session.getBirtReportInstance();
        if (instance != null && !isReportDocumentExists(instance)) {
            throw new EntityNotFoundException("Your session was broken, please reload page.");
        }

        if (!birtReportRestrictions.canViewSession(session)) {
            throw new AccessRestrictedException("Access forbidden by BirtReport.viewSession restriction", Collections.<ConstraintViolation>emptySet());
        }

        return session;
    }

    @Override
    public void setSessionStarted(Long sessionId, String parametersHash) {
        BirtReportSession session = em.find(BirtReportSession.class, sessionId);
        session.setBirtReportInstance(null);
        session.setParametersHash(parametersHash);
        session.setState(BirtReportSessionState.STARTED);
    }

    @Override
    public void setSessionAborted(Long sessionId) {
        BirtReportSession session = em.find(BirtReportSession.class, sessionId);
        session.setState(BirtReportSessionState.ABORTED);
    }

    @Override
    public void setSessionDone(Long sessionId, Long birtReportInstanceId) {
        BirtReportSession session = em.find(BirtReportSession.class, sessionId);
        session.setBirtReportInstance(em.find(BirtReportInstance.class, birtReportInstanceId));
        session.setState(BirtReportSessionState.DONE);
    }

    @Override
    public void setSessionExpired(Long sessionId) {
        BirtReportSession session = em.find(BirtReportSession.class, sessionId);
        session.setState(BirtReportSessionState.EXPIRED);
    }

    @Override
    public void setSessionInstance(Long sessionId, Long reportInstanceId) {
        BirtReportSession session = em.find(BirtReportSession.class, sessionId);
        session.setBirtReportInstance(em.find(BirtReportInstance.class, reportInstanceId));
    }

    private static class SessionsAndInstancesIds {
        private List<Long> instances;
        private List<Long> sessions;

        private SessionsAndInstancesIds(List<Long> instances, List<Long> sessions) {
            this.instances = instances;
            this.sessions = sessions;
        }

        public List<Long> getInstances() {
            return instances;
        }

        public List<Long> getSessions() {
            return sessions;
        }
    }

    @Override
    public InstancesAndSessions expireInstancesAndSessions() {
        final Long expire = config.get(ConfigParameters.REPORT_SESSION_EXPIRE);
        SessionsAndInstancesIds instancesAndInstancesIds = jdbcTemplate.queryForObject(
                "select p_out_instances_ids::bigint[], p_out_sessions_ids::bigint[] from birt.expire_instances_and_sessions(?::int)",
                new Object[]{expire},
                new RowMapper<SessionsAndInstancesIds>() {
                    @Override
                    public SessionsAndInstancesIds mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Array instancesArray = rs.getArray(1);
                        Array sessionsArray = rs.getArray(2);
                        return new SessionsAndInstancesIds(toList(instancesArray), toList(sessionsArray));
                    }

                    private List<Long> toList(Array array) throws SQLException {
                        return array == null ? Collections.<Long>emptyList() : Arrays.asList((Long[])array.getArray());
                    }
                }
        );

        List<BirtReportInstance> instances = findInstances(instancesAndInstancesIds.getInstances());
        List<BirtReportSession> sessions = findSessions(instancesAndInstancesIds.getSessions());

        return new InstancesAndSessions(instances, sessions);
    }

    @Override
    public String getReportImagesFolder(BirtReportSession reportSession) {
        File images = pathProviderService.getReportDocuments().getPath("images/"+reportSession.getSessionId()+"/");

        if (!images.exists()) {
            images.mkdirs();
        }

        return images.getAbsolutePath();
    }

}
