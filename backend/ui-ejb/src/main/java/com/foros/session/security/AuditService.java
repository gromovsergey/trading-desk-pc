package com.foros.session.security;

import com.foros.model.AuditLogRecord;
import com.foros.model.Identifiable;
import com.foros.model.account.Account;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.security.ActionType;
import com.foros.model.security.ResultType;

import java.util.Set;

import javax.ejb.Local;

/**
 * This is the business interface for AuditService enterprise bean.
 */
@Local
public interface AuditService {

    /**
     * Log in audit login action
     *
     * @param login user's login
     * @param status state of login action
     * @param remoteAddr
     */
    void logLogin(String login, ResultType status, String remoteAddr, Long userId);

    void logMessage(Identifiable entity, ActionType actionType, ResultType resultType, String message);

    Long logReportStarted(ReportRunTO reportRunTO);

    void logReportFinished(Long auditLogId, ReportRunTO reportRunTO);

    void logReport(ReportRunTO reportRunTO);

    void logDeleteTerm(Account account, String term);

    void logAddTerms(Account account, String... term);

    /**
     * Register entity for audit in changes capture mechanism
     *
     * @param entity entity for audit
     * @param actionType action type
     */
    void audit(Object entity, ActionType actionType);

    void auditDetached(Identifiable entity, ActionType actionType);

    AuditLogRecord find(Long logId);

    void logFileSystem(Long auditObjectId, Set<String> added, Set<String> updated, Set<String> removed);

    void logAudienceChannelUIDsUpdated(AudienceChannel channel, long uidsCount);
}
