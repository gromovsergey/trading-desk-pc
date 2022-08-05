package com.foros.action.campaign.campaignGroup;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;

import java.util.HashSet;
import java.util.Set;

public class SaveDeviceTargetingAction extends DeviceTargetingActionSupport {

    public String update() {
        CampaignCreativeGroup existing = groupService.view(getModel().getId());
        group.setCampaign(existing.getCampaign());
        populateTargeting();
        group.setDeviceChannels(getDeviceChannels());
        group.setCcgType(existing.getCcgType());
        groupService.updateDeviceTargeting(group);
        return SUCCESS;
    }

    private Set<DeviceChannel> getDeviceChannels() {
        Set<DeviceChannel> res = new HashSet<>();
        for (Long id: getDeviceHelper().getSelectedChannels()) {
            res.add(new DeviceChannel(id));
        }
        return res;
    }
}
