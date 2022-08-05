package com.foros.action.admin.userRole;

import com.foros.action.admin.permissions.PolicyEntryInfo;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.annotation.Restrict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavePermissionsAction extends PermissionsActionSupport implements BreadcrumbsSupport {

    private List<PolicyEntryInfo> checks = new ArrayList<PolicyEntryInfo>();

    @Restrict(restriction="UserRole.update")
    public String save() {
        UserRole userRole = userRoleService.view(getUserRole().getId());
        userRole.setVersion(getUserRole().getVersion());

        Set<PolicyEntry> newEntries = calculateNewEntries(userRole);
        userRole.setPolicyEntries(newEntries);

        userRoleService.updatePolicy(userRole);

        return SUCCESS;
    }

    private <T> T find(Collection<T> collection, T value) {
        for (T t : collection) {
            if (t.equals(value)) {
                return t;
            }
        }

        return null;
    }

    private PolicyEntry createPolicyEntry(PolicyEntryInfo info) {
        PolicyEntry policyEntry = new PolicyEntry();
        policyEntry.setId(info.getEntry());
        policyEntry.setType(info.getObjectType());
        policyEntry.setAction(info.getAction());
        policyEntry.setParameter(info.getParameter());
        return policyEntry;
    }

    private Set<PolicyEntry> calculateNewEntries(UserRole userRole) {
        Set<PolicyEntry> entries = new HashSet<PolicyEntry>(userRole.getPolicyEntries());

        for (PolicyEntryInfo info : checks) {
            if (info.getEntry() != null && !info.hasValue()) {
                PolicyEntry policyEntry = find(entries, createPolicyEntry(info));
                entries.remove(policyEntry);
            } else if (info.getEntry() == null && info.hasValue()) {
                PolicyEntry policyEntry = createPolicyEntry(info);
                policyEntry.setUserRole(userRole);
                entries.add(policyEntry);
            }
        }

        return entries;
    }

    public List<PolicyEntryInfo> getChecks() {
        return checks;
    }

    public void setChecks(List<PolicyEntryInfo> checks) {
        this.checks = checks;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement(entity)).add(new PermissionsBreadcrumbsElement());
    }
}
