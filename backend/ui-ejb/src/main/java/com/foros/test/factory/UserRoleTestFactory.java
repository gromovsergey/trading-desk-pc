package com.foros.test.factory;

import com.foros.model.security.InternalAccessType;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.security.AccountRole;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class UserRoleTestFactory extends TestFactory<UserRole> {
    @EJB
    private UserRoleService userRoleService;

    public void populate(UserRole userRole) {
        if (AccountRole.INTERNAL == userRole.getAccountRole()) {
            userRole.setInternalAccessType(InternalAccessType.ALL_ACCOUNTS);
        }
        userRole.setName(getTestEntityRandomName());
        userRole.setPolicyEntries(new HashSet<PolicyEntry>());
    }

    @Override
    public UserRole create() {
        return create(AccountRole.INTERNAL);
    }

    public UserRole create(AccountRole accountRole) {
        UserRole userRole = new UserRole();
        userRole.setAccountRole(accountRole);
        populate(userRole);
        return userRole;
    }

    @Override
    public void persist(UserRole userRole) {
        userRoleService.create(userRole);
    }

    public void update(UserRole userRole) {
        userRoleService.update(userRole);
    }

    @Override
    public UserRole createPersistent() {
        UserRole userRole = create();
        persist(userRole);
        return userRole;
    }

    public UserRole createPersistent(AccountRole accountRole) {
        UserRole userRole = create(accountRole);
        persist(userRole);
        return userRole;
    }
}
