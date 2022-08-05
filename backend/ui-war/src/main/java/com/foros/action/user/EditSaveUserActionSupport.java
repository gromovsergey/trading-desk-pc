package com.foros.action.user;

import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.GenericAccount;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.security.AuthenticationType;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.security.LdapService;
import com.foros.session.security.UserRestrictions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

public abstract class EditSaveUserActionSupport extends UserActionSupport implements BreadcrumbsSupport {
    @EJB
    private UserRoleService userRoleService;

    @EJB
    private LdapService ldapService;

    @EJB
    private UserRestrictions userRestrictions;

    private Boolean roleChangeAllowed;

    private Boolean fieldsChangeAllowed;

    private List<UserRole> roles;

    public EditSaveUserActionSupport() {
        user.setAccount(new GenericAccount());
        user.unregisterChange("account");
    }

    public boolean isRoleChangeAllowed() {
        if (roleChangeAllowed == null) {
            roleChangeAllowed = user.getId() == null || userService.isRoleChangeAllowed(getModel().getId());
        }
        return roleChangeAllowed;
    }

    public List<UserRole> getRoles() {
        if (roles == null) {
            roles = userRoleService.find(user.getAccount().getRole());
        }
        return roles;
    }

    public Collection getAvailableDNs() {
        if (user.getRole() != null && user.getAuthType() == AuthenticationType.LDAP) {
            UserRole role = userRoleService.findById(user.getRole().getId());
            return ldapService.findDnsForRole(role);
        } else return Collections.emptyList();
    }

    public boolean isFieldsChangeAllowed() {
        if (fieldsChangeAllowed == null) {
            fieldsChangeAllowed = user.getId() == null || userRestrictions.canUpdate(userService.find(user.getId()));
        }
        return fieldsChangeAllowed;
    }

    public User getExistingUser() {
        Long id = getModel().getId();
        return id != null ? userService.view(id) : null;
    }
}
