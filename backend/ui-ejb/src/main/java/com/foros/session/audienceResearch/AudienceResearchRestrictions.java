package com.foros.session.audienceResearch;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.validation.ValidationContext;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import static com.foros.security.AccountRole.*;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "audience_research", action = "view", accountRoles = {INTERNAL, AGENCY, ADVERTISER}),
        @Permission(objectType = "audience_research", action = "edit", accountRoles = {INTERNAL})
})
public class AudienceResearchRestrictions {
    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public void canView(ValidationContext context) {
        validatePermission(context, "view");
    }

    @Restriction
    public void canEdit(ValidationContext context) {
        validatePermission(context, "edit");
    }

    private void validatePermission(ValidationContext context, String action) {
        entityRestrictions.validatePermission(context, "audience_research", action);
    }
}
