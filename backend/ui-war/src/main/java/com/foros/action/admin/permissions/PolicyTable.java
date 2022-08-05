package com.foros.action.admin.permissions;

import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PolicyTable {

    private Map<String, Map<String, PermissionDescriptor>> permissions;

    private Comparator actionComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int index1 = actionOrder.indexOf(o1);
            int index2 = actionOrder.indexOf(o2);

            if (index1 == -1 && index2 == -1) {
                return o1.compareTo(o2);
            }
            if (index1 == -1) {
                return 1;
            }
            if (index2 == -1) {
                return -1;
            }
            return index1 - index2;
        }
    };

    protected static Comparator policyComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return StringUtil.resolveGlobal("permissionType", o1, false).compareTo(StringUtil.resolveGlobal("permissionType", o2, false));
        }
    };
    
    private Map<String, List<Parameter>> parameters;
    private Map<PermissionDescriptor, Map<String, Long>> summaryPolicy;
    private Map<String, Map<Parameter, Set<String>>> policy;
    private List<String> actionOrder;
    private List<String> actions;

    public PolicyTable(
            Map<String, Map<String, PermissionDescriptor>> permissions,
            Map<PermissionDescriptor, Map<String, Long>> summaryPolicy,
            Map<String, List<Parameter>> parameters,
            List<String> actionOrder
    ) {
        this.permissions = permissions == null ? Collections.<String, Map<String, PermissionDescriptor>>emptyMap() : permissions;
        this.actionOrder = actionOrder == null ? Collections.<String>emptyList() : actionOrder;
        this.parameters = parameters;
        this.summaryPolicy = summaryPolicy;
        this.actions = fetchActions();
        this.policy = fetchPolicy();
    }

    private Map<String, Map<Parameter, Set<String>>> fetchPolicy() {
        Map<String, Map<Parameter, Set<String>>> result = new TreeMap<String, Map<Parameter, Set<String>>>(policyComparator);

        for (Map<String, PermissionDescriptor> actions : permissions.values()) {
            for (PermissionDescriptor descriptor : actions.values()) {
                Map<String, Long> parameters = summaryPolicy.get(descriptor);
                if (parameters != null && !parameters.isEmpty()) {

                    Map<String, Parameter> availableParameters = getParametersMap(descriptor.getObjectType());

                    Map<Parameter, Set<String>> itemParameters = result.get(descriptor.getObjectType());
                    if (itemParameters == null) {
                        itemParameters = new TreeMap<Parameter, Set<String>>();
                        result.put(descriptor.getObjectType(), itemParameters);
                    }

                    for (String parameter : parameters.keySet()) {
                        Parameter parameterInfo = availableParameters.containsKey(parameter) ?
                                availableParameters.get(parameter) : Parameter.nullParameter();

                        Set<String> itemActions = itemParameters.get(parameterInfo);
                        if (itemActions == null) {
                            itemActions = new TreeSet<String>(actionComparator);
                            itemParameters.put(parameterInfo, itemActions);
                        }

                        itemActions.add(descriptor.getActionName());
                    }
                }
            }
        }

        return result;
    }

    private List<String> fetchActions() {
        Set<String> result = new HashSet<String>();
        for (Map<String, PermissionDescriptor> actionsMap : permissions.values()) {
            result.addAll(actionsMap.keySet());
        }

        return sort(new ArrayList<String>(result));
    }

    private List<String> sort(List<String> list) {
        ArrayList<String> result = new ArrayList<String>(list);
        Collections.sort(result, actionComparator);
        return result;
    }

    public boolean checked(PermissionDescriptor descriptor, String parameter) {
        if (summaryPolicy.containsKey(descriptor)) {
            Map<String, Long> parameters = summaryPolicy.get(descriptor);
            return parameters.containsKey(parameter);
        } else {
            return false;
        }
    }

    public Long id(PermissionDescriptor descriptor, String parameter) {
        if (summaryPolicy.containsKey(descriptor)) {
            Map<String, Long> parameters = summaryPolicy.get(descriptor);
            return parameters.get(parameter);
        } else {
            return null;
        }
    }

    public Map<String, Map<Parameter, Set<String>>> getPolicy() {
        return policy;
    }

    public boolean isEmpty() {
        return permissions.isEmpty();
    }

    public boolean hasPolicy() {
        return !policy.isEmpty();
    }

    public Map<String, Map<String, PermissionDescriptor>> getPermissions() {
        return permissions;
    }

    public List<Parameter> getParameters(String objectType) {
        return parameters.get(objectType);
    }

    private Map<String, Parameter> getParametersMap(String objectType) {
        HashMap<String, Parameter> result = new HashMap<String, Parameter>();

        List<Parameter> parameterList = parameters.get(objectType);
        if (parameterList != null) {
            for (Parameter parameter : parameterList) {
                result.put(parameter.getName(), parameter);
            }
        }

        return result;
    }

    public List<String> getActions() {
        return actions;
    }

}
