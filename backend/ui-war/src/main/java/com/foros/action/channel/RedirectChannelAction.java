package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.interceptor.NoParameters;

public class RedirectChannelAction extends BaseActionSupport implements NoParameters {

    @ReadOnly
    @Override
    public String execute() throws Exception {
        ActionContext context = ActionContext.getContext();
        Channel model = (Channel) context.getValueStack().findValue("model");
        return model.getClass().getSimpleName();
    }
}
