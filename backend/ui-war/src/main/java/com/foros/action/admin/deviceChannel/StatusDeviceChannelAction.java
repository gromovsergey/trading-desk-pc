package com.foros.action.admin.deviceChannel;

import com.foros.action.BaseActionSupport;
import com.foros.session.BusinessException;
import com.foros.session.channel.service.DeviceChannelService;

import javax.ejb.EJB;

public class StatusDeviceChannelAction extends BaseActionSupport {
    @EJB
    private DeviceChannelService deviceChannelService;

    // parameters
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String activate()  {
        deviceChannelService.activate(getId());
        return SUCCESS;
    }

    public String inactivate()  {
        try {
            deviceChannelService.inactivate(getId());
        } catch (BusinessException e) {
            addActionError(e.getMessage());
        }
        if (hasFieldErrors()) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String delete() {
        try {
            deviceChannelService.delete(id);
        } catch (BusinessException e) {
            addActionError(e.getMessage());
        }
        if (hasFieldErrors()) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String undelete() {
        deviceChannelService.undelete(id);
        return SUCCESS;
    }

}
