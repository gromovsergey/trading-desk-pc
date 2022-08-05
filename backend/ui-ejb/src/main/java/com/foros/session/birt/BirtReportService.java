package com.foros.session.birt;

import com.foros.model.Country;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.security.UserRole;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.NamedTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface BirtReportService {
    Long create(BirtReport report, List<UserRole> userRoles, InputStream is, long fileSize) throws IOException, FileManagerException;

    void updateInvoiceReport(String countryCode, InputStream stream) throws FileManagerException;

    void update(BirtReport report, List<UserRole> updatedUserRoles, List<UserRole> removedUserRoles, boolean updateFile, InputStream is, long fileSize) throws IOException, FileManagerException;

    void delete(Long id);

    void refresh(Long id);

    BirtReport get(Long id);

    BirtReport findForUpdate(Long id);

    List<BirtReportTO> getIndex();

    List<NamedTO> getAllReportNames();

    boolean checkTemplateExists(BirtReport report);

    ContentSource readTemplate(BirtReport report);

    String getFullTemplatePath(BirtReport report);

    String getReportDocumentAbsoluteFileName(BirtReportInstance instance);

    String getReportAbsoluteFileName(BirtReport report);

    BirtReportInstance findCachedInstance(Long reportId, String parametersHash);

    void createInstance(BirtReportInstance instance);

    void deleteInstance(Long id);

    List<BirtReportInstance> findInstances(Long reportId);

    BirtReportSession createSession(BirtReport report);

    BirtReportSession findSession(Long id);

    BirtReportSession findSession(String sessionId);

    BirtReportSession findStartedSessionByReportIdAndParametersHash(Long reportId, String parametersHash);

    void setSessionStarted(Long sessionId, String parametersHash);

    void setSessionAborted(Long sessionId);

    void setSessionDone(Long sessionId, Long birtReportInstanceId);

    void setSessionExpired(Long sessionId);

    void setSessionInstance(Long sessionId, Long reportInstanceId);

    InstancesAndSessions expireInstancesAndSessions();

    void clearInstance(BirtReportInstance instance);

    void clearSession(BirtReportSession session);

    String getReportImagesFolder(BirtReportSession reportSession);

    static class InstancesAndSessions {
        private List<BirtReportInstance> instances;
        private List<BirtReportSession> sessions;

        public InstancesAndSessions(List<BirtReportInstance> instances, List<BirtReportSession> sessions) {
            this.instances = instances;
            this.sessions = sessions;
        }

        public List<BirtReportInstance> getInstances() {
            return instances;
        }

        public List<BirtReportSession> getSessions() {
            return sessions;
        }
    }
}
