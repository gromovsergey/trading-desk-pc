package com.foros.util;

public class Stats {

    private long updated = 0;
    private long created = 0;

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void appendCreated() {
        created++;
    }

    public void appendUpdated() {
        updated++;
    }

}
