package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

public class AgentReportFilter implements ZoneFilter {
    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.AGENT_REPORT;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return descriptor.getObjectType().equals("agent_report");
    }
}