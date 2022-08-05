package com.foros.session.security;

import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.AGENCY;
import static com.foros.security.AccountRole.INTERNAL;
import static com.foros.security.AccountRole.PUBLISHER;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "API", action = "run", accountRoles = {INTERNAL, ADVERTISER, AGENCY, PUBLISHER})
})
public class APIRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private UserService userService;

    @Restriction
    public boolean canRun() {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        return permissionService.isGranted("API", "run") &&
                !userService.isMultiUserCredentials(principal.getUserCredentialId());
    }
}
