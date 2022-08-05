package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.InternalAccount;
import com.foros.session.account.AccountService;
import com.foros.session.auctionSettings.AuctionSettingsService;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class AccountAuctionSettingsActionBase extends BaseActionSupport
        implements ModelDriven<AccountAuctionSettings>, RequestContextsAware, BreadcrumbsSupport {

    @EJB
    protected AccountService accountService;

    @EJB
    protected AuctionSettingsService auctionSettingsService;

    private Long id;
    private InternalAccount internalAccount;
    protected AccountAuctionSettings auctionSettings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InternalAccount getInternalAccount() {
        if (internalAccount == null) {
            internalAccount = accountService.viewInternalAccount(id);
        }
        return internalAccount;
    }

    @Override
    public AccountAuctionSettings getModel() {
        return auctionSettings;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        ContextBase context = contexts.getContext(getInternalAccount().getRole());

        if (context != null) {
            context.switchTo(getInternalAccount());
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(getInternalAccount()))
                .add(new SimpleTextBreadcrumbsElement("AuctionSettings.breadcrumbs"));
    }
}
