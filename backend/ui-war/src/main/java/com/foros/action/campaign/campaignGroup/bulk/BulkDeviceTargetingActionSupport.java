package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.action.admin.accountType.DeviceTargetingHelper;
import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.bean.Filter;
import com.foros.util.tree.TreeNode;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;

public class BulkDeviceTargetingActionSupport extends CcgEditBulkActionSupport {
    public static enum Mode {
        Set,
        Add,
        Remove
    }

    @EJB
    private AccountTypeService accountTypeService;

    @EJB
    protected DeviceChannelService deviceChannelService;

    protected DeviceTargetingHelper deviceHelper;
    protected Set<Long> allowedChannels = new HashSet<Long>();

    protected Mode editMode = Mode.Set;
    protected Set<Long> setIds;
    protected Set<Long> addIds;
    protected Set<Long> removeIds;

    public Set<Long> getAddIds() {
        return addIds;
    }

    public DeviceTargetingHelper getDeviceHelper() {
        if (deviceHelper == null) {
            initDeviceHelper();
        }
        return deviceHelper;
    }

    public Mode getEditMode() {
        return editMode;
    }

    public Set<Long> getRemoveIds() {
        return removeIds;
    }

    public Set<Long> getSetIds() {
        return setIds;
    }

    protected void initDeviceHelper() {
        Set<DeviceChannel> channels = new HashSet<>();
        deviceHelper = new DeviceTargetingHelper(channels, new Filter<TreeNode<EntityTO>>() {
            @Override
            public boolean accept(TreeNode<EntityTO> node) {
                if (!node.getElement().getStatus().equals(Status.ACTIVE)) {
                    return false;
                }
                if (node.getChildren().size() > 0) {
                    return true;
                }

                if (allowedChannels.contains(node.getElement().getId())) {
                    return true;
                }
                return false;
            }

        });
    }

    protected void populateTargeting() {
        Set<DeviceChannel> deviceChannels = accountTypeService.getAccountDeviceChannels(getAccount().getAccountType().getId());
        for (DeviceChannel dc: deviceChannels) {
            allowedChannels.add(dc.getId());
        }
        getDeviceHelper().populateTargeting(allowedChannels);
    }

    public void setAddIds(Set<Long> addIds) {
        this.addIds = addIds;
    }

    public void setDeviceHelper(DeviceTargetingHelper deviceHelper) {
        this.deviceHelper = deviceHelper;
    }

    public void setEditMode(Mode editMode) {
        this.editMode = editMode;
    }

    public void setRemoveIds(Set<Long> removeIds) {
        this.removeIds = removeIds;
    }

    public void setSetIds(Set<Long> setIds) {
        this.setIds = setIds;
    }
}
