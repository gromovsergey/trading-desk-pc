package com.foros.action.channel.expression;

import com.foros.action.channel.StatusChannelActionSupport;
import com.foros.session.channel.service.ChannelService;
import com.foros.session.channel.service.ExpressionChannelService;

import javax.ejb.EJB;

public class StatusExpressionChannelAction extends StatusChannelActionSupport {

    @EJB
    private ExpressionChannelService expressionChannelService;

    @Override
    protected ChannelService channelService() {
        return expressionChannelService;
    }
}
