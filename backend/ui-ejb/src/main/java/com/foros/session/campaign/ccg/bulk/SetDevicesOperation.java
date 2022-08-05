package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.channel.service.DeviceChannelService;

import java.util.Set;

public class SetDevicesOperation extends DevicesOperationSupport {
    public SetDevicesOperation(
            Set<Long> deviceChannelIds,
            DeviceChannelService deviceChannelService) {
        super(deviceChannelIds, deviceChannelService);
    }

    public Set<DeviceChannel> getChannelsToUpdate(CampaignCreativeGroup existing) {
        return deviceChannelService.getNormalizedDeviceChannelsCollection(deviceChannelIds, getAccountTypeChannelIds(existing));
    }
}
