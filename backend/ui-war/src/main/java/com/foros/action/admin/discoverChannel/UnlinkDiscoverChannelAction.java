package com.foros.action.admin.discoverChannel;

import com.foros.action.BaseActionSupport;
import com.foros.session.channel.service.DiscoverChannelListService;

import javax.ejb.EJB;

public class UnlinkDiscoverChannelAction extends BaseActionSupport {

    @EJB
    private DiscoverChannelListService discoverChannelListService;

    // param
    private Long id;

    public String unlink() {
        discoverChannelListService.unlink(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
