package com.foros.session.channel;

import com.foros.util.Stats;

public class PlacementsBlacklistValidationResultTO {

    private Stats placementsBlacklist = new Stats();
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

    public Stats getPlacementsBlacklist() {
        return placementsBlacklist;
    }

    public void setPlacementsBlacklist(Stats placementsBlacklist) {
        this.placementsBlacklist = placementsBlacklist;
    }
}
