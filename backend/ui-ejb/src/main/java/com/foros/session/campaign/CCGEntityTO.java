package com.foros.session.campaign;

import com.foros.model.DisplayStatus;
import com.foros.model.Flags;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.EntityTO;


public class CCGEntityTO extends EntityTO {

    private CCGType ccgType;
    private DisplayStatus displayStatus;
    private Long flags;

    public CCGEntityTO(Long id, String name, char status, char ccgType) {
        super(id, name, status);
        this.ccgType = CCGType.valueOf(ccgType);
    }

    public CCGEntityTO(Long id, String name, char status, char ccgType, Long displayStatusId, Flags flags) {
        this(id, name, status, ccgType);
        this.displayStatus = CampaignCreativeGroup.getDisplayStatus(displayStatusId);
        this.flags = flags == null ? null : flags.longValue();
    }

    public CCGType getCcgType() {
        return ccgType;
    }

    public void setCcgType(CCGType ccgType) {
        this.ccgType = ccgType;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Long getFlags() {
        return flags == null ? 0 : flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
    }
}
