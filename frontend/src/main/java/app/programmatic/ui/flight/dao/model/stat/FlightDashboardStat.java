package app.programmatic.ui.flight.dao.model.stat;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.Date;

public class FlightDashboardStat {
    private String agencyName;
    private Long agencyId;
    private String advertiserName;
    private Long advertiserId;
    private String flightName;
    private Long flightId;
    private MajorDisplayStatus displayStatus;
    private Date version;
    private String alertReason;
    private boolean isFlight;

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public boolean isFlight() {
        return isFlight;
    }

    public void setIsFlight(boolean flight) {
        isFlight = flight;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus status) {
        this.displayStatus = status;
    }

    public String getAlertReason() {
        return alertReason;
    }

    public void setAlertReason(String alertReason) {
        this.alertReason = alertReason;
    }
}
