package com.foros.action.channel.bulk;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.context.RequestContexts;


public class MainUploadChannelAction extends UploadChannelActionSupport implements RequestContextsAware {

    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.upload", parameters = "find('Account', #target.advertiserId)")
    public String main() {
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(getAccount());
    }
}
