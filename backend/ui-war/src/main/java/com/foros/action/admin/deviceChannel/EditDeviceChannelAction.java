package com.foros.action.admin.deviceChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Status;
import com.foros.model.channel.Platform;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.PlatformService;
import com.foros.util.expression.ExpressionHelper;

import java.util.Collection;

import javax.ejb.EJB;

public class EditDeviceChannelAction extends DeviceChannelActionSupport implements BreadcrumbsSupport {

    @EJB
    protected PlatformService platformService;

    private Breadcrumbs breadcrumbs;

    public void prepare() {
        if (getParentChannelId() == null) {
            setParentChannelId(deviceChannelService.getMobileDevicesChannel().getId());
        }

        Long id = getModel().getId();
        if (id == null) {
            getModel().setStatus(Status.ACTIVE);
            parentLocations = deviceChannelService.getChannelAncestorsChain(getParentChannelId(), true);
        } else {
            deviceChannel = deviceChannelService.view(id);
            parentLocations = deviceChannelService.getChannelAncestorsChain(id, false);
        }
        populatePlatforms();
    }

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.update")
    public String edit() {
        prepare();
        breadcrumbs = new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(new DeviceChannelBreadcrumbsElement(deviceChannel)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.create")
    public String create() {
        prepare();
        breadcrumbs = new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    public String getHumanExpression() {
        String cdml = getModel().getExpression();
        String result = "";
        if (cdml != null && cdml.length() > 0) {
            result = ExpressionHelper.replaceCDMLOperations(cdml);
            Collection<Platform> platforms = platformService.findByExpression(cdml);
            for (Platform platform : platforms) {
                result = result.replace(platform.getId().toString(), "[" + platform.getName() + "]");
            }
        }

        return result;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
