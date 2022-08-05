package com.foros.util.permissions;

import com.foros.restriction.registry.PermissionDescriptor;

import org.apache.commons.lang.ArrayUtils;

public class AdminFilter implements ZoneFilter {
    public static final String[] POLICY = {"bannedChannel", "kwmTool", "colocation", "channel_match_test", "fileManager",
            "adOpsDashboard", "currency", "triggerQA", "discoverChannel", "creativeCategory", "fraudConditions",
            "template", "currencyExchange", "applicationFormat", "globalParams", "userRole",
            "country", "creativeSize", "wdRequestMapping", "accountType", "wdFrequencyCaps", "categoryChannel",
            "keyword_channel", "walledGarden", "searchEngine", "deviceChannel", "geoChannel"};

    @Override
    public PermissionsZone getZone() {
        return PermissionsZone.ADMIN;
    }

    @Override
    public boolean accept(PermissionDescriptor descriptor) {
        return ArrayUtils.indexOf(POLICY, descriptor.getObjectType()) != ArrayUtils.INDEX_NOT_FOUND;
    }
}
