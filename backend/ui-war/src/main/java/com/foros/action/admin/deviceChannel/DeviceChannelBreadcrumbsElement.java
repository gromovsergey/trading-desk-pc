package com.foros.action.admin.deviceChannel;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.DeviceChannel;

public class DeviceChannelBreadcrumbsElement extends EntityBreadcrumbsElement {
    public DeviceChannelBreadcrumbsElement(DeviceChannel deviceChannel) {
        super("channel.breadcrumbs.deviceChannel", deviceChannel.getId(), deviceChannel.getName(), "DeviceChannel/view");
    }
}
