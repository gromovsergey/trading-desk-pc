package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.DisplayStatus;
import com.foros.model.channel.Channel;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import java.util.Collection;
import javax.ejb.EJB;

public class DiscoverChannelsXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private SearchChannelService searchChannelService;

    private String accountPair;
    private Long discoverChannelListId;
    private Long displayStatusId;
    private String countryCode;
    private String name;

    public DiscoverChannelsXmlAction() {
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

    public Long getDiscoverChannelListId() {
        return discoverChannelListId;
    }

    public void setDiscoverChannelListId(Long discoverChannelListId) {
        this.discoverChannelListId = discoverChannelListId;
    }

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        DisplayStatus displayStatus = getDisplayStatusId() != null && getDisplayStatusId() > 0 ?
                Channel.getDisplayStatus(getDisplayStatusId()) : null;

        return searchChannelService.findDiscoverByAccountAndStatus(accountId, discoverChannelListId, displayStatus, countryCode, name, AUTOCOMPLETE_SIZE);
    }
}
