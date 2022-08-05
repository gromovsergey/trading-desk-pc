package com.foros.session.restriction;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.session.RestrictionTestService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Restrictions
@Stateless
@Permissions({
        @Permission(objectType = "test", action = "testAction", accountRoles = {AccountRole.INTERNAL}),
        @Permission(objectType = "test", action = "testParameterizedAction", parameterized = true,  accountRoles = {AccountRole.INTERNAL})
})
public class TestEntityRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private RestrictionTestService testService;

    @Restriction
    public boolean canTest(Long id) {
        return permissionService.isGranted("test", "testAction") && testService.check1(id);
    }

    @Restriction
    public boolean canTestParameterized(Long id) {
        return permissionService.isGranted("test", "testParameterizedAction", id.toString());
    }
}
