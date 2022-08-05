package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class PublisherFilter implements ZoneFilter {
    public static final String[] POLICY = {"publisher_account", "publisher_entity"};

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.PUBLISHER;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
