package com.foros.action.channel.expression;

import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.model.account.Account;
import com.foros.model.channel.ExpressionChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;

public class CreateExpressionChannelAction extends SearchChannelSupport
    implements CmpSelfIdAware, AgencySelfIdAware, AdvertiserSelfIdAware, BreadcrumbsSupport {

    private Long accountId;

    public CreateExpressionChannelAction() {
        model = new ExpressionChannel();
    }

    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.create", parameters = "#target.existingAccount")
    public String create() {
        loadAccount();
        searchCriteria.populateConditionOfVisibility(getExistingAccount());
        model.setCountry(model.getAccount().getCountry());
        return SUCCESS;
    }

    @Override
    public Account getExistingAccount() {
        if (model.getAccount() == null) {
            loadAccount();
        }
        return model.getAccount();
    }

    private void loadAccount() {
        Account account = accountService.find(accountId);
        model.setAccount(account);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void setAdvertiserId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void setAgencyId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void setCmpId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (model.getAccount().getRole() == AccountRole.INTERNAL) {
            breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
