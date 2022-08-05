package com.foros.action.channel;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.GenericChannel;
import com.foros.session.EntityTO;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.tree.TreeHolder;
import com.foros.util.tree.TreeNode;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import javax.ejb.EJB;

public class ChannelCategoriesActionSupport extends ViewEditChannelActionSupport<Channel> implements RequestContextsAware {
    @EJB
    protected SearchChannelService channelService;

    @EJB
    protected CategoryChannelService categoryChannelService;

    protected Collection<Long> selectedCategories = new LinkedList<Long>();

    private TreeHolder<EntityTO> categoryChannelTree;

    public ChannelCategoriesActionSupport() {
        model = new GenericChannel();
    }

    public Collection<Long> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(Collection<Long> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    @Override
    public Account getExistingAccount() {
        return getModel().getAccount();
    }

    @Override
    public Channel getModel() {
        if (model instanceof GenericChannel && model.getId() != null) {
            Channel m = model;
            model = channelService.findWithCategories(model.getId());
            if (m.isChanged("version")) {
                model.setVersion(m.getVersion());
            }
        }
        return model;
    }

    public TreeHolder<EntityTO> getCategoryChannelTree() {
        if (categoryChannelTree != null) {
            return categoryChannelTree;
        }

        categoryChannelTree = categoryChannelService.getCategoryChannelTree(model.getId());

        categoryChannelTree.sort(new Comparator<TreeNode<EntityTO>>() {
            @Override
            public int compare(TreeNode<EntityTO> o1, TreeNode<EntityTO> o2) {
                return LocalizableNameUtil.getComparator().compare(o1.getElement()
                        .getLocalizableName(), o2.getElement().getLocalizableName());
            }
        });

        return categoryChannelTree;
    }
}
