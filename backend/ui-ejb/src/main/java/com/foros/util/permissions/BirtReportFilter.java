package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

public class BirtReportFilter implements ZoneFilter {
    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.BIRT_REPORTS;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return descriptor.getObjectType().equals("birt_report");
    }
}
