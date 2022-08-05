package com.foros.session.channel.service;

import com.foros.model.channel.BehavioralParametersList;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class BehavioralParamsRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("discoverChannel", "view");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("discoverChannel", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("discoverChannel", "edit");
    }

    @Restriction
    public boolean canUpdateStatus() {
        return canUpdate();
    }

    @Restriction
    public boolean canDelete(BehavioralParametersList behavioralParametersList) {
        return canUpdate();
    }

}
