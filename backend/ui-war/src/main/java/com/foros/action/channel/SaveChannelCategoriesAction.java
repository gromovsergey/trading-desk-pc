package com.foros.action.channel;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannelList;

import java.util.HashSet;
import java.util.Set;

public class SaveChannelCategoriesAction extends ChannelCategoriesActionSupport implements BreadcrumbsSupport {

    public String save() {
        Set<CategoryChannel> categories = new HashSet<CategoryChannel>();
        for (Long categoryId : getSelectedCategories()) {
            CategoryChannel selectedCategoryChannel = new CategoryChannel();
            selectedCategoryChannel.setId(categoryId);
            categories.add(selectedCategoryChannel);
        }

        Channel m = getModel();
        m.setCategories(categories);

        if (m instanceof DiscoverChannelList) {
            categoryChannelService.updateDiscoverListCategories((DiscoverChannelList) m);
        } else {
            categoryChannelService.updateChannelCategories(m);
        }
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel()).add(new SimpleTextBreadcrumbsElement("channel.channelCategories.edit"));
    }
}
