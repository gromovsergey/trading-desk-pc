package com.foros.restriction.permission;

import com.foros.restriction.registry.PermissionDescriptor;
import java.util.Map;
import javax.ejb.Local;

/**
 * Service to work with permissions. 
 */
@Local
public interface PermissionService {
    /**
     * Checks whether permission is granted to current user.
     * @param objectType object type
     * @param action action defined for the object type
     * @return 'true' if permission is granted, 'false' - otherwise
     */
    boolean isGranted(String objectType, String action);

    /**
     * Checks whether permission is granted to current user.
     * @param objectType object type
     * @param action action defined for the object type
     * @param parameter permission parameter (usually object id)
     * @return 'true' if permission is granted, 'false' - otherwise
     */
    boolean isGranted(String objectType, String action, String parameter);

    void removePolicyCache(Long userRoleId);

    void removePolicyCache();

    /**
     * @param userRoleId indetifier of user role
     * @return associations of permission -> map(parameter name -> parameter value)
     */
    Map<PermissionDescriptor, Map<String, Long>> getPolicy(Long userRoleId);

}
