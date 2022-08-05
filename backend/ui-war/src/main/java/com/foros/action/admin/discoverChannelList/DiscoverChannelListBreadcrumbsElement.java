package com.foros.action.admin.discoverChannelList;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.DiscoverChannelList;

public class DiscoverChannelListBreadcrumbsElement extends EntityBreadcrumbsElement {
    public DiscoverChannelListBreadcrumbsElement(DiscoverChannelList discoverChannelList) {
        super("channel.breadcrumbs.discoverChannelList", discoverChannelList.getId(), discoverChannelList.getName(), "DiscoverChannelList/view");
    }
}
