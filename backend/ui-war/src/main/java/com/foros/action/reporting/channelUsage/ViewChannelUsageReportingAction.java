package com.foros.action.reporting.channelUsage;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AccountService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.reporting.channelUsage.DetailLevel;
import com.foros.session.security.AccountTO;
import com.foros.session.security.ManagerAccountTO;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.Collections;
import javax.ejb.EJB;

public class ViewChannelUsageReportingAction extends BaseActionSupport implements RequestContextsAware, CmpSelfIdAware {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private SearchChannelService channelService;

    @EJB
    private AccountRestrictions accountRestrictions;

    // parameters
    private Long accountId;
    private Long channelId;

    // model
    private ManagerAccountTO account;
    private Collection<AccountTO> accounts;
    private EntityTO channel;
    private DetailLevel detailLevel = DetailLevel.date;

    private Boolean isInternal;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'channelUsage'")
    public String view() throws Exception {
        return "success";
    }

    public Collection<AccountTO> getAccounts() {
        if (accounts != null) {
            return accounts;
        }

        if (accountId == null) {
            accounts = IndexHelper.getAccountsList(AccountRole.CMP);
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
        this.account = new ManagerAccountTO(account.getId(), account.getName(), account.getStatus().getLetter(),
                account.getRole(), account.getFlags());

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

    public DetailLevel getDetailLevel() {
        return detailLevel;
    }

    public void setDetailLevel(DetailLevel detailLevel) {
        this.detailLevel = detailLevel;
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

    @Override
    public void setCmpId(Long cmpId) {
        accountId = cmpId;
    }
}
