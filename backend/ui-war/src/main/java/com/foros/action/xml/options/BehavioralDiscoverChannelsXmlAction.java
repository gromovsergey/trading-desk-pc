package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import java.util.Collection;
import javax.ejb.EJB;

public class BehavioralDiscoverChannelsXmlAction extends AbstractOptionsByAccountAction<EntityTO> {

    @EJB
    private SearchChannelService searchChannelService;

    private String accountPair;
    private String query;


    public BehavioralDiscoverChannelsXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(SecurityContext.isInternal()));
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

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return searchChannelService.findBehavioralDiscoverChannels(accountId, query, getFirstResult(), AUTOCOMPLETE_SIZE);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
