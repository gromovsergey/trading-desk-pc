package com.foros.restriction.permission;

import com.foros.cache.CacheManager;
import com.foros.cache.ForosCache;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.restriction.registry.PermissionRegistryService;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless(name = "PermissionService")
public class PermissionServiceBean implements PermissionService {
    private static final String POLICY_CACHE_NODE_NAME = "cache.foros.policy";

    @EJB
    private CacheManager cacheManager;

    @EJB
    private PermissionRegistryService permissionRegistryService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    public boolean isGranted(String objectType, String action) {
        return isGranted(objectType, action, null);
    }

    @Override
    public boolean isGranted(String objectType, String action, String parameter) {
        AccountRole accountRole = SecurityContext.getAccountRole();

        PermissionDescriptor descriptor = permissionRegistryService.getDescriptor(accountRole, objectType, action);

        if (descriptor == null) {
            // permission is not applicable to the user's account role
            return false;
        }

        Long userRoleId = SecurityContext.getPrincipal().getUserRoleId();

        return isGranted(accountRole, userRoleId, descriptor, parameter);
    }

    private boolean isGranted(AccountRole accountRole, Long userRoleId, PermissionDescriptor permission, String parameter) {
        if (!permission.isParameterized() && StringUtil.isPropertyNotEmpty(parameter)) {
            throw new IllegalArgumentException("Permission " + permission + " doesn't require parameter!");
        }

        return isPermissionPresent(accountRole, userRoleId, permission, parameter);
    }

    private boolean isPermissionPresent(AccountRole accountRole, Long userRoleId, PermissionDescriptor permission, String parameter) {
        Map<PermissionDescriptor, Map<String, Long>> cachedPolicy = getPolicy(accountRole, userRoleId);

        Map<String, Long> parameters = cachedPolicy.get(permission);
        if (parameters != null) {
            return     parameters.containsKey(null)        // permit for all instances
                   ||  parameters.containsKey(parameter);  // permit only for instance with parameter
        }

        return false;
    }

    @Override
    public Map<PermissionDescriptor, Map<String, Long>> getPolicy(Long userRoleId) {
        UserRole role = em.find(UserRole.class, userRoleId);
        return getPolicy(role.getAccountRole(), role.getId());
    }

    private Map<PermissionDescriptor, Map<String, Long>> getPolicy(AccountRole accountRole, Long userRoleId) {
        ForosCache cache = cacheManager.getCache(POLICY_CACHE_NODE_NAME);

        Map<PermissionDescriptor, Map<String, Long>> cachedPolicy =
                (Map<PermissionDescriptor, Map<String, Long>>) cache.get(userRoleId);

        // todo: synchronising!!
        if (cachedPolicy == null) {
            cachedPolicy = findPolicyEntries(accountRole, userRoleId);
            cache.put(userRoleId, cachedPolicy);
        }

        // todo: return immutable map!!
        return cachedPolicy;
    }

    private Map<PermissionDescriptor, Map<String, Long>> findPolicyEntries(AccountRole accountRole, Long userRoleId) {
        Map<PermissionDescriptor, Map<String, Long>> result
                = new HashMap<PermissionDescriptor, Map<String, Long>>();

        List<PolicyEntry> entries = findPolicy(userRoleId);

        for (PolicyEntry entry : entries) {
            String objectType = entry.getType();
            String parameter = entry.getParameter();

            PermissionDescriptor permission = permissionRegistryService.getDescriptor(accountRole, objectType, entry.getAction());

            if (permission != null) {
                Map<String, Long> parameters = result.get(permission);
                if (parameters == null) {
                    parameters = new HashMap<String, Long>();
                    result.put(permission, parameters);
                }

                // duplicate of db constraint
                if (parameters.containsValue(parameter)) {
                    throw new IllegalStateException("Duplicate permission! Permission: " + entry);
                }

                parameters.put(parameter, entry.getId());
            } /* else -- ignore undefined permission */
        }

        return Collections.unmodifiableMap(result);
    }

    private List<PolicyEntry> findPolicy(Long userRoleId) {
        return (List<PolicyEntry>) em
                .createNamedQuery("PolicyEntry.findPermissionsByUserRole")
                .setParameter("userRoleId", userRoleId)
                .getResultList();
    }

    @Override
    public void removePolicyCache(Long userRoleId) {
        ForosCache cache = cacheManager.getCache(POLICY_CACHE_NODE_NAME);
        cache.remove(userRoleId);
    }

    @Override
    public void removePolicyCache() {
        ForosCache cache = cacheManager.getCache(POLICY_CACHE_NODE_NAME);
        cache.clear();
    }
}
