package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class ViewChannelAction extends BaseActionSupport implements ModelDriven<Channel>{

    @EJB
    private SearchChannelService searchChannelService;

    private Long id;
    private Channel model;

    @ReadOnly
    public String redirect() {
        model = searchChannelService.find(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Channel getModel() {
        return model;
    }
}
