package com.foros.session.channel.service;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.channel.DeviceChannelTO;
import com.foros.session.query.PartialList;
import com.foros.util.tree.TreeNode;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface DeviceChannelService {

    DeviceChannel view(Long id);
    DeviceChannel findById(Long id);

    DeviceChannel getBrowsersChannel();
    DeviceChannel getApplicationsChannel();
    DeviceChannel getNonMobileDevicesChannel();
    DeviceChannel getMobileDevicesChannel();

    void create(DeviceChannel channel);
    void update(DeviceChannel channel);

    void activate(Long id);
    void inactivate(Long id);
    void delete(Long id);
    void undelete(Long id);

    List<DeviceChannelTO> getChannelList(Long parentChannelId);
    List<EntityTO> getChannelAncestorsChain(Long channelId, boolean keepLastChain);

    TreeNode<EntityTO> getBrowsersTreeRoot();
    TreeNode<EntityTO> getApplicationsTreeRoot();
    PartialList<CampaignCreativeGroup> searchAssociatedCampaigns(Long id, int from, int count);

    /*
     * @param deviceChannelIds IDs of explicitly selected Device Channels
     * @param allowedChannelIds IDs allowed to be selected
     * @return Set of most top channels, which are:
     *         - Channel ID in @allowedChannelIds
     *         - Channel is Live (has Status = Active)
     *         - Channel ID in @deviceChannelIds without [grand] children or all [its] grand children are in ( @deviceChannelIds or un-selectable)
     *         Un-selectable:
     *         - Channel ID is not in @allowedChannelIds or is not Live
     */
    Set<DeviceChannel> getNormalizedDeviceChannelsCollection(Set<Long> deviceChannelIds, Set<Long> allowedChannelIds);
}
