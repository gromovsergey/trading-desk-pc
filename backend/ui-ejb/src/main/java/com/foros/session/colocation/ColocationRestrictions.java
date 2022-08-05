package com.foros.session.colocation;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.model.security.NotManagedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.UtilityService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "colocation", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "colocation", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "colocation", action = "edit", accountRoles = {INTERNAL}),
    @Permission(objectType = "colocation", action = "undelete", accountRoles = {INTERNAL})
})
public class ColocationRestrictions  {

    private static final boolean IS_MANAGED = NotManagedEntity.Util.isManaged(Colocation.class);

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private UtilityService utilityService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("colocation", "view");
    }

    @Restriction
    public boolean canList(Long accountId) {
        IspAccount account = utilityService.find(IspAccount.class, accountId);
        return entityRestrictions.canView(account);
    }

    @Restriction
    public boolean canView(Long accountId) {
        IspAccount account = utilityService.find(IspAccount.class, accountId);
        return canViewImpl(account);
    }

    @Restriction
    public boolean canView(IspAccount account) {
        return canViewImpl(account);
    }

    @Restriction
    public boolean canView(Colocation colocation) {
        return canViewImpl(colocation);
    }

    private boolean canViewImpl(OwnedStatusable<?> entity) {
        return permissionService.isGranted("colocation", "view") && entityRestrictions.canView(entity);
    }

    @Restriction
    public boolean canUpdate(Colocation colocation) {
        return permissionService.isGranted("colocation", "edit") && entityRestrictions.canUpdate(colocation);
    }

    @Restriction
    public boolean canDelete(Colocation colocation) {
        return permissionService.isGranted("colocation", "edit") && entityRestrictions.canDelete(colocation);
    }

    @Restriction
    public boolean canUndelete(Colocation colocation) {
        return permissionService.isGranted("colocation", "undelete") && entityRestrictions.canUndelete(colocation);
    }

    @Restriction
    public boolean canCreate(IspAccount account) {
        return permissionService.isGranted("colocation", "create") && entityRestrictions.canCreate(account, IS_MANAGED);
    }

}
