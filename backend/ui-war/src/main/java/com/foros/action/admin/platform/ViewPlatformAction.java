package com.foros.action.admin.platform;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.Platform;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.PlatformService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class ViewPlatformAction extends BaseActionSupport implements ModelDriven<Platform>, BreadcrumbsSupport {

    @EJB
    protected PlatformService platformService;
    private Platform platform = new Platform();

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.view")
    public String view() {
        Long platformId = getModel().getId();
        platform = platformService.findById(platformId);
        return SUCCESS;
    }

    @Override
    public Platform getModel() {
        return platform;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new PlatformBreadcrumbsElement(platform));
    }
}
