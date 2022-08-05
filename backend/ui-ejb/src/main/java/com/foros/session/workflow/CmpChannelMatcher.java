package com.foros.session.workflow;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.util.workflow.WorkflowScheme;

public class CmpChannelMatcher<S, A> extends BaseWorkflowMatcher<S, A> {
    public CmpChannelMatcher(WorkflowScheme<S, A> scheme) {
        super(scheme);
    }

    @Override
    public boolean isThisMatched(Object entity) {
        if (!(entity instanceof Channel)) {
            return false;
        }
        Channel channel = (Channel) entity;
        return channel.getVisibility() == ChannelVisibility.CMP;
    }
}
