package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.security.principal.SecurityContext;
import com.foros.session.channel.service.ChannelService;

public abstract class StatusChannelActionSupport extends BaseActionSupport {
    // param
    private Long id;

    public String delete() {
        channelService().delete(id);
        if (SecurityContext.isInternal()) {
            return "internal";
        } else {
            return "external";
        }
    }

    public String undelete() {
        channelService().undelete(id);
        return SUCCESS;
    }

    public String activate() {
        channelService().activate(id);
        return SUCCESS;
    }

    public String inactivate() {
        channelService().inactivate(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected abstract ChannelService channelService();
}
