package com.foros.action.xml.options;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.DisplayStatus;
import com.foros.model.channel.Channel;
import com.foros.session.EntityTO;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.channel.service.SearchChannelService;

import javax.ejb.EJB;
import java.util.Collection;

public class ChannelsXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private SearchChannelService searchChannelService;

    private String accountPair;
    private Long displayStatusId;
    private ChannelVisibilityCriteria visibilityCriteria;
    private String countryCode;
    private String name;

    public ChannelsXmlAction() {
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

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public ChannelVisibilityCriteria getVisibilityCriteria() {
        return visibilityCriteria;
    }

    public void setVisibilityCriteria(ChannelVisibilityCriteria visibilityCriteria) {
        this.visibilityCriteria = visibilityCriteria;
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

        ChannelVisibilityCriteria criteria = visibilityCriteria == null ? ChannelVisibilityCriteria.ALL : visibilityCriteria;
        return searchChannelService.findByAccountAndStatus(accountId, displayStatus, criteria, getCountryCode(), name, AUTOCOMPLETE_SIZE);
    }
}
