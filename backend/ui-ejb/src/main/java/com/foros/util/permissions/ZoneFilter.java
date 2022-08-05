package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

public interface ZoneFilter {
    PermissionsZone getZone();

    boolean accept(PermissionDescriptor descriptor);
}
