package com.foros.action.reporting.channelInventoryForecast;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.security.AccountTO;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.context.RequestContexts;
import com.foros.util.helper.IndexHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

public class ViewChannelInventoryForecastReportAction
        extends BaseActionSupport implements RequestContextsAware, AgencySelfIdAware, AdvertiserSelfIdAware {

    @EJB
    private AccountService accountService;

    @EJB
    private AccountTypeService accountTypeService;

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    protected RestrictionService restrictionService;

    @EJB
    private CreativeSizeService creativeSizeService;

    private Collection<? extends AccountTO> accounts;

    private List<EntityTO> sizes;

    private Long channelId;

    private Long accountId;

    private Long contextAccountId;

    private Account account;

    private Channel channel;

    private String targetCurrencyCode;

    @ReadOnly
    @Restrict(restriction = "Report.ChannelInventory.view", parameters = { "#target.account", "#target.channel" })
    public String view() {
        if (contextAccountId != null) {
            Account contextAccount = accountService.find(contextAccountId);
            targetCurrencyCode = contextAccount.getCurrency().getCurrencyCode();
        }
        return SUCCESS;
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

    public List<EntityTO> getSizes() {
        if (sizes == null) {
            sizes = new LinkedList<EntityTO>();
            if (isInternal()) {
                CreativeSizeService creativeSizeSvc = ServiceLocator.getInstance().lookup(CreativeSizeService.class);
                sizes = creativeSizeSvc.getIndex();
            } else {
                Account account = accountService.getMyAccount();
                sizes = accountTypeService.findCreativeSizes(account.getAccountType().getId());
            }

            for (EntityTO size : sizes) {
                size.setName(LocalizableNameUtil.getLocalizedValue(size));
            }

            Collections.sort(sizes, new StatusNameTOComparator());
        }
        return sizes;
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


    public Account getAccount() {
        if (account == null) {
            if (getChannel() == null) {
                account = findAccount();
            } else {
                account = getChannel().getAccount();
            }
            // keep account & accountId in sync
            if (account != null) {
                accountId = account.getId();
            }
        }
        return account;
    }

    public Channel getChannel() {
        if (channel == null) {
            channel = findChannel();
        }
        return channel;
    }

    @Override
    public void setAgencyId(Long agencyId) {
        setAccountId(agencyId);
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        setAccountId(advertiserId);
    }

    public void setContextAccountId(Long contextAccountId) {
        this.contextAccountId = contextAccountId;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (contextAccountId != null) {
            contexts.switchTo(accountService.find(contextAccountId));
        } else if (account != null) {
            contexts.switchTo(account);
        }
    }

    private Channel findChannel() {
        if (channelId == null) {
            return null;
        }
        return searchChannelService.find(channelId);
    }

    private Account findAccount() {
        if (accountId == null) {
            return null;
        }
        return accountService.find(accountId);
    }
}
