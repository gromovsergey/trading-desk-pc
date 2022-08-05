package com.foros.restriction.registry;

import com.foros.security.AccountRole;

import java.util.Map;
import javax.ejb.Local;

@Local
public interface PermissionRegistryService {

    PermissionDescriptor getDescriptor(AccountRole accountRole, String objectType, String actionName);

    Map<String, Map<String, PermissionDescriptor>> getPermissions(AccountRole role);

    PermissionDescriptorFinder finder(AccountRole role);

}
