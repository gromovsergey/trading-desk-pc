package com.foros.session.security.auditLog;

import com.foros.model.AuditLogRecord;
import com.foros.model.EntityBase;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.session.security.AuditLogRecordTO;
import com.foros.util.jpa.DetachedList;

import javax.ejb.Local;

@Local
public interface SearchAuditService {

    int SEARCH_PAGE_SIZE = 100;

    AuditLogRecord viewLogForReport(Long logId);

    AuditLogRecord view(Long logId);

    DetachedList<AuditLogRecord> getHistory(ObjectType objectType, ActionType actionType, Long objectId, int firstRow, int maxAuditLogRecords);

    DetachedList<AuditLogRecordTO> searchLogs(AuditReportParameters parameters);

    EntityBase findEntity(ObjectType objectType, Long objectId);

    String getObjectName(ObjectType objectType, Long objectId);
}
