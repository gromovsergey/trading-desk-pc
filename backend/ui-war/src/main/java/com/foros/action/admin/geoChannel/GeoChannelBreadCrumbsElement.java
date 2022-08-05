package com.foros.action.admin.geoChannel;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.GeoChannel;

public class GeoChannelBreadCrumbsElement extends EntityBreadcrumbsElement {
    public GeoChannelBreadCrumbsElement(GeoChannel geoChannel) {
        super("channel.breadcrumbs.geoChannel", geoChannel.getId(), geoChannel.getName(), "GeoChannel/view");
    }
}
