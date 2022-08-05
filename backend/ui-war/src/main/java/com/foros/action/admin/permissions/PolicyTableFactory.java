package com.foros.action.admin.permissions;

import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.util.CollectionUtils;
import com.foros.util.permissions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PolicyTableFactory {

    private static final Map<String, ? extends ParametersProvider> providers =
            CollectionUtils.map("birt_report", new BirtReportsIdsProvider()).build();

    private static final List<? extends ZoneFilter> filters = Arrays.asList(
            new AdvertiserFilter(),
            new PublisherFilter(),
            new IspFilter(),
            new CmpFilter(),
            new InternalFilter(),
            new AdminFilter(),
            new APIFilter(),
            new PredefinedReportFilter(),
            new BirtReportFilter(),
            new AgentReportFilter(),
            new AudienceResearchFilter()
    );

    private static final Map<PermissionsZone, List<String>> orderByZone = CollectionUtils
            .map(PermissionsZone.DEFAULT, Arrays.asList("view", "create", "edit", "undelete"))
            .map(PermissionsZone.BIRT_REPORTS, Arrays.asList("run", "edit", "create"))
            .build();

    private Map<PermissionDescriptor, Map<String, Long>> policy;

    private Map<PermissionsZone, Map<String, Map<String, PermissionDescriptor>>> policyByZones;

    private Map<String, List<Parameter>> parameters;

    public PolicyTableFactory(
            Map<PermissionDescriptor, Map<String, Long>> policy,
            Map<String, Map<String, PermissionDescriptor>> permissions) {
        this.policy = policy;
        this.policyByZones = fetchPolicyByZones(permissions);
        this.parameters = fetchParameters();
    }

    private Map<String, List<Parameter>> fetchParameters() {
        HashMap<String, List<Parameter>> result = new HashMap<String, List<Parameter>>();

        for (Map.Entry<String, ? extends ParametersProvider> entry : providers.entrySet()) {
            result.put(entry.getKey(), entry.getValue().parameters());
        }

        return result;
    }

    private Map<PermissionsZone, Map<String, Map<String, PermissionDescriptor>>> fetchPolicyByZones(
                Map<String, Map<String, PermissionDescriptor>> permissions) {

        Map<PermissionsZone, Map<String, Map<String, PermissionDescriptor>>> policyByZones =
                new HashMap<PermissionsZone, Map<String, Map<String, PermissionDescriptor>>>();

        for (Map<String, PermissionDescriptor> objectTypes : permissions.values()) {
            for (PermissionDescriptor permissionDescriptor : objectTypes.values()) {
                PermissionsZone zone = zone(permissionDescriptor);
                putPolicyIn(policyByZones, zone, permissionDescriptor);
            }
        }

        return policyByZones;
    }

    private void putPolicyIn(Map<PermissionsZone, Map<String, Map<String, PermissionDescriptor>>> policyByZones,
                          PermissionsZone zone, PermissionDescriptor descriptor) {

        Map<String, Map<String, PermissionDescriptor>> objectTypes = policyByZones.get(zone);
        if (objectTypes == null) {
            objectTypes = new TreeMap<String, Map<String, PermissionDescriptor>>(PolicyTable.policyComparator);
            policyByZones.put(zone, objectTypes);
        }

        Map<String, PermissionDescriptor> actions = objectTypes.get(descriptor.getObjectType());
        if (actions == null) {
            actions = new HashMap<String, PermissionDescriptor>();
            objectTypes.put(descriptor.getObjectType(), actions);
        }

        actions.put(descriptor.getActionName(), descriptor);
    }

    private PermissionsZone zone(PermissionDescriptor permissionDescriptor) {
        for (ZoneFilter zoneFilter : filters) {
            if (zoneFilter.accept(permissionDescriptor)) {
                return zoneFilter.getZone();
            }
        }
        // all policy must be filtered so if code runs there there is a mistake
        assert true;
        return PermissionsZone.DEFAULT;
    }

    public PolicyTable getPolicyTable(PermissionsZone zone) {
        return new PolicyTable(policyByZones.get(zone), policy, parameters, orderByZone.get(zone));
    }

}
