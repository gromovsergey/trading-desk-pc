package com.foros.action.admin.bannedChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;

public class EditNoAdvChannelAction extends BannedChannelActionSupport implements BreadcrumbsSupport {

    @ReadOnly
    @Restrict(restriction="BannedChannel.update")
    public String edit() {
        model = service.getNoAdvBannedChannel();
        loadPageSearchKeywordsText();
        loadUrlsText();
        loadUrlKeywordsText();
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new NoAdvChannelBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
