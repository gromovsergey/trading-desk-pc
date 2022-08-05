package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import javax.ejb.EJB;

public class ListExternalChannelAction
        extends BaseActionSupport
        implements CmpSelfIdAware, AgencySelfIdAware, AdvertiserSelfIdAware, RequestContextsAware {

    @EJB
    private AccountService accountService;

    @EJB
    private SearchChannelService channelService;

    private Long accountId;
    private Long advertiserId;
    private Account account;
    private Collection<ChannelTO> channels;

    @ReadOnly
    public String list() {
        channels  = channelService.findChannelsForAccount(accountId);
        return SUCCESS;
    }

    public Account getAccount() {
        if (account == null) {
            account = accountService.find(accountId);
        }
        return account;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Collection<ChannelTO> getChannels() {
        return channels;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
        this.accountId = advertiserId;
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
        if (advertiserId != null) {
            contexts.getAdvertiserContext().switchTo(advertiserId);
        } else {
            contexts.switchTo(getAccount());
        }
    }
}
