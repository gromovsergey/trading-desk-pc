package com.foros.action.channel.expression;

import com.foros.action.channel.MakePublicChannelActionSupport;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.channel.service.AdvertisingChannelSupport;
import com.foros.session.channel.service.ExpressionChannelService;

import javax.ejb.EJB;

public class MakePublicExpressionChannelAction extends MakePublicChannelActionSupport<ExpressionChannel> {

    @EJB
    private ExpressionChannelService expressionChannelService;

    public MakePublicExpressionChannelAction() {
        super(new ExpressionChannel());
    }

    @Override
    protected AdvertisingChannelSupport<ExpressionChannel> channelService() {
        return expressionChannelService;
    }
}
