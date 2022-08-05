package com.foros.action.reporting.channel;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.GeoChannel;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AccountService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.security.AccountTO;
import com.foros.util.context.RequestContexts;

import java.util.List;
import javax.ejb.EJB;


public class ViewChannelReportingAction extends BaseActionSupport implements RequestContextsAware,
        AdvertiserSelfIdAware, AgencySelfIdAware, CmpSelfIdAware {

    private Long accountId;
    private Long channelId;

    private Channel channel;

    private List<AccountTO> accounts;

    @EJB
    private AccountService accountService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private RestrictionService restrictionService;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'channel'")
    public String view() throws Exception {
        if (!canSelectChannel()) {
            this.channel = searchChannelService.find(channelId);
            if (!restrictionService.isPermitted("Report.Channel.run", channel)) {
                throw new SecurityException("Invalid channel: " + channel.getId());
            }

            if (channel.getAccount() != null) {
                accountId = channel.getAccount().getId();
            }
        }
        return "success";
    }

    public List<AccountTO> getAccounts() {
        if (accounts == null) {
            accounts = IndexHelper.getAccountsList(AccountRole.INTERNAL, AccountRole.ADVERTISER, AccountRole.AGENCY,
                    AccountRole.CMP);
            if (accounts != null && !accounts.isEmpty()) {
                setAccountId(accounts.get(0).getId());
            }
        }
        return accounts;
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

    public Channel getChannel() {
        return channel;
    }

    public boolean canSelectAccount() {
        return accountId == null && isInternal() && (channel == null || !(channel instanceof GeoChannel));
    }

    public boolean canSelectChannel() {
        return channelId == null;
    }

    public boolean isInternal() {
        return currentUserService.isInternal();
    }

    @Override
    public void setCmpId(Long cmpId) {
        accountId = cmpId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        accountId = advertiserId;
    }

    @Override
    public void setAgencyId(Long agencyId) {
        accountId = agencyId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountService.getAccountName(getAccountId(), true);
    }

}
