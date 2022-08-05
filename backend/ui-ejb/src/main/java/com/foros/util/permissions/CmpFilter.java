package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class CmpFilter implements ZoneFilter {
    public static final String[] POLICY = { "cmp_account", "cmp_advertising_channel" };

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.CMP;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
