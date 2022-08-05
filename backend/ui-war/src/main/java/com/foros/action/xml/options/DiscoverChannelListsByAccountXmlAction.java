package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import java.util.Collection;
import javax.ejb.EJB;

public class DiscoverChannelListsByAccountXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private SearchChannelService searchChannelService;

    private String accountPair;
    private String countryCode;

    public DiscoverChannelListsByAccountXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @AccountId
    @RequiredStringValidator(key = "errors.required", message = "value.accountPair")
    @CustomValidator(type = "pair", key = "errors.pair", message = "value.accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return searchChannelService.findDiscoverListsByAccount(accountId, countryCode);
    }
}
