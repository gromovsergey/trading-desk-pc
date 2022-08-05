package com.foros.changes.inspection;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.changes.inspection.changeNode.FieldChange;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.EntityMode;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;

public class EntityChangeDescriptor {
    private static final Logger logger = Logger.getLogger(EntityChangeDescriptor.class.getName());

    public static final FieldChange[] EMPTY_CHANGES = new FieldChange[0];

    private final String name;
    private final ClassMetadata metadata;
    private final AuditSerializer serializer;
    private final ChangeNode.EntityChangeFactory entityChangeFactory;
    private final FieldChangeDescriptor[] fieldChangeDescriptors;
    private final String identifierPropertyName;
    private final Class<?> entityType;

    public EntityChangeDescriptor(Class<?> entityType, ClassMetadata metadata, ChangeNode.EntityChangeFactory entityChangeFactory, AuditSerializer serializer, FieldChangeDescriptor[] fieldChangeDescriptors) {
        this.entityType = entityType;
        this.name = entityType.getSimpleName();
        this.metadata = metadata;
        this.entityChangeFactory = entityChangeFactory;
        this.serializer = serializer;
        this.fieldChangeDescriptors = fieldChangeDescriptors;

        if (metadata != null) {
            this.identifierPropertyName = this.metadata.getIdentifierPropertyName();
        } else {
            this.identifierPropertyName = null;
        }
    }

    public int fieldsCount() {
        return fieldChangeDescriptors.length;
    }

    public FieldChange newChange(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
        if (descriptor == null) {
            return null;
        }
        return descriptor.newInstance(oldValue, newValue);
    }

    public AuditSerializer getSerializer() {
        return serializer;
    }

    @Override
    public String toString() {
        return "EntityChangeDescriptor[" + name + "]";
    }


    public EntityChangeNode newEntityChange(Object entity, ChangeType changeType, Object[] oldValues, Object[] newValues) {
        FieldChange[] changes = new FieldChange[fieldsCount()];

        StringBuilder loggerBuf = new StringBuilder("entityChange=");
        for (int i = 0; i < changes.length; i++) {
            Object newValue = newValues[i];
            Object oldValue = oldValues[i];
            FieldChangeDescriptor descriptor = fieldChangeDescriptors[i];
            FieldChange fieldChange = newChange(descriptor, oldValue, newValue);
            changes[i] = fieldChange;

            if (logger.isLoggable(Level.INFO)) {
                if (descriptor != null) {
                    loggerBuf.append('\n');
                    loggerBuf.append(entity.toString()).append(" ");
                    loggerBuf.append(descriptor.toString()).append(": ");
                    loggerBuf.append(fieldChange == null ? "null" : fieldChange.toString());
                }
            }
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info(loggerBuf.toString());
        }

        EntityChangeNode entityChangeNode = entityChangeFactory.newInstance(this, entity, changeType, changes);
        return entityChangeNode;
    }

    public EntityChangeNode newEntityChange(Object entity) {
        FieldChange[] fieldChanges = new FieldChange[fieldChangeDescriptors.length];
        for (int i = 0; i < fieldChangeDescriptors.length; i++) {
            FieldChangeDescriptor changeDescriptor = fieldChangeDescriptors[i];
            if (changeDescriptor == null || !changeDescriptor.isCascade()) {
                continue;
            }

            Object fieldValue = changeDescriptor.getValue(entity);
            FieldChange fieldChange = newChange(changeDescriptor, fieldValue, fieldValue);
            fieldChanges[i] = fieldChange;
        }
        return entityChangeFactory.newInstance(this, entity, ChangeType.UNCHANGED, fieldChanges);
    }

    public EntityChangeNode newEmptyEntityChange(Object entity) {
        return entityChangeFactory.newInstance(this, entity, ChangeType.UNCHANGED, EMPTY_CHANGES);
    }

    public Object getId(Object object) {
        Object id = null;
        if (object instanceof HibernateProxy) {
            id = ((HibernateProxy) object).getHibernateLazyInitializer().getIdentifier();
        } else if (metadata != null) {
            id = metadata.getIdentifier(object, EntityMode.POJO);
        }
        return id;
    }

    public String getIdProperty() {
        return identifierPropertyName;
    }

    public Class<?> getType() {
        return entityType;
    }

    /**
     * @return copy of the fieldChangeDescriptors array to keep the original immutable
     */
    public FieldChangeDescriptor[] getFieldChangeDescriptors() {
        return fieldChangeDescriptors.clone();
    }
}
