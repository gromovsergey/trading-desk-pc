package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class AdvertiserFilter implements ZoneFilter {
    private static final String[] POLICY = {
            "advertising_account",
            "agency_advertiser_account",
            "opportunity",
            "campaignAllocation",
            "campaignCredit",
            "advertiser_entity",
            "advertiser_advertising_channel"
    };

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.ADVERTISER;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }

    public static String[] getPolicy() {
        String[] res = new String[POLICY.length];
        System.arraycopy(POLICY, 0, res, 0, POLICY.length);
        return res;
    }
}
