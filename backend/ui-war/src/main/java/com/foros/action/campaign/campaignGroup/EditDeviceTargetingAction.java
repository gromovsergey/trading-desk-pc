package com.foros.action.campaign.campaignGroup;

import com.foros.framework.ReadOnly;
import com.foros.model.channel.DeviceChannel;
import com.foros.restriction.annotation.Restrict;

import java.util.Set;

public class EditDeviceTargetingAction extends DeviceTargetingActionSupport {

    @ReadOnly
    @Restrict(restriction = "CreativeGroup.updateDeviceTargeting", parameters = "find('CampaignCreativeGroup', #target.model.id)")
    public String edit() {
        deviceHelper = null;
        group = groupService.view(getModel().getId());
        Set<DeviceChannel> channels = group.getDeviceChannels();
        if (channels.isEmpty()) {
            channels.add(deviceChannelService.getApplicationsChannel());
            channels.add(deviceChannelService.getBrowsersChannel());
        }
        populateTargeting();

        return SUCCESS;
    }
}
