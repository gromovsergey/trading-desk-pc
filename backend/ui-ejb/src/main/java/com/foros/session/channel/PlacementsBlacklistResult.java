package com.foros.session.channel;

import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;

import java.io.Serializable;
import java.util.List;

public class PlacementsBlacklistResult implements Serializable {
    private List<PlacementBlacklist> placements;
    private String countryCode;

    public PlacementsBlacklistResult(List<PlacementBlacklist> placements, String countryCode) {
        this.placements = placements;
        this.countryCode = countryCode;
    }

    public List<PlacementBlacklist> getPlacements() {
        return placements;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
