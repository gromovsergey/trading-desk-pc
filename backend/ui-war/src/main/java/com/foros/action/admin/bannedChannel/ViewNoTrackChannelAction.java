package com.foros.action.admin.bannedChannel;

import com.foros.framework.ReadOnly;

public class ViewNoTrackChannelAction extends BannedChannelActionSupport {

    @ReadOnly
    public String view() {
        model = service.getNoTrackBannedChannel();
        return SUCCESS;
    }

}
