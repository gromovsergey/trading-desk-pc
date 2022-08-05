package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class InternalFilter implements ZoneFilter {
    public static final String[] POLICY = { "internal_account", "auctionSettings", "internal_advertising_channel", "placementsBlacklist" };

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.INTERNAL;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
