package com.foros.action.admin.deviceChannel;

import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.Platform;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.exceptions.UnreachableExpressionException;
import com.foros.session.channel.service.PlatformService;
import com.phorm.oix.util.expression.CDMLParsingError;
import com.foros.util.expression.ExpressionHelper;

import java.io.IOException;
import java.util.Collection;
import javax.ejb.EJB;

public class SaveDeviceChannelAction extends DeviceChannelActionSupport implements Invalidable, BreadcrumbsSupport {

    @EJB
    protected PlatformService platformService;

    private String humanExpression; 

    private String save(boolean isNew) {
        prepareExpression();
        if (getParentChannelId() != null) {
            getModel().setParentChannel(deviceChannelService.findById(getParentChannelId()));
        } else {
            getModel().setParentChannel(deviceChannelService.getMobileDevicesChannel());
        }
        if (hasFieldErrors()) {
            invalid();
            return INPUT;
        }
        if (isNew) {
            deviceChannelService.create(getModel());
        } else {
            deviceChannelService.update(getModel());
        }

        if (hasFieldErrors()) {
            invalid();
            return INPUT;
        }
        return SUCCESS;
    }

    @Restrict(restriction = "DeviceChannel.update")
    public String update() {
        return save(false);
    }

    @Restrict(restriction = "DeviceChannel.create")
    public String create() {
        return save(true);
    }

    private void prepareExpression() {
        try {
            String cdml = humanExpression;
            Collection<String> platformNames;

            try {
                platformNames = ExpressionHelper.parseNames(humanExpression);
            } catch (CDMLParsingError ex) {
                throw new UnreachableExpressionException(ex.getMessage(), ex.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            for (String platformName : platformNames) {
                String humanName = "[" + platformName + "]";
                Platform platform = platformService.findByName(platformName);
                if (platform == null) {
                    addFieldError("expression", getText("errors.platformNotFound", new String[]{platformName}));
                    return;
                }
                cdml = cdml.replace(humanName, platform.getId().toString());
            }

            cdml = ExpressionHelper.replaceHumanOperations(cdml).replace(" ", "").
                    replace("\n", "").replace("\r", "").replace("\t", "");
            getModel().setExpression(cdml);
        } catch (UnreachableExpressionException e) {
            addFieldError("expression", getText("errors.wrong.cdml"));
        }
    }

    public void setHumanExpression(String humanExpression) {
        this.humanExpression = humanExpression;
    }

    public String getHumanExpression() {
        return humanExpression;
    }

    @Override
    public void invalid() {
        populatePlatforms();
        if (getModel().getId() != null) {
            parentLocations = deviceChannelService.getChannelAncestorsChain(getModel().getId(), false);
        } else {
            parentLocations = deviceChannelService.getChannelAncestorsChain(getParentChannelId(), true);
        }

    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (deviceChannel.getId() != null) {
            DeviceChannel persistent = deviceChannelService.findById(deviceChannel.getId());
            breadcrumbs = new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(new DeviceChannelBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
