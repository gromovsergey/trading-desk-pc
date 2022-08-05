package com.foros.action.admin.accountType;

import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.bean.Filter;
import com.foros.util.helper.ServiceHelper;
import com.foros.util.tree.TreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceTargetingHelper {
    private DeviceChannelService deviceChannelService = ServiceHelper.getDeviceService();
    private Collection<Long> selectedChannels = new HashSet<Long>();
    private TreeNode<EntityTO> applicationsTreeRoot;
    private TreeNode<EntityTO> browsersTreeRoot;
    private Collection<Long> disabledChannels = new HashSet<Long>();
    private Collection<TreeNode> openNodes = new ArrayList<TreeNode>();
    private List<IdNameTO> browsersRootChannels;

    private Filter<TreeNode<EntityTO>> filter;

    public DeviceTargetingHelper(Collection<DeviceChannel> channels, Filter<TreeNode<EntityTO>> filter) {
        for (DeviceChannel dc : channels) {
            selectedChannels.add(dc.getId());
        }

        this.filter = filter;

        applicationsTreeRoot = filterTreeNodes(deviceChannelService.getApplicationsTreeRoot());
        browsersTreeRoot = filterTreeNodes(deviceChannelService.getBrowsersTreeRoot());

        if (applicationsTreeRoot != null) {
            for (TreeNode<EntityTO> node : applicationsTreeRoot.getChildren()) {
                if (node.getLevel() != 1) {
                    continue;
                }
                openNodes.add(node);
            }
        }

        if (browsersTreeRoot != null) {
            for (TreeNode<EntityTO> node : browsersTreeRoot.getChildren()) {
                if (node.getLevel() != 1) {
                    continue;
                }
                openNodes.add(node);
            }
        }
    }

    private TreeNode<EntityTO> filterTreeNodes(TreeNode<EntityTO> root) {
        for (int i = root.getChildren().size() - 1; i >= 0; i--) {
            TreeNode<EntityTO> child = root.getChildren().get(i);
            filterTreeNodes(child);
            if (!filter.accept(child)) {
                root.getChildren().remove(i);
            }
        }
        if (filter.accept(root)) {
            return root;
        } else {
            return null;
        }
    }

    public TreeNode<EntityTO> getApplicationsTreeRoot() {
        return applicationsTreeRoot;
    }

    public TreeNode<EntityTO> getBrowsersTreeRoot() {
        return browsersTreeRoot;
    }

    public Collection<Long> getDisabledChannels() {
        return disabledChannels;
    }

    public void setDisabledChannels(Collection<Long> disabledChannels) {
        this.disabledChannels = disabledChannels;
    }

    public void populateTargeting(Set<Long> allowedChannels) {
        Set<Long> channels = new HashSet<>(selectedChannels);

        if (applicationsTreeRoot != null) {
            for (TreeNode<EntityTO> child : applicationsTreeRoot.getChildren()) {
                addToSelectedDisabled(child, selectedChannels, allowedChannels, false);
                if (!selectedChannels.contains(child.getElement().getId())) {
                    markOpenedNodes(child, selectedChannels, openNodes);
                }
            }
        }

        if (browsersTreeRoot != null) {
            for (TreeNode<EntityTO> child : browsersTreeRoot.getChildren()) {
                addToSelectedDisabled(child, selectedChannels, allowedChannels, false);
                if (!selectedChannels.contains(child.getElement().getId())) {
                    markOpenedNodes(child, selectedChannels, openNodes);
                }
            }
        }

        getDisabledChannels().removeAll(channels);

    }

    private void addToSelectedDisabled(TreeNode<EntityTO> node, Collection<Long> selectedChannels, Set<Long> allowedChannels, boolean isNodeSelected) {
        isNodeSelected = isNodeSelected || selectedChannels.contains(node.getElement().getId());
        if (isNodeSelected) {
            selectedChannels.add(node.getElement().getId());
        }
        if (!allowedChannels.contains(node.getElement().getId())) {
            getDisabledChannels().add(node.getElement().getId());
        }
        for (TreeNode<EntityTO> child : node.getChildren()) {
            addToSelectedDisabled(child, selectedChannels, allowedChannels, isNodeSelected);
        }
    }

    private void markOpenedNodes(TreeNode<EntityTO> node, Collection<Long> selectedChannels, Collection<TreeNode> openNodes) {
        boolean hasSelectedChildren = false;
        for (TreeNode<EntityTO> child : node.getChildren()) {
            if (selectedChannels.contains(child.getElement().getId())) {
                hasSelectedChildren = true;
            } else {
                markOpenedNodes(child, selectedChannels, openNodes);
            }
        }
        if (hasSelectedChildren) {
            openNodes.add(node);
        }
    }

    public Collection<TreeNode> getOpenNodes() {
        return openNodes;
    }

    public Collection<Long> getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(Collection<Long> selectedChannels) {
        this.selectedChannels = selectedChannels;
    }

    public List<IdNameTO> getBrowsersRootChannels() {
        if (browsersRootChannels == null) {
            browsersRootChannels = new ArrayList<>(2);

            DeviceChannel mobileDeviceChannel = deviceChannelService.getMobileDevicesChannel();
            browsersRootChannels.add(new IdNameTO(mobileDeviceChannel.getId(), "mobile"));

            DeviceChannel nonMobileDeviceChannel = deviceChannelService.getNonMobileDevicesChannel();
            browsersRootChannels.add(new IdNameTO(nonMobileDeviceChannel.getId(), "nonMobile"));
        }
        return browsersRootChannels;
    }
}