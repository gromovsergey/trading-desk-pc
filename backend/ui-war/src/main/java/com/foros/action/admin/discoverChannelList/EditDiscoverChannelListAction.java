package com.foros.action.admin.discoverChannelList;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.AccountUtil;
import com.foros.util.EntityUtils;

public class EditDiscoverChannelListAction extends EditDiscoverChannelListActionBase implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    public EditDiscoverChannelListAction() {
        model = new DiscoverChannelList();
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
        getModel().setChannelNameMacro(DiscoverChannelList.KEYWORD_TOKEN);
        getModel().setKeywordTriggerMacro(DiscoverChannelList.KEYWORD_TOKEN);
        getModel().setDiscoverQuery(DiscoverChannelList.KEYWORD_TOKEN);
        getModel().setDiscoverAnnotation(DiscoverChannelList.KEYWORD_TOKEN);

        initChannelOwners();
        populateDependenciesForEdit();
        breadcrumbs = new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.update")
    public String edit() throws Exception {
        model = discoverChannelListService.view(getModel().getId());
        Account account = model.getAccount();
        setAccountId(account.getId());
        setAccountName(EntityUtils.appendStatusSuffix(account.getName(), account.getStatus()));
        populateDependenciesForEdit();
        populateAlreadyExistingChannels();
        breadcrumbs = new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(new DiscoverChannelListBreadcrumbsElement(model)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
