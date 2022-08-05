package com.foros.session.security;

import com.foros.audit.serialize.AuditChange;
import com.foros.audit.serialize.serializer.DomHelper;
import com.foros.changes.ChangesService;
import com.foros.changes.inspection.ChangeType;
import com.foros.model.AuditLogRecord;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.account.Account;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.ResultType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.util.CollectionUtils;
import com.foros.util.NameValuePair;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


@Stateless(name = "AuditService")
@Interceptors({RestrictionInterceptor.class})
public class AuditServiceBean implements AuditService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ChangesService changesService;

    public AuditServiceBean() {
    }

    /**
     * Create and save new audit log record
     *
     * @param objectType        object type
     * @param objectId          object id
     * @param userId            user id
     * @param actionType        action type
     * @param actionDescriprion details of
     * @return created audit log record
     */
    private AuditLogRecord log(ObjectType objectType, Long objectId, Long userId, ActionType actionType, String actionDescriprion, String remoteAddr, ResultType result) {
        AuditLogRecord logRecord = new AuditLogRecord();
        logRecord.setObjectType(objectType);
        logRecord.setObjectId(objectId);
        logRecord.setActionType(actionType);
        logRecord.setActionDescription(actionDescriprion);
        logRecord.setSuccess(result == ResultType.SUCCESS);

        logRecord.setIP(remoteAddr != null ? remoteAddr : CurrentUserSettingsHolder.getIpOrDefault());
        if (userId != null) {
            logRecord.setUserId(userId);
        }
        em.persist(logRecord);
        return logRecord;
    }

    @Override
    public void logMessage(Identifiable entity, ActionType actionType, ResultType resultType, String message) {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", actionType.getName());

        Element entityElement = root.addElement("entity");
        entityElement.addAttribute("class", entity.getClass().getName());
        entityElement.addAttribute("id", ObjectUtils.toString(entity.getId()));

        Element property = entityElement.addElement("property");
        property.addAttribute("name", "message");
        property.setText(message);

        log(ObjectType.valueOf(entity.getClass()), entity.getId(),
                SecurityContext.getPrincipal() == null ? null : SecurityContext.getPrincipal().getUserId(),
                actionType, DomHelper.documentToString(document), null, resultType);

    }

    @Override
    public void logLogin(String login, ResultType status, String remoteAddr, Long userId) {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", "login");

        Element loginElement = root.addElement("login");

        loginElement.addElement("user").setText(login);
        loginElement.addElement("status").setText(status.name());

        log(null, null, userId, ActionType.LOGIN, DomHelper.documentToString(document), remoteAddr, status);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long logReportStarted(ReportRunTO reportRunTO) {
        return insertLogRecord(reportRunTO, ActionType.START_REPORT);
    }

    private Long insertLogRecord(ReportRunTO reportRunTO, ActionType actionType) {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        ResultType result = reportRunTO.getResultType();
        String description = buildRunReportDescription(reportRunTO, actionType);

        AuditLogRecord record = log(reportRunTO.getObjectType(), reportRunTO.getId(), principal.getUserId(), actionType,
                description, principal.getRemoteUserIP(), result);

        return record.getId();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logReportFinished(Long auditLogId, ReportRunTO reportRunTO) {
        String description = buildRunReportDescription(reportRunTO, ActionType.COMPLETE_REPORT);

        em.createQuery("update AuditLogRecord set " +
                " success = :success, actionType = :actionType, actionDescription = :description " +
                " where id=:id")
                .setParameter("success", reportRunTO.getResultType() == ResultType.SUCCESS)
                .setParameter("actionType", ActionType.COMPLETE_REPORT.getId())
                .setParameter("description", description)
                .setParameter("id", auditLogId)
                .executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logReport(ReportRunTO reportRunTO) {
        insertLogRecord(reportRunTO, ActionType.COMPLETE_REPORT);
    }

    private String buildRunReportDescription(ReportRunTO reportRunTO, ActionType actionType) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", actionType.getName());
        Element reportElement = root.addElement("report");

        addProperty(reportElement, "name", reportRunTO.getName());
        addProperty(reportElement, "report_id", reportRunTO.getId());

        if (!CollectionUtils.isNullOrEmpty(reportRunTO.getParams())) {
            Element paramsElement = reportElement.addElement("report-property").addAttribute("name", "parameters");
            for (NameValuePair<String, Object> pair : reportRunTO.getParams()) {
                if (pair.getValue() != null) {
                    paramsElement.addElement("parameter").addAttribute("name", pair.getName()).setText(pair.getValue().toString());
                }
            }
        }

        if (!CollectionUtils.isNullOrEmpty(reportRunTO.getColumns())) {
            Element columnsElement = reportElement.addElement("report-property").addAttribute("name", "columns");
            for (String column : reportRunTO.getColumns()) {
                columnsElement.addElement("column").setText(column);
            }
        }

        addProperty(reportElement, "output type", reportRunTO.getOutputType());
        addProperty(reportElement, "output rows", reportRunTO.getRowsCount(), " row(s)");
        addProperty(reportElement, "output size", reportRunTO.getSize(), " byte(s)");
        addProperty(reportElement, "output time", reportRunTO.getExecutionTime(), " ms");
        addProperty(reportElement, "error message", reportRunTO.getErrorMessage());


        return DomHelper.documentToString(document);
    }

    private void addProperty(Element element, String name, Object value) {
        addProperty(element, name, value, null);
    }

    private void addProperty(Element element, String name, Object value, String postfix) {
        if (value != null) {
            String text = value.toString();
            if (postfix != null) {
                text += postfix;
            }
            element.addElement("report-property").addAttribute("name", name).setText(text);
        }
    }

    @Override
    public void audit(Object entity, ActionType actionType) {
        entity = findRoot(entity);
        changesService.addChange(new AuditChange(entity, actionType));
    }

    @Override
    public void auditDetached(Identifiable entity, ActionType actionType) {
        EntityBase existingEntity = (EntityBase) em.find(entity.getClass(), entity.getId());
        audit(existingEntity, actionType);
    }

    private Object findRoot(Object entity) {
        if (entity instanceof CampaignCreative) {
            entity = ((CampaignCreative) entity).getCreativeGroup();
        }
        return entity;
    }

    @Override
    public AuditLogRecord find(Long logId) {
        AuditLogRecord logRecord = em.find(AuditLogRecord.class, logId);
        if (logRecord == null) {
            throw new EntityNotFoundException(AuditLogRecord.class.getSimpleName() + " with id=" + logId + " not found");
        }
        return logRecord;
    }

    @Override
    public void logDeleteTerm(Account account, String term) {
        logTerms(account, "REMOVE", term);
    }

    @Override
    public void logAddTerms(Account account, String... terms) {
        logTerms(account, "ADD", terms);
    }

    private void logTerms(Account account, String changeType, String... terms) {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", ActionType.UPDATE.getName());

        Element entityElement = root.addElement("entity");
        entityElement.addAttribute("class", account.getClass().getName());
        entityElement.addAttribute("id", account.getId().toString());
        entityElement.addAttribute("name", account.getName());
        Element property = entityElement.addElement("property");
        property.addAttribute("name", "terms");
        Element collection = property.addElement("collection");
        for (String term: terms) {
            Element item = collection.addElement("item");
            item.addAttribute("changeType", changeType);
            item.setText(term);
        }

        ObjectType objectType = ObjectType.valueOf(account.getClass());
        Long userId = SecurityContext.getPrincipal().getUserId();
        String descr = DomHelper.documentToString(document);
        ResultType resultType = ResultType.SUCCESS;
        log(objectType, account.getId(), userId, ActionType.UPDATE, descr, null, resultType);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logFileSystem(Long auditObjectId, Set<String> added, Set<String> updated, Set<String> removed) {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", ActionType.UPDATE.getName());

        Element files = root
                .addElement("fileManager")
                .addAttribute("id", String.valueOf(auditObjectId));

        addFiles(files, ChangeType.ADD, added);
        addFiles(files, ChangeType.UPDATE, updated);
        addFiles(files, ChangeType.REMOVE, removed);

        Long userId = SecurityContext.getPrincipal().getUserId();
        String xml = DomHelper.documentToString(document);
        log(ObjectType.FileManager, auditObjectId, userId, ActionType.UPDATE, xml, null, ResultType.SUCCESS);
    }

    @Override
    public void logAudienceChannelUIDsUpdated(AudienceChannel channel, long uidCount) {
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement("auditRecord")
                .addAttribute("version", "1.1")
                .addAttribute("type", ActionType.UPDATE.getName());

        Element entityElement = root.addElement("entity");
        entityElement.addAttribute("class", channel.getClass().getName());
        entityElement.addAttribute("id", channel.getId().toString());
        entityElement.addAttribute("name", channel.getName());
        Element property = entityElement.addElement("property");
        property.addAttribute("name", "uidCount");
        property.addAttribute("changeType", ChangeType.UPDATE.name());
        property.setText(String.valueOf(uidCount));

        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        String xml = DomHelper.documentToString(document);

        log(ObjectType.AudienceChannel, channel.getId(), principal.getUserId(), ActionType.UPDATE, xml, principal.getRemoteUserIP(), ResultType.SUCCESS);
    }

    private void addFiles(Element parent, ChangeType changeType, Set<String> changes) {
        if (changes.isEmpty()) {
            return;
        }

        for (String change : changes) {
            parent.addElement("item")
                  .addAttribute("changeType", changeType.name())
                  .setText(change);
        }
    }
}
