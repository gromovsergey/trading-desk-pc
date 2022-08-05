package com.foros.session.admin.currency;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.currency.Currency;
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
    @Permission(objectType = "currency", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "currency", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "currency", action = "edit", accountRoles = {INTERNAL})
})
public class CurrencyRestrictions {
    @EJB
    private PermissionService permissionService;
    
    @EJB
    private CurrencyService currencyService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("currency", "view");
    }
    
    @Restriction
    public boolean canView(Long currencyId ) {
        Currency currency = currencyService.findById(currencyId);
        return canView() && !isSystemCurrency(currency);
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("currency", "create");
    }
    
    @Restriction
    public boolean canCreate(Currency currency) {
        return canCreate() && !isSystemCurrency(currency);
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("currency", "edit");
    }
    
    @Restriction
    public boolean canUpdate(Currency currency) {
        return canUpdate() && !isSystemCurrency(currency);
    }

    private boolean isSystemCurrency(Currency currency) {
        return "USD".equals(currency.getCurrencyCode());
    }

}