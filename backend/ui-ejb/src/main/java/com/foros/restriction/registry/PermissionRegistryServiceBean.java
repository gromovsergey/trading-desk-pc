package com.foros.restriction.registry;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.registry.find.PermissionFinder;
import com.foros.security.AccountRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

@Stateless(name = "PermissionRegistryService")
public class PermissionRegistryServiceBean implements PermissionRegistryService {

    private static final String CLASS_SEARCH_PATTERN = "classpath*:com/foros/session/**/*.class";

    private final Logger logger = Logger.getLogger(PermissionRegistryServiceBean.class.getName());

    private static Map<AccountRole, Map<String, Map<String, PermissionDescriptor>>> permissions = null;

    @PostConstruct
    public void init() {
        synchronized (PermissionRegistryServiceBean.class) {
            if (permissions == null) {
                permissions = initialize();
            }
        }
    }

    protected Map<AccountRole, Map<String, Map<String, PermissionDescriptor>>> initialize() {
        Map<AccountRole, Map<String, Map<String, PermissionDescriptor>>> result
                = new HashMap<AccountRole, Map<String, Map<String, PermissionDescriptor>>>();

        List<Permissions> permissionsList = new PermissionFinder(CLASS_SEARCH_PATTERN).find();
        for (Permissions permissions : permissionsList) {

            for (Permission permission : permissions.value()) {
                AccountRole[] roles = permission.accountRoles().length > 0 ? permission.accountRoles() : AccountRole.values();
                for (AccountRole role : roles) {
                    Map<String, Map<String, PermissionDescriptor>> objectTypes = result.get(role);

                    if (objectTypes == null) {
                        objectTypes = new HashMap<String, Map<String, PermissionDescriptor>>();
                        result.put(role, objectTypes);
                    }

                    Map<String, PermissionDescriptor> descriptors = objectTypes.get(permission.objectType());
                    if (descriptors == null) {
                        descriptors = new HashMap<String, PermissionDescriptor>();
                        objectTypes.put(permission.objectType(), descriptors);
                    }

                    PermissionDescriptor descriptor =
                            new PermissionDescriptor(permission.objectType(), permission.action(), permission.parameterized(), permission.accountRoles());

                    descriptors.put(permission.action(), descriptor);

                    logger.fine("Permission registered: " + descriptor);
                }
            }
        }

        return result;
    }

    @Override
    public PermissionDescriptor getDescriptor(AccountRole accountRole, String objectType, String actionName) {
        Map<String, Map<String, PermissionDescriptor>> objectTypes = permissions.get(accountRole);
        if (objectType != null) {
            Map<String, PermissionDescriptor> descriptors = objectTypes.get(objectType);
            if (descriptors != null) {
                PermissionDescriptor descriptor = descriptors.get(actionName);
                if (descriptor != null) {
                    return descriptor;
                }
            }
        }

        return null;
    }

    @Override
    public Map<String, Map<String, PermissionDescriptor>> getPermissions(AccountRole role) {
        return permissions.get(role);
    }

    @Override
    public PermissionDescriptorFinder finder(AccountRole role) {
        return new PermissionDescriptorFinder(getPermissions(role));
    }

}
