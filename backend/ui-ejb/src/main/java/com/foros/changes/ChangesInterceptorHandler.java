package com.foros.changes;

import com.foros.audit.serialize.AuditChange;
import com.foros.audit.serialize.serializer.AuditTreeSerializer;
import com.foros.changes.inspection.ChangeDescriptorRegistry;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.ChangesContainer;
import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.AuditLogRecord;
import com.foros.model.account.Account;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.OwnedEntity;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.util.PersistenceUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang.text.StrBuilder;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;

public class ChangesInterceptorHandler extends EmptyInterceptor {
    private static final Logger logger = Logger.getLogger(ChangesInterceptorHandler.class.getName());

    public static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private ChangesContainer container = null;
    private Set<AuditChange> auditChanges = new HashSet<AuditChange>();
    private Session session;
    private StrBuilder strBuilder;
    private ApplicationPrincipal principal;

    public void initialize(Session session, ChangeDescriptorRegistry registry) {
        this.session = session;
        this.container = new ChangesContainer(registry);
    }

    public void addAuditChange(AuditChange auditChange) {
        auditChanges.add(auditChange);
    }

    void clear() {
        auditChanges.clear();
        if (container != null) {
            container.clear();
        }
        principal = null;
    }

    @Override
    public boolean onFlushDirty(Object object, Serializable id, Object[] newValues, Object[] oldValues,
                                String[] properties, Type[] types) {
        if (isInitialized()) {
            container.addChangeRecord(object, ChangeType.UNCHANGED, newValues, oldValues);
        }
        
        return false;
    }

    @Override
    public boolean onSave(Object object, Serializable id, Object[] newValues,
                                String[] properties, Type[] types) {
        if (isInitialized()) {
            container.addChangeRecord(object, ChangeType.ADD, newValues, new Object[newValues.length]);
        }

        return false;
    }

    @Override
    public void onDelete(Object object, Serializable id, Object[] newValues,
                         String[] properties, Type[] types) {
        if (isInitialized()) {
            container.addChangeRecord(object, ChangeType.REMOVE, newValues, new Object[newValues.length]);
        }
    }

    public void processChanges() {
        flush();
        clear();
    }

    private void flush() {
        if (!isInitialized()) return;

        // prepare fields
        if (strBuilder == null) {
            strBuilder = new StrBuilder();
        }

        principal = SecurityContext.getPrincipal();
        for (AuditChange auditChange : auditChanges) {
            Object root = PersistenceUtils.unproxyIfInitialized(auditChange.getRoot());

            EntityChangeNode change = container.fetchChangesTree(root);
            if (change != null) {
                handle(change, auditChange.getActionType());
            }
        }

        session.flush();
    }

    private void handle(EntityChangeNode change, ActionType actionType) {
        AuditLogRecord record = new AuditLogRecord();

        record.setObjectId(change.geAuditLogId());
        record.setObjectType(ObjectType.valueOf(change.getType()));
        record.setActionType(actionType);
        record.setIP(CurrentUserSettingsHolder.getIpOrDefault());
        record.setUserId(principal == null || principal.isAnonymous() ? null : principal.getUserId());
        record.setActionDescription(serializeTree(change, actionType));
        final Object object = change.getLastDefinedValue();
        if (object instanceof OwnedEntity) {
            Account account = ((OwnedEntity) object).getAccount();
            if (account != null) {
                record.setObjectAccountId(account.getId());
            }
        }

        logger.info("Committing " + record.toString());
        session.persist(record);
    }

    private String serializeTree(EntityChangeNode change, ActionType actionType) {
        strBuilder.clear();
        try {
            XMLStreamWriter xmlWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(strBuilder.asWriter());
            AuditTreeSerializer serializer = new AuditTreeSerializer(actionType);
            serializer.startSerialize(xmlWriter, change);
            change.serialize(xmlWriter);
            serializer.endSerialize(xmlWriter);
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return strBuilder.toString();
    }

    /**
     * @return true, if interceptor is already initialized
     */
    public boolean isInitialized() {
        return container != null;
    }

    ChangesContainer getContainer() {
        return container;
    }
}
