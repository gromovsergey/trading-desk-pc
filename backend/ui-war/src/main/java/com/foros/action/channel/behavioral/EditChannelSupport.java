package com.foros.action.channel.behavioral;

import com.foros.action.channel.ViewEditChannelActionSupport;
import com.foros.cache.application.CountryCO;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelRate;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.CountryHelper;
import com.foros.util.EntityUtils;
import com.foros.util.helper.IndexHelper;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public abstract class EditChannelSupport<T extends Channel> extends ViewEditChannelActionSupport<T> implements RequestContextsAware {
    @EJB
    protected AccountService accountService;

    @EJB
    private SearchChannelService searchChannelService;

    private BigDecimal channelRateValue;

    // dependencies
    private Collection<? extends EntityTO> channelOwners;
    private List<CountryCO> countries;

    public Collection<? extends EntityTO> getChannelOwners() {
        if (channelOwners == null && getExistingAccount().getRole() == AccountRole.INTERNAL) {
            channelOwners = accountService.getInternalAccounts(true);
            channelOwners = EntityUtils.applyStatusRules(channelOwners, null, false);
        }
        return channelOwners;
    }

    public List<CountryCO> getCountries() {
        if (countries == null && getExistingAccount().isInternational()) {
            countries = CountryHelper.sort(IndexHelper.getCountryList());
        }
        return countries;
    }

    public Channel getSupersededByChannel() {
        if (model.getSupersededByChannel() != null &&
                model.getSupersededByChannel().getId() != null && model.getSupersededByChannel().getName() == null) {
            model.setSupersededByChannel(searchChannelService.find(model.getSupersededByChannel().getId()));
        }
        return model.getSupersededByChannel();
    }

    public BigDecimal getChannelRateValue() {
        return channelRateValue;
    }

    public void setChannelRateValue(BigDecimal channelRateValue) {
        this.channelRateValue = channelRateValue;
    }

    public T prepareCmpModel() {
        if (model.getChannelRate() != null && model.getChannelRate().getRateType() != null) {
            RateType rateType = model.getChannelRate().getRateType();
            model.getChannelRate().setRate(getChannelRateValue(), rateType);
        }
        return model;
    }

    protected void populateRate() {
        setChannelRateValue(model.getChannelRate().getRate());
    }

    public List<RateType> getRateTypes() {
        return ChannelRate.getAllowedTypes();
    }

    public boolean isUsedAvailable() {
        return getExistingAccount().getRole() == AccountRole.AGENCY || getExistingAccount().getRole() == AccountRole.ADVERTISER;
    }
}
