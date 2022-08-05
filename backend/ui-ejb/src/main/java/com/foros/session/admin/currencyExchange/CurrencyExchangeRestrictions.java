package com.foros.session.admin.currencyExchange;

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
    @Permission(objectType = "currencyExchange", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "currencyExchange", action = "edit", accountRoles = {INTERNAL})
})
public class CurrencyExchangeRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("currencyExchange", "view");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("currencyExchange", "edit");
    }

}