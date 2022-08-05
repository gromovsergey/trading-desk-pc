package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

public class AdvertisingCmpChannelsByAccountXmlAction extends AbstractOptionsAction<EntityTO> {

    @EJB
    private SearchChannelService searchChannelService;

    private Long accountId;

    public AdvertisingCmpChannelsByAccountXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @RequiredStringValidator(key = "errors.required", message = "value.accountId")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        List<Class<? extends Channel>> channelClasses = Arrays.asList(ExpressionChannel.class, BehavioralChannel.class);
        return searchChannelService.findChannelsByAccountTypeAndVisibility(accountId, null, channelClasses, ChannelVisibility.CMP, -1);
    }
}
