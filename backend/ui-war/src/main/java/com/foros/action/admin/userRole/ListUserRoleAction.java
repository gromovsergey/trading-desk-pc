package com.foros.action.admin.userRole;

import com.foros.framework.ReadOnly;
import com.foros.model.security.UserRole;

import java.util.List;

public class ListUserRoleAction extends UserRoleActionSupport {

    private List<UserRole> entities;

    @ReadOnly
    public String list() {
        entities = service.findAll();
        return SUCCESS;
    }

    public List<UserRole> getEntities() {
        return entities;
    }
}
