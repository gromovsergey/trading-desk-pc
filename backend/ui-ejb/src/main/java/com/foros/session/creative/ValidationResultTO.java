package com.foros.session.creative;

import com.foros.util.Stats;

public class ValidationResultTO {
    private Stats creatives = new Stats();
    private long lineWithErrors = 0;
    private String id;

    public long getLineWithErrors() {
        return lineWithErrors;
    }

    public void setLineWithErrors(long lineWithErrors) {
        this.lineWithErrors = lineWithErrors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stats getCreatives() {
        return creatives;
    }

    public void setCreatives(Stats creatives) {
        this.creatives = creatives;
    }
}
