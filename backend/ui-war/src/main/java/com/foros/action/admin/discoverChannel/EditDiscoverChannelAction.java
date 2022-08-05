package com.foros.action.admin.discoverChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.DiscoverChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.AccountUtil;
import com.foros.util.EntityUtils;

public class EditDiscoverChannelAction extends DiscoverChannelActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    public EditDiscoverChannelAction() {
        model = new DiscoverChannel();
        model.setAccount(new GenericAccount());
    }

    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.create")
    public String create() throws Exception {
        Long accountId = AccountUtil.getMyAccountId();
        Account account = accountService.find(accountId);
        getModel().setAccount(account);
        getModel().setCountry(account.getCountry());
        getModel().setLanguage(account.getCountry().getLanguage());
        getModel().setStatus(Status.ACTIVE);
        initChannelOwners();
        populateDependenciesForEdit();
        breadcrumbs = new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.update")
    public String edit() throws Exception {
        model = discoverChannelService.view(getModel().getId());
        Account account = model.getAccount();
        setAccountId(account.getId());
        setAccountName(EntityUtils.appendStatusSuffix(account.getName(), account.getStatus()));
        loadTriggers();
        populateDependenciesForEdit();
        breadcrumbs = new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(new DiscoverChannelBreadcrumbsElement(model)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public String createCopy() {
        getModel().setId(discoverChannelService.copy(getModel().getId()));
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
