package com.foros.action.admin.categoryChannel;

import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.CategoryChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.AccountUtil;

public class SaveCategoryChannelAction extends CategoryChannelActionSupport implements Invalidable, BreadcrumbsSupport {
    @Restrict(restriction = "CategoryChannel.update")
    public String save() {
        if (getModel().getId() == null) {
            create();
        } else {
            update();
        }

        if (hasFieldErrors()) {
            invalid();
            return INPUT;
        }
        return SUCCESS;
    }

    private void create() {
        getModel().setAccount(AccountUtil.extractAccountById(getAccountId()));
        getModel().getAccount().unregisterChange("id");
        getModel().unregisterChange("id");
        categoryChannelService.createChannel(getModel());
    }

    private void update() {
        CategoryChannel oldChannel = categoryChannelService.view(getModel().getId());
        getModel().setAccount(oldChannel.getAccount());
        getModel().unregisterChange("account");
        getModel().unregisterChange("id");
        categoryChannelService.updateChannel(getModel());
    }

    @Override
    public void invalid() {
        initChannelOwners();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (getModel().getId() != null) {
            CategoryChannel persistent = categoryChannelService.find(getModel().getId());
            breadcrumbs = new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(new CategoryChannelBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
