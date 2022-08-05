package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

public class AudienceResearchFilter implements ZoneFilter {
    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.AUDIENCE_RESEARCH;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return descriptor.getObjectType().equals("audience_research");
    }
}