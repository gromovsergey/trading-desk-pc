package com.foros.restriction.registry;

import com.foros.security.AccountRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionDescriptorFinder {

    private Map<String, Map<String, PermissionDescriptor>> permissions;
    private Set<PermissionDescriptor> forFind = new HashSet<PermissionDescriptor>();
    private Set<PermissionDescriptor> except = new HashSet<PermissionDescriptor>();

    private boolean all = false;

    public PermissionDescriptorFinder(Map<String, Map<String, PermissionDescriptor>> permissions) {
        this.permissions = permissions;
    }

    public PermissionDescriptorFinder all() {
        all = true;
        return this;
    }

    public PermissionDescriptorFinder permission(String object, String operation) {
        forFind.add(new PermissionDescriptor(object, operation, false, new AccountRole[0]));
        return this;
    }

    public PermissionDescriptorFinder permissions(String object) {
        forFind.add(new PermissionDescriptor(object, null, false, new AccountRole[0]));
        return this;
    }

    public PermissionDescriptorFinder except(Set<PermissionDescriptor> permissionDescriptors) {
        except.addAll(permissionDescriptors);
        return this;
    }

    public Set<PermissionDescriptor> find() {
        HashSet<PermissionDescriptor> result = new HashSet<PermissionDescriptor>();

        if (all) {
            return findAll();
        }

        for (PermissionDescriptor info : forFind) {
            if (except.contains(info)) {
                continue;
            }

            Map<String, PermissionDescriptor> operationToDescriptorMap = permissions.get(info.getObjectType());
            if (operationToDescriptorMap != null) {
                if (info.getActionName() != null) {
                    PermissionDescriptor descriptor = operationToDescriptorMap.get(info.getActionName());
                    if (descriptor != null) {
                        result.add(descriptor);
                    } else {
                        throw new IllegalArgumentException("Can't find permission with this operation: " + info);
                    }
                } else {
                    result.addAll(operationToDescriptorMap.values());
                }
            } else {
                throw new IllegalArgumentException("Can't find permission with this object: " + info);
            }
        }

        return result;
    }

    private Set<PermissionDescriptor> findAll() {
        HashSet<PermissionDescriptor> result = new HashSet<PermissionDescriptor>();
        for (Map<String, PermissionDescriptor> values : permissions.values()) {
            for (PermissionDescriptor descriptor : values.values()) {
                if (!except.contains(descriptor)) {
                    result.add(descriptor);
                }
            }
        }

        return result;
    }

    public PermissionDescriptorFinder clone() {
        return new PermissionDescriptorFinder(permissions);
    }

}
