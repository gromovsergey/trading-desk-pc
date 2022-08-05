package com.foros.session.channel.service;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "channel_match_test", action = "run", accountRoles = {INTERNAL})
})
public class ChannelMatchTestRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canRun() {
        boolean canViewChannels = permissionService.isGranted("discoverChannel", "view")
                || permissionService.isGranted("advertiser_advertising_channel", "view")
                || permissionService.isGranted("cmp_advertising_channel", "view")
                || permissionService.isGranted("internal_advertising_channel", "view");

        return permissionService.isGranted("channel_match_test", "run") && canViewChannels;
    }
}
