package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.channel.geo.GeoChannelService;

import javax.ejb.EJB;

public class BulkGeotargetActionSupport extends CcgEditBulkActionSupport {

    @EJB
    protected GeoChannelService geoChannelService;

    @EJB
    protected CampaignCreativeGroupService groupService;

    protected Mode editMode = Mode.Set;

    public Mode getEditMode() {
        return editMode;
    }

    public void setEditMode(Mode editMode) {
        this.editMode = editMode;
    }

    public static enum Mode {
        Add,
        Remove,
        Set
    }
}
