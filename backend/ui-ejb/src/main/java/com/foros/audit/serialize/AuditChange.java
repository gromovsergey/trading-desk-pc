package com.foros.audit.serialize;

import com.foros.model.security.ActionType;

public class AuditChange {

    private Object entity;
    private ActionType actionType;

    public AuditChange(Object entity, ActionType actionType) {
        this.entity = entity;
        this.actionType = actionType;
    }

    public Object getRoot() {
        return entity;
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditChange that = (AuditChange) o;

        if (!entity.equals(that.entity)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }
}
