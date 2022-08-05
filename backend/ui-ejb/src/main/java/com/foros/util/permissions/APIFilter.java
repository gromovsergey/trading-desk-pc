package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;
import org.apache.commons.lang.ArrayUtils;

public class APIFilter implements ZoneFilter {
    public static final String[] POLICY = {"API"};

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.API;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
