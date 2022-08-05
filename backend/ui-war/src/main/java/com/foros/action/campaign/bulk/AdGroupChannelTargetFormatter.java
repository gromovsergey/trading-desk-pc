package com.foros.action.campaign.bulk;

import com.foros.model.campaign.ChannelTarget;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelExpressionLink;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.util.expression.ExpressionHelper;

public class AdGroupChannelTargetFormatter extends ValueFormatterSupport<Object> {
    @Override
    public String formatText(Object value, FormatterContext context) {
        if (value instanceof ChannelTarget) {
            return ((ChannelTarget) value).name();
        } else if (value instanceof ChannelExpressionLink){
            return ((ChannelExpressionLink) value).getExpression();
        } else if (value instanceof Channel){
            Channel channel = (Channel) value;
            return ExpressionHelper.getEditableHumanName(channel);
        } else {
            throw new IllegalArgumentException("value" + value);
        }
    }
}