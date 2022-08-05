package com.foros.session.campaign.ccg.bulk;

import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.EntityUtils;
import com.foros.util.tree.TreeNode;

import java.util.HashSet;
import java.util.Set;

public abstract class DevicesOperationSupport implements BulkOperation<CampaignCreativeGroup> {

    protected final DeviceChannelService deviceChannelService;
    protected final Set<Long> deviceChannelIds;

    public DevicesOperationSupport(
            Set<Long> deviceChannelIds,
            DeviceChannelService deviceChannelService) {
        this.deviceChannelIds = deviceChannelIds;
        this.deviceChannelService = deviceChannelService;
    }

    public Set<DeviceChannel> getDeviceChannels() {
        Set<DeviceChannel> deviceChannels = new HashSet<DeviceChannel>();
        for (Long id: deviceChannelIds) {
            deviceChannels.add(new DeviceChannel(id));
        }
        return deviceChannels;
    }

    public abstract Set<DeviceChannel> getChannelsToUpdate(CampaignCreativeGroup existing);

    public Set<Long> getDeviceChannelIds() {
        return deviceChannelIds;
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        if (existing.getStatus() == Status.DELETED) {
            return;
        }

        toUpdate.setDeviceChannels(getChannelsToUpdate(existing));
    }

    protected Set<Long> getAccountTypeChannelIds(CampaignCreativeGroup existing) {
        return EntityUtils.getEntityIds(existing.getAccount().getAccountType().getDeviceChannels());
    }

    protected Set<Long> getGroupChannelIds(CampaignCreativeGroup existing) {
        return EntityUtils.getEntityIds(existing.getDeviceChannels());
    }

    private void addAllChildren(TreeNode<EntityTO> node, Set<Long> newChannelIds) {
        for (TreeNode<EntityTO> child: node.getChildren()) {
            addAllChildren(child, newChannelIds);
        }
        newChannelIds.add(node.getElement().getId());
    }

    private void checkSelectedChildren(TreeNode<EntityTO> node, Set<Long> channelIds, Set<Long> newChannelIds) {
        boolean selected = (channelIds.isEmpty() || channelIds.contains(node.getElement().getId()));
        if (selected) {
            addAllChildren(node, newChannelIds);
        } else {
            for (TreeNode<EntityTO> child: node.getChildren()) {
                checkSelectedChildren(child, channelIds, newChannelIds);
            }
        }
    }

    protected Set<Long> expandParentChannels(Set<Long> channelIds) {
        Set<Long> newChannelIds = new HashSet<>();
        checkSelectedChildren(deviceChannelService.getBrowsersTreeRoot(), channelIds, newChannelIds);
        checkSelectedChildren(deviceChannelService.getApplicationsTreeRoot(), channelIds, newChannelIds);
        return newChannelIds;
    }
}
