package com.foros.action.admin.userRole;

import com.foros.action.BaseActionSupport;
import com.foros.model.security.UserRole;
import com.foros.session.admin.userRole.UserRoleService;

import javax.ejb.EJB;

public abstract class PermissionsActionSupport extends BaseActionSupport {

    @EJB
    protected UserRoleService userRoleService;

    protected UserRole entity = new UserRole();

    public PermissionsActionSupport() {
        super();
    }

    public UserRole getUserRole() {
        return entity;
    }

    public void setUserRole(UserRole userRole) {
        this.entity = userRole;
    }

    public UserRole getEntity() {
        return entity;
    }
}
