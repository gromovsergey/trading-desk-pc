package com.foros.action.admin.bannedChannel;

import com.foros.framework.ReadOnly;

public class ViewNoAdvChannelAction extends BannedChannelActionSupport {

    @ReadOnly
    public String view() {
        model = service.getNoAdvBannedChannel();
        return SUCCESS;
    }

}
