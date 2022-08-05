package com.foros.session.channel.service;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.ExpressionChannelTestFactory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractChannelRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    protected BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    protected ExpressionChannelTestFactory expressionChannelTF;

    @Autowired
    protected AudienceChannelTestFactory audienceChannelTF;

    @Autowired
    protected AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @Autowired
    protected ChannelRestrictions channelRestrictions;

    protected ExpressionChannel expressionChannel;
    protected BehavioralChannel behavioralChannel;
    protected AudienceChannel audienceChannel;
}
