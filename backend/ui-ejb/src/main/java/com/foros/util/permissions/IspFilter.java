package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class IspFilter implements ZoneFilter {
    public static final String[] POLICY = { "isp_account", "colocation" };

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.ISP;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
