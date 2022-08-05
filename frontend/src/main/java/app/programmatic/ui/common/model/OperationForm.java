package app.programmatic.ui.common.model;

import java.util.List;

public class OperationForm {
    List<Long> siteIds;
    List<Long> excludedGeoChannelIds;
    List<Long> geoChannelIds;

    public List<Long> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Long> getExcludedGeoChannelIds() {
        return excludedGeoChannelIds;
    }

    public void setExcludedGeoChannelIds(List<Long> excludedGeoChannelIds) {
        this.excludedGeoChannelIds = excludedGeoChannelIds;
    }

    public List<Long> getGeoChannelIds() {
        return geoChannelIds;
    }

    public void setGeoChannelIds(List<Long> geoChannelIds) {
        this.geoChannelIds = geoChannelIds;
    }
}
