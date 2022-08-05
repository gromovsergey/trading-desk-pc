package com.foros.action.reporting.channeltriggers;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AccountService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.security.ManagerAccountTO;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.Collections;
import javax.ejb.EJB;

public class ViewChannelTriggersReportingAction extends BaseActionSupport
        implements CmpSelfIdAware, AgencySelfIdAware, AdvertiserSelfIdAware, RequestContextsAware {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private SearchChannelService channelService;

    // parameters
    private Long accountId;
    private Long channelId;

    // model
    private Account account;
    private Channel channel;

    private Collection<ManagerAccountTO> accounts;

    private Boolean isInternal;

    @ReadOnly
    @Restrict(restriction = "Report.ChannelTriggers.view", parameters = "#target.channel")
    public String view() throws Exception {
        if (getChannel() != null) {
            account = getChannel().getAccount();
            accountId = account.getId();
        }
        return "success";
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        accountId = advertiserId;
    }

    @Override
    public void setAgencyId(Long agencyId) {
        accountId = agencyId;
    }

    @Override
    public void setCmpId(Long cmpId) {
        accountId = cmpId;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (accountId != null) {
            Account account = accountService.find(accountId);
            if (accountRestrictions.canView(account)) {
                contexts.switchTo(account);
            }
        }
    }

    public Collection<ManagerAccountTO> getAccounts() {
        if (accounts != null) {
            return accounts;
        }

        if (accountId == null) {
            accounts = EntityUtils.applyStatusRules(
                    accountService.getChannelOwners(), null, true);
        } else {
            accounts = Collections.emptyList();
        }

        return accounts;
    }

    public Account getAccount() {
        if (account != null || accountId == null) {
            return account;
        }
        account = accountService.find(accountId);
        return account;
    }

    public Channel getChannel() {
        if (channel != null || channelId == null) {
            return channel;
        }
        channel = channelService.find(channelId);
        return channel;
    }

    public boolean canSelectAccount() {
        return isInternal() && accountId == null;
    }

    public boolean canSelectChannel() {
        return channelId == null;
    }

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

    public Long getChannelId() {
        return this.channelId;
    }
}
