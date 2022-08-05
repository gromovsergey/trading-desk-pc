package com.foros.action.channel.behavioral;

import com.foros.action.BaseActionSupport;
import com.foros.session.channel.service.BehavioralChannelService;

import javax.ejb.EJB;

public class CreateCopyBehavioralChannelAction extends BaseActionSupport {

    @EJB
    private BehavioralChannelService behavioralChannelService;

    // param
    private Long id;

    private Long copyId;

    public String copy() {
        copyId = behavioralChannelService.copy(this.id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCopyId() {
        return copyId;
    }
}
