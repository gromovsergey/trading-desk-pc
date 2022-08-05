package com.foros.action.admin.deviceChannel;

import com.foros.action.BaseActionSupport;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.Platform;
import com.foros.restriction.RestrictionService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.channel.DeviceChannelTO;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.channel.service.PlatformService;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.ModelDriven;
import java.util.List;
import javax.ejb.EJB;

public class DeviceChannelActionSupport extends BaseActionSupport implements ModelDriven<DeviceChannel> {
    @EJB
    protected DeviceChannelService deviceChannelService;

    @EJB
    protected PlatformService platformService;

    @EJB
    protected AccountService accountService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private SearchChannelService searchChannelService;

    protected DeviceChannel deviceChannel = new DeviceChannel();

    private Long parentChannelId;

    protected List<DeviceChannelTO> childrenChannels;
    protected List<EntityTO> parentLocations;
    private List<Platform> platforms;

    public List<DeviceChannelTO> getChildrenChannels() {
        return childrenChannels;
    }

    public List<EntityTO> getParentLocations() {
        return parentLocations;
    }

    @Override
    public DeviceChannel getModel() {
        return deviceChannel;
    }

    protected void populatePlatforms() {
        platforms = platformService.findAll();
    }

    public Long getParentChannelId() {
        return parentChannelId;
    }

    public void setParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }
}
