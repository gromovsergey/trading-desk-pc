package com.foros.action.campaign;

import com.foros.action.IdNameBean;
import com.foros.model.DisplayStatus;

public class CampaignCreativeGroupRowTO extends IdNameBean {
    private DisplayStatus displayStatus;
    private String impressionsCount;
    private String dates;
    private String ccgPageExtension;

    public CampaignCreativeGroupRowTO(String id, String name, DisplayStatus displayStatus, String impressionsCount, String dates, String ccgPageExtension) {
        super(id, name);
        this.displayStatus = displayStatus;
        this.impressionsCount = impressionsCount;
        this.dates = dates;
        this.ccgPageExtension = ccgPageExtension;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus status) {
        this.displayStatus = status;
    }

    public String getImpressionsCount() {
        return impressionsCount;
    }

    public void setImpressionsCount(String impressionsCount) {
        this.impressionsCount = impressionsCount;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getCcgPageExtension() {
        return ccgPageExtension;
    }

    public void setCcgPageExtension(String ccgPageExtension) {
        this.ccgPageExtension = ccgPageExtension;
    }

}
