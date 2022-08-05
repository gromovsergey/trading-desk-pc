package com.foros.action.admin.platform;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.Platform;

public class PlatformBreadcrumbsElement extends EntityBreadcrumbsElement {
    public PlatformBreadcrumbsElement(Platform platform) {
        super("channel.breadcrumbs.platform", platform.getId(), platform.getName(), "Platform/view");
    }
}
