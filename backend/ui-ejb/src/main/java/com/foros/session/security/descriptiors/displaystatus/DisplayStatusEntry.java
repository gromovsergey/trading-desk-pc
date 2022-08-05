package com.foros.session.security.descriptiors.displaystatus;

import com.foros.util.SQLUtil;

public class DisplayStatusEntry {
    private Long id;
    private String entityTableName;

    public DisplayStatusEntry(Long id, String entityTableName) {
        this.id = id;
        this.entityTableName = entityTableName;
    }

    public Long getId() {
        return id;
    }

    public String getEntityTableName() {
        return entityTableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayStatusEntry that = (DisplayStatusEntry) o;

        if (!entityTableName.equals(that.entityTableName)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + entityTableName.hashCode();
        return result;
    }

    public String toPGString() {
        return "(" + SQLUtil.escapeStructValue(entityTableName) + "," + id + ")";
    }

    @Override
    public String toString() {
        return toPGString();
    }
}
