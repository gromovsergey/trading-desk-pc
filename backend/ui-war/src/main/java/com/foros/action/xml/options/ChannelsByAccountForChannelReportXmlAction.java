package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.ChannelReportTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.security.principal.SecurityContext;
import com.foros.session.channel.ChannelReportTO;
import com.foros.session.channel.service.SearchChannelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class ChannelsByAccountForChannelReportXmlAction extends AbstractOptionsByAccountAction<ChannelReportTO> {

    @EJB
    private SearchChannelService searchChannelService;

    private String accountId;
    private String query;

    public ChannelsByAccountForChannelReportXmlAction() {
        super(new ChannelReportTOConverter(true), new OptionStatusFilter(SecurityContext.isInternal()));
    }

    @AccountId
    @RequiredStringValidator(key = "errors.required", message = "value.accountId")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected Collection<ChannelReportTO> getOptionsByAccount(Long accountId) {
        List<Class<? extends Channel>> channelClasses = Arrays.asList(
                ExpressionChannel.class,
                AudienceChannel.class,
                BehavioralChannel.class,
                DiscoverChannel.class,
                KeywordChannel.class
        );
        return searchChannelService.findChannelsByAccountAndType(accountId, query, channelClasses, AUTOCOMPLETE_SIZE);
    }

    @Override
    protected boolean hideException(ProcessException e) {
        return accountId == null;
    }
}
