package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeNode;

public interface EntityChangeNode extends ChangeNode {
    Class<?> getType();

    Object getId();

    String getIdProperty();

    Long geAuditLogId();

    void merge(EntityChangeNode entityChange);
}
