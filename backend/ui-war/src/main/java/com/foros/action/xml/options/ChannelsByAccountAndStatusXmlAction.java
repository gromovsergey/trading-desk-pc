package com.foros.action.xml.options;

import com.foros.action.admin.channel.ChannelSearchStatus;
import com.foros.util.helper.IndexHelper;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import java.util.Collection;

public class ChannelsByAccountAndStatusXmlAction extends AbstractOptionsByAccountAction<EntityTO> {

    private String accountPair;
    private ChannelSearchStatus searchStatus;

    public ChannelsByAccountAndStatusXmlAction() {
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

    @RequiredStringValidator(key = "errors.required", message = "searchStatus")
    public ChannelSearchStatus getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(ChannelSearchStatus searchStatus) {
        this.searchStatus = searchStatus;
    }

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return IndexHelper.getAdvertisingChannelsByAccountAndStatuses(accountId, searchStatus.getDisplayStatuses());
    }
}
