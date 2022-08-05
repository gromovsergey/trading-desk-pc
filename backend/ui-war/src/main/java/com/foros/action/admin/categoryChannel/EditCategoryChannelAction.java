package com.foros.action.admin.categoryChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.util.EntityUtils;

public class EditCategoryChannelAction extends CategoryChannelActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    public void prepare() {
        Long id = getModel().getId();
        if (id == null) {
            getModel().setStatus(Status.ACTIVE);
            initChannelOwners();
            ApplicationPrincipal principal = SecurityContext.getPrincipal();
            Long accountId = principal.getAccountId();
            getModel().setAccount(new InternalAccount(accountId));
            breadcrumbs = new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        } else {
            categoryChannel = categoryChannelService.view(id);
            Account account = categoryChannel.getAccount();
            setAccountId(account.getId());
            setAccountName(EntityUtils.appendStatusSuffix(account.getName(), account.getStatus()));
            parentLocations = categoryChannelService.getChannelAncestorsChain(id, false);
            breadcrumbs = new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(new CategoryChannelBreadcrumbsElement(getModel())).add(ActionBreadcrumbs.EDIT);
        }
    }

    @ReadOnly
    @Restrict(restriction = "CategoryChannel.update")
    public String edit() {
        prepare();
        return INPUT;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
