package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.web.taglib.RestrictionTools;

public class MainAdvertiserDashboardAction extends BaseActionSupport implements AdvertiserSelfIdAware, AgencySelfIdAware {

    private Long agencyId;
    private Long advertiserId;

    @ReadOnly
    public String switchTo() {
        return gotoAdvertiser();
    }

    @ReadOnly
    public String main() {
        if (advertiserId == null) {
            return "dashboard.advertisers";
        }
        return gotoAdvertiser();
    }

    private String gotoAdvertiser() {
        if (RestrictionTools.isPermitted("AdvertiserEntity.view")) {
            return "dashboard.campaigns";
        }
        return "dashboard.advertiserAccount";
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }
}
