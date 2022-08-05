package com.foros.action.channel;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.CategoryChannel;
import com.foros.restriction.annotation.Restrict;

public class EditChannelCategoriesAction extends ChannelCategoriesActionSupport implements BreadcrumbsSupport {

    @ReadOnly
    @Restrict(restriction = "Channel.editCategories", parameters = "#target.model")
    public String edit() {
        if (!selectedCategories.isEmpty()) {
            selectedCategories.clear();
        }
        for (CategoryChannel cc : getModel().getCategories()) {
            selectedCategories.add(cc.getId());
        }
        return INPUT;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel()).add(new SimpleTextBreadcrumbsElement("channel.channelCategories.edit"));
    }
}
