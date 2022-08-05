package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.channel.service.DeviceChannelService;

import java.util.HashSet;
import java.util.Set;

public class AddDevicesOperation extends DevicesOperationSupport {
    public AddDevicesOperation(
            Set<Long> deviceChannelIds,
            DeviceChannelService deviceChannelService) {
        super(deviceChannelIds, deviceChannelService);
    }

    public Set<DeviceChannel> getChannelsToUpdate(CampaignCreativeGroup existing) {
        Set<DeviceChannel> existingChannels = existing.getDeviceChannels();
        if (existingChannels.isEmpty()) {
            return existingChannels; // if channels are empty it means that all already selected
        }

        Set<Long> newDeviceIds = new HashSet<>();
        newDeviceIds.addAll(expandParentChannels(getGroupChannelIds(existing)));
        newDeviceIds.addAll(expandParentChannels(deviceChannelIds));
        return deviceChannelService.getNormalizedDeviceChannelsCollection(newDeviceIds, getAccountTypeChannelIds(existing));
    }
}
