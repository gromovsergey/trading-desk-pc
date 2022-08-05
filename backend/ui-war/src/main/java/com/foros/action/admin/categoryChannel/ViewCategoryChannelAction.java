package com.foros.action.admin.categoryChannel;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.EntityUtils;

import javax.persistence.EntityNotFoundException;

public class ViewCategoryChannelAction extends CategoryChannelActionSupport implements BreadcrumbsSupport {
    @ReadOnly
    @Restrict(restriction = "CategoryChannel.view")
    public String view() {
        Long channelId = getModel().getId();
        if (channelId == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        categoryChannel = categoryChannelService.view(channelId);
        childrenChannels = categoryChannelService.getChannelList(channelId);
        Account account = categoryChannel.getAccount();
        setAccountName(EntityUtils.appendStatusSuffix(account.getName(), account.getStatus()));
        parentLocations = categoryChannelService.getChannelAncestorsChain(channelId, false);
        return INPUT;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(new CategoryChannelBreadcrumbsElement(getModel()));
    }
}
