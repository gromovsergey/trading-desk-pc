package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

public class PredefinedReportFilter implements ZoneFilter {
    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.PREDEFINED_REPORTS;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return descriptor.getObjectType().startsWith("predefined_report");
    }
}
