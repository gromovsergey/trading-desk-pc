package com.foros.session.channel.service;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.channel.KeywordChannel;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({ @Permission(objectType = "keyword_channel", action = "view", accountRoles = { INTERNAL }),
        @Permission(objectType = "keyword_channel", action = "edit", accountRoles = { INTERNAL }) })
public class KeywordChannelRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("keyword_channel", "view");
    }

    @Restriction
    public boolean canView(KeywordChannel channel) {
        return canView() && entityRestrictions.canView(channel);
    }

    @Restriction
    public boolean canUpdate(KeywordChannel channel) {
        return permissionService.isGranted("keyword_channel", "edit") && entityRestrictions.canUpdate(channel);
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("keyword_channel", "edit");
    }
}
