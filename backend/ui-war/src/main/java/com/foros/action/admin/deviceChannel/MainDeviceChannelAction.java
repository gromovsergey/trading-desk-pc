package com.foros.action.admin.deviceChannel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.DeviceChannelTO;
import com.foros.session.channel.service.DeviceChannelService;

import java.util.List;

import javax.ejb.EJB;

public class MainDeviceChannelAction extends BaseActionSupport {
    @EJB
    private DeviceChannelService deviceChannelService;

    private List<DeviceChannelTO> childrenChannels;

    public List<DeviceChannelTO> getChildrenChannels() {
        return childrenChannels;
    }

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.view")
    public String main() {
        childrenChannels = deviceChannelService.getChannelList(null);
        return SUCCESS;
    }
}