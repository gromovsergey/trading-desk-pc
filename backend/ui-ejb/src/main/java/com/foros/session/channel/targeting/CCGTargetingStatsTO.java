package com.foros.session.channel.targeting;

import java.util.Map;

public class CCGTargetingStatsTO {
    private TargetingStatsTO behaviors;
    private TargetingStatsTO country;
    private Map<Long,TargetingStatsTO> geolocations;
    private Map<Long, TargetingStatsTO> devices;
    private TargetingStatsTO colocations;
    private Map<Long, TargetingStatsTO> sites;
    private TargetingStatsTO userSampleGroups;
    private TargetingStatsTO total;

    public TargetingStatsTO getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(TargetingStatsTO behaviors) {
        this.behaviors = behaviors;
    }

    public TargetingStatsTO getCountry() {
        return country;
    }

    public void setCountry(TargetingStatsTO country) {
        this.country = country;
    }

    public Map<Long, TargetingStatsTO> getGeolocations() {
        return geolocations;
    }

    public void setGeolocations(Map<Long, TargetingStatsTO> geolocations) {
        this.geolocations = geolocations;
    }

    public void setDevices(Map<Long, TargetingStatsTO> devices) {
        this.devices = devices;
    }

    public Map<Long, TargetingStatsTO> getDevices() {
        return devices;
    }

    public TargetingStatsTO getColocations() {
        return colocations;
    }

    public void setColocations(TargetingStatsTO colocations) {
        this.colocations = colocations;
    }

    public void setSites(Map<Long, TargetingStatsTO> sites) {
        this.sites = sites;
    }

    public Map<Long, TargetingStatsTO> getSites() {
        return sites;
    }

    public void setUserSampleGroups(TargetingStatsTO userSampleGroups) {
        this.userSampleGroups = userSampleGroups;
    }

    public TargetingStatsTO getUserSampleGroups() {
        return userSampleGroups;
    }

    public void setTotal(TargetingStatsTO total) {
        this.total = total;
    }

    public TargetingStatsTO getTotal() {
        return total;
    }
}
