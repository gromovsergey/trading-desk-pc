package com.foros.action.security.auditLog;

import com.foros.action.BaseActionSupport;
import com.foros.audit.serialize.serializer.DomHelper;
import com.foros.audit.serialize.serializer.TriggersAuditSerializerHelper;
import com.foros.framework.ReadOnly;
import com.foros.model.AuditLogRecord;
import com.foros.session.security.auditLog.SearchAuditService;

import javax.ejb.EJB;
import org.dom4j.Document;
import org.dom4j.Node;

public class ViewTriggersAuditLog extends BaseActionSupport {

    @EJB
    private SearchAuditService service;

    private Long recordId;
    private String xpath;

    private String addedTriggers;
    private String removedTriggers;

    @ReadOnly
    public String view() {
        AuditLogRecord record = service.view(recordId);

        Document doc = DomHelper.stringToDocument(record.getActionDescription());
        Node triggersNode = doc.selectSingleNode(xpath);

        addedTriggers = TriggersAuditSerializerHelper.fetchAddedTriggers(triggersNode);
        removedTriggers = TriggersAuditSerializerHelper.fetchRemovedTriggers(triggersNode);

        return SUCCESS;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getAddedTriggers() {
        return addedTriggers;
    }

    public String getRemovedTriggers() {
        return removedTriggers;
    }
}
