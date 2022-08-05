package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.security.principal.SecurityContext;
import com.foros.session.channel.ChannelReportTO;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

public class ChannelsByAccountXmlAction extends AbstractOptionsByAccountAction<ChannelReportTO> {

    @EJB
    private SearchChannelService searchChannelService;

    private String accountPair;

    public ChannelsByAccountXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(SecurityContext.isInternal()));
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

    protected Collection<ChannelReportTO> getOptionsByAccount(Long accountId) {
        List<Class<? extends Channel>> channelClasses = Arrays.asList(ExpressionChannel.class, BehavioralChannel.class, DiscoverChannel.class, KeywordChannel.class);
        return searchChannelService.findChannelsByAccountTypeAndVisibility(accountId, null, channelClasses, ChannelVisibility.CMP, PAGE_SIZE);
    }
}
