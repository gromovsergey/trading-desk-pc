package com.foros.action.admin.discoverChannel;

import com.foros.action.channel.ViewEditChannelActionSupport;
import com.foros.cache.application.CountryCO;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.service.DiscoverChannelListService;
import com.foros.util.EntityUtils;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

public abstract class AbstractDiscoverChannelActionSupport<T> extends ViewEditChannelActionSupport<T> {
    @EJB
    protected BehavioralParamsListService behavioralParamsListService;
    @EJB
    protected DiscoverChannelListService discoverChannelListService;
    @EJB
    protected CountryService countryService;
    @EJB
    protected AccountService accountService;

    private Long accountId;
    private String accountName;
    private Collection<EntityTO> channelOwners;
    private List<CountryCO> countries;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Collection<EntityTO> getChannelOwners() {
        return channelOwners;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryCO> countries) {
        this.countries = countries;
    }

    protected void initChannelOwners() {
        channelOwners = accountService.getInternalAccounts(true);
        EntityUtils.applyStatusRules(channelOwners, null, false);
    }
}
