package com.foros.action.channel.bulk;

import com.foros.framework.support.RequestContextsAware;
import com.foros.util.context.RequestContexts;


public class SubmitUploadChannelAction extends SubmitUploadChannelBaseAction implements RequestContextsAware {

    public String submit() {
        return doSubmit();
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(getAccount());
    }
}
