package com.foros.action.reporting.audit;

import javax.ejb.EJB;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.AuditLogRecord;
import com.foros.session.security.auditLog.SearchAuditService;

public class ViewAuditLogRecordAction extends BaseActionSupport {
    @EJB
    private SearchAuditService service;

    private AuditLogRecord logRecord;

    private Long id;

    @SuppressWarnings("UnusedDeclaration")
    public AuditLogRecord getLogRecord() {
        return logRecord;
    }

    @ReadOnly
    public String view() throws Exception {
        logRecord = service.viewLogForReport(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
