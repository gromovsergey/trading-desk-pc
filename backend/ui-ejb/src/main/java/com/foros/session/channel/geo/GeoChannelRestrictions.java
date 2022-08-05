package com.foros.session.channel.geo;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.CurrentUserService;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "geoChannel", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "geoChannel", action = "undelete", accountRoles = {INTERNAL})
})
public class GeoChannelRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private GeoChannelService geoChannelService;

    @Restriction
    public boolean canList() {
        return currentUserService.isInternal();
    }

    @Restriction
    public boolean canViewLog() {
        return currentUserService.isInternal();
    }

    @Restriction
    public boolean canView() {
        return currentUserService.isInternal();
    }

    @Restriction
    public boolean canView(Long channelId) {
        return geoChannelService.find(channelId).getGeoType() != GeoType.ADDRESS;
    }

    @Restriction
    public boolean canCreateAddress() {
        return advertisingChannelRestrictions.canView(currentUserService.getAccountRole());
    }

    @Restriction
    public boolean canUpdate(GeoChannel channel) {
        return channel.getGeoType() == GeoType.CITY &&
                permissionService.isGranted("geoChannel", "edit") &&
                entityRestrictions.canDelete(channel);
    }

    @Restriction
    public boolean canUndelete(GeoChannel channel) {
        return channel.getGeoType() == GeoType.CITY &&
                permissionService.isGranted("geoChannel", "undelete") &&
                entityRestrictions.canUndelete(channel);
    }
}
