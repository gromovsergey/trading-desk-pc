package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.admin.accountType.DeviceTargetingHelper;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.bean.Filter;
import com.foros.util.context.RequestContexts;
import com.foros.util.tree.TreeNode;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class DeviceTargetingActionSupport extends BaseActionSupport implements ModelDriven<CampaignCreativeGroup>, RequestContextsAware, BreadcrumbsSupport {
    @EJB
    protected DeviceChannelService deviceChannelService;

    @EJB
    protected CampaignCreativeGroupService groupService;

    protected CampaignCreativeGroup group;
    protected DeviceTargetingHelper deviceHelper;
    protected Set<Long> allowedChannels = new HashSet<Long>();

    public DeviceTargetingActionSupport() {
        group = new CampaignCreativeGroup();
    }

    @Override
    public CampaignCreativeGroup getModel() {
        return group;
    }

    public CampaignCreativeGroup getGroup() {
        return group;
    }

    protected void populateTargeting() {

        for (DeviceChannel dc: group.getAccount().getAccountType().getDeviceChannels()) {
            allowedChannels.add(dc.getId());
        }

        getDeviceHelper().populateTargeting(allowedChannels);
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(group.getAccount().getId());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(group.getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(group))
                .add("ccg.deviceTargeting.edit");
    }

    protected void initDeviceHelper() {
        Set<DeviceChannel> channels = getModel().getDeviceChannels();
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

    public DeviceTargetingHelper getDeviceHelper() {
        if (deviceHelper == null) {
            initDeviceHelper();
        }
        return deviceHelper;
    }


    public void setDeviceHelper(DeviceTargetingHelper deviceHelper) {
        this.deviceHelper = deviceHelper;
    }

}
