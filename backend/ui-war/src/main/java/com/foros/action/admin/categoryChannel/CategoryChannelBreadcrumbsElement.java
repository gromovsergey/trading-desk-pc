package com.foros.action.admin.categoryChannel;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.channel.CategoryChannel;

public class CategoryChannelBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CategoryChannelBreadcrumbsElement(CategoryChannel categoryChannel) {
        super("channel.breadcrumbs.categoryChannel", categoryChannel.getId(), categoryChannel.getName(), "CategoryChannel/view");
    }
}
