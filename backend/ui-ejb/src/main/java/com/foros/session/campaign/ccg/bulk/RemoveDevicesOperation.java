package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.EntityUtils;

import java.util.Set;

public class RemoveDevicesOperation extends DevicesOperationSupport {
    public RemoveDevicesOperation(
            Set<Long> deviceChannelIds,
            DeviceChannelService deviceChannelService) {
        super(deviceChannelIds, deviceChannelService);
    }

    public Set<DeviceChannel> getChannelsToUpdate(CampaignCreativeGroup existing) {
        Set<Long> allowedChannelIds = getAccountTypeChannelIds(existing);
        Set<Long> channelsWithChildren = expandParentChannels(getGroupChannelIds(existing));
        Set<DeviceChannel> normalizedRemoved = deviceChannelService.getNormalizedDeviceChannelsCollection(deviceChannelIds, allowedChannelIds);
        Set<Long> removedWithChildren = expandParentChannels(EntityUtils.getEntityIds(normalizedRemoved));
        channelsWithChildren.removeAll(removedWithChildren);
        return deviceChannelService.getNormalizedDeviceChannelsCollection(channelsWithChildren, allowedChannelIds);
    }
}
