package com.foros.session.opportunity;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.model.security.NotManagedEntity;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.restriction.EntityRestrictions;

import java.util.Arrays;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "opportunity", action = "create", accountRoles = {INTERNAL}),
        @Permission(objectType = "opportunity", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "opportunity", action = "view", accountRoles = {INTERNAL})
})
public class OpportunityRestrictions {
    private static final Collection<Probability> IO_PROBABILITIES = Arrays.asList(
            Probability.IO_SIGNED,
            Probability.AWAITING_GO_LIVE,
            Probability.LIVE
    );

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("opportunity", "view");
    }

    @Restriction
    public boolean canView(OwnedEntity ownedEntity) {
        return canView() && entityRestrictions.canAccess(ownedEntity);
    }

    @Restriction
    public boolean canViewIO() {
        return permissionService.isGranted("advertiser_entity", "view");
    }

    @Restriction
    public boolean canViewIO(Opportunity opportunity) {
        return IO_PROBABILITIES.contains(opportunity.getProbability())
                && entityRestrictions.canAccess(opportunity)
                && canViewIO();
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("opportunity", "edit");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("opportunity", "create");
    }

    @Restriction
    public boolean canCreate(OwnedStatusable parent) {
        return canCreate() && entityRestrictions.canCreate(parent, NotManagedEntity.Util.isManaged(Opportunity.class));
    }

    @Restriction
    public boolean canUpdate(OwnedEntity ownedEntity) {
        return canUpdate() && entityRestrictions.canUpdate(ownedEntity.getAccount());
    }

}
