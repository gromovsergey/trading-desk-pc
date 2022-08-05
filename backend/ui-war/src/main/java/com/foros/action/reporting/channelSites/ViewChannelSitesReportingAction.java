package com.foros.action.reporting.channelSites;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.security.AccountTO;
import com.foros.session.security.ManagerAccountTO;
import com.foros.util.helper.IndexHelper;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

public class ViewChannelSitesReportingAction extends BaseActionSupport {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private SearchChannelService channelService;

    // parameters
    private Long accountId;
    private Long channelId;

    // model
    private ManagerAccountTO account;
    private Collection<? extends AccountTO> accounts;
    private EntityTO channel;

    private Boolean isInternal;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'channelSites'")
    public String view() throws Exception {
        return "success";
    }

    public Collection<? extends AccountTO> getAccounts() {
        if (accounts != null) {
            return accounts;
        }

        if (accountId == null) {
            accounts = IndexHelper.getAccountsList(AccountRole.INTERNAL, AccountRole.ADVERTISER, AccountRole.AGENCY, AccountRole.CMP);
        } else {
            accounts = Collections.emptyList();
        }

        return accounts;
    }

    public ManagerAccountTO getAccount() {
        if (account != null || accountId == null) {
            return account;
        }

        Account account = isInternal() ? accountService.find(accountId) : accountService.getMyAccount();
        this.account = new ManagerAccountTO(account.getId(), account.getName(), account.getStatus().getLetter(), account.getRole(), account.getFlags());

        return this.account;
    }

    public EntityTO getChannel() {
        if (channel != null || channelId == null) {
            return channel;
        }

        Channel channel = channelService.find(channelId);
        this.channel = new EntityTO(channel.getId(), channel.getName(), channel.getStatus().getLetter());
        return this.channel;
    }

    public boolean canSelectAccount() {
        return accountId == null && isInternal();
    }

    public boolean canSelectChannel() {
        return channelId == null;
    }

    @Override
    public boolean isInternal() {
        if (isInternal == null) {
            isInternal = currentUserService.isInternal();
        }
        return isInternal;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
