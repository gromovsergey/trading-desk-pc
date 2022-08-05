package com.foros.action.admin.channel;

import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.service.RemoteServiceException;
import com.foros.session.EntityTO;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.query.PartialList;
import com.foros.util.EntityUtils;
import com.foros.util.tree.TreeHolder;
import com.foros.util.tree.TreeNode;

import java.util.ArrayList;

public class SearchChannelsAction extends SearchChannelsActionBase {

    private PartialList<ChannelTO> channels;
    private Boolean resubmitRequired;

    @ReadOnly
    public String searchAdvertiserChannels() {
        //TODO: Delete this method after Channel interface for External user realization on struts 2 or realize here and rename
        return SUCCESS;
    }

    @Override
    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.view")
    public String main() {
        super.main();
        accounts = accountService.getAllChannelOwners();
        EntityUtils.applyStatusRules(accounts, null, true);
        prepareCategoryChannels();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.view")
    public String search() {
        try {
            channels = searchChannelService.search(
                    searchParams.getName(),
                    searchParams.getAccountId(),
                    searchParams.getCountryCode(),
                    searchParams.getTestOption(),
                    AdvertisingChannelType.byAliases(searchParams.getChannelType()),
                    ChannelSearchStatus.toDisplayStatuses(searchParams.getStatus()),
                    searchParams.getVisibilityCriteria(), searchParams.getPhrase(),
                    searchParams.getCategoryChannelId(),
                    searchParams.getFirstResultCount(), searchParams.getPageSize());
            searchParams.setTotal((long) channels.getTotal());
        } catch (RemoteServiceException e) {
            addActionError(getText("errors.serviceIsNotAvailable", new String[]{getText("channel.channelSearchService")}));
        }
        return SUCCESS;
    }

    public void prepareCategoryChannels() {
        TreeHolder<EntityTO> categories = categoryChannelService.getCategoryChannelTree(0);
        categoryChannels = new ArrayList<>();
        for (TreeNode<EntityTO> node : categories) {
            if (node.getElement() == null) {
                continue;
            } else {
                categoryChannels.add(node.getElement());
            }
        }
    }

    public PartialList<ChannelTO> getChannels(){
        return channels;
    }

    public Boolean getResubmitRequired() {
        return resubmitRequired;
    }

    public void setResubmitRequired(Boolean resubmitRequired) {
        this.resubmitRequired = resubmitRequired;
    }

}
