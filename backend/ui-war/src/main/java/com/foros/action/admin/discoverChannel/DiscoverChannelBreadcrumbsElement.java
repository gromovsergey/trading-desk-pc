package com.foros.action.admin.discoverChannel;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.DiscoverChannel;

public class DiscoverChannelBreadcrumbsElement extends EntityBreadcrumbsElement {
    public DiscoverChannelBreadcrumbsElement(DiscoverChannel discoverChannel) {
        super("channel.breadcrumbs.discoverChannel", discoverChannel.getId(), discoverChannel.getName(), "DiscoverChannel/view");
    }
}
