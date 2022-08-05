package com.foros.session.admin.userRole;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.security.ActionType;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.restriction.permission.PermissionService;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.restriction.registry.PermissionDescriptorFinder;
import com.foros.restriction.registry.PermissionRegistryService;
import com.foros.security.AccountRole;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.security.AuditService;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.permissions.AdvertiserFilter;
import com.foros.util.permissions.CmpFilter;
import com.foros.util.permissions.IspFilter;
import com.foros.util.permissions.PublisherFilter;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

@Stateless(name = "UserRoleService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class UserRoleServiceBean extends BusinessServiceBean<UserRole> implements UserRoleService {

    @EJB
    private AuditService auditService;

    @EJB
    private PermissionService permissionService;

    @EJB
    private PermissionRegistryService permissionRegistryService;

    public UserRoleServiceBean() {
        super(UserRole.class);
    }

    @Override
    @Restrict(restriction = "UserRole.create")
    @Validate(validation = "UserRole.create", parameters = "#userRole")
    @Interceptors({CaptureChangesInterceptor.class})
    public void create(UserRole userRole) {
        prePersist(userRole);
        auditService.audit(userRole, ActionType.CREATE);
        super.create(userRole);
    }
    
    private void prePersist(UserRole userRole) {
        if (userRole.getAccountRole() != INTERNAL) {
            userRole.setFlags(0);
            userRole.setInternalAccessType(null);
            userRole.getAccessAccountIds().clear();
            return;
        } 
        
        if (userRole.getInternalAccessType() != InternalAccessType.MULTIPLE_ACCOUNTS) {
            userRole.getAccessAccountIds().clear();
        }
    }

    @Override
    @Restrict(restriction = "UserRole.update")
    @Validate(validation = "UserRole.update", parameters = "#userRole")
    @Interceptors({CaptureChangesInterceptor.class})
    public UserRole update(UserRole userRole) {
        UserRole oldEntity = findById(userRole.getId());

        if (userRole.getAccountRole() == INTERNAL && userRole.isChanged("flags")) {
            validateRoleChangeAllowed(userRole, oldEntity);
        }
        prePersist(userRole);

        UserRole existed = super.update(userRole);

        removeUnavailablePermissions(existed);

        super.update(existed);

        permissionService.removePolicyCache(existed.getId());
        auditService.audit(existed, ActionType.UPDATE);
        return existed;
    }

    /**
     * Remove unavailable permissions from user role, see
     * {@link #getUnavailablePermissions(com.foros.model.security.UserRole)} for more information
     * @param userRole user role, will be changed
     */
    private void removeUnavailablePermissions(
            UserRole userRole) {

        Set<PolicyEntry> policyEntries = userRole.getPolicyEntries();
        Set<PermissionDescriptor> unavailablePermissions = getUnavailablePermissions(userRole);

        for (Iterator<PolicyEntry> iterator = policyEntries.iterator(); iterator.hasNext(); ) {
            PolicyEntry policyEntry = iterator.next();
            for (PermissionDescriptor permission : unavailablePermissions) {
                if (isSamePermission(policyEntry, permission)) {
                    iterator.remove();
                    em.remove(policyEntry);
                    break;
                }
            }
        }
    }

    private boolean isSamePermission(PolicyEntry policyEntry, PermissionDescriptor descriptor) {
        return policyEntry.getType().equals(descriptor.getObjectType())
                && policyEntry.getAction().equals(descriptor.getActionName())
                && (policyEntry.getParameter() == null || descriptor.getParameter().equals(policyEntry.getParameter()));
    }

    @Override
    @Restrict(restriction = "UserRole.update")
    @Interceptors({CaptureChangesInterceptor.class})
    public void updatePolicy(UserRole userRole) {

        removeUnavailablePermissions(userRole);

        UserRole existed = updatePolicyImpl(userRole);

        auditService.audit(existed, ActionType.UPDATE);
        permissionService.removePolicyCache(userRole.getId());
    }

    /**
     * Policies removed from the UI does not get deleted when the entity (UserRole)
     * is merged, so to overcome that issue, all the deleted Policies are taken
     * in a collection by comparing with the Policies existing in the database
     * and then removed .
     */
    private UserRole updatePolicyImpl(UserRole userRole) {
        UserRole existedUserRole = findById(userRole.getId());

        EntityUtils.checkEntityVersion(existedUserRole, userRole.getVersion());

        Set<PolicyEntry> newPolicy = userRole.getPolicyEntries();

        /* check for unconsistent states */
        Map<String, Set<String>> global = fetchGlobal(newPolicy);
        for (Iterator<PolicyEntry> iterator = newPolicy.iterator(); iterator.hasNext();) {
            PolicyEntry entry = iterator.next();
            if (entry.getParameter() != null) {
                Set<String> actions = global.get(entry.getType());
                if (actions != null && actions.contains(entry.getAction())) {
                    // warning, unconsistent state
                    iterator.remove();
                }
            }
        }

        Set<PolicyEntry> oldPolicy = existedUserRole.getPolicyEntries();

        /* remove unused policy entry */
        for (PolicyEntry entry : oldPolicy) {
            if (!newPolicy.contains(entry)) {
                em.remove(entry);
            }
        }

        existedUserRole.setPolicyEntries(newPolicy);
        return super.update(existedUserRole);
    }

    private Map<String, Set<String>> fetchGlobal(Set<PolicyEntry> policy) {
        HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();

        for (PolicyEntry entry : policy) {
            if (entry.getParameter() == null) {
                Set<String> actions = result.get(entry.getType());
                if (actions == null) {
                    actions = new HashSet<String>();
                    result.put(entry.getType(), actions);
                }
                actions.add(entry.getAction());
            }
        }

        return result;
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    public void removePolicy(String objectType, String parameter) {
        for (PolicyEntry policyEntry : findPolicyEntries(objectType, parameter)) {
            UserRole userRole = policyEntry.getUserRole();
            userRole.getPolicyEntries().remove(policyEntry);
            auditService.audit(userRole, ActionType.UPDATE);
            em.remove(policyEntry);
        }
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    public void mergePolicies(List<UserRole> newUserRoles, String objectType, String parameter) {
        List<PolicyEntry> existingPolicies = new LinkedList<PolicyEntry>(findPolicyEntries(objectType, parameter));

        Map<Long, UserRole> userRolesForUpdate = new HashMap<Long, UserRole>();
        for (PolicyEntry existingPolicy : existingPolicies) {
            UserRole existingRole = existingPolicy.getUserRole();
            if (newUserRoles.contains(existingRole)) {
                UserRole userRole = newUserRoles.get(newUserRoles.indexOf(existingRole));

                EntityUtils.checkEntityVersion(existingRole, userRole.getVersion());

                if (!userRole.getPolicyEntries().contains(existingPolicy)) {
                    existingRole.getPolicyEntries().remove(existingPolicy);
                    em.remove(existingPolicy);
                    userRolesForUpdate.put(existingRole.getId(), existingRole);
                }
            } else {
                throw new VersionCollisionException();
            }
        }

        for (UserRole role : userRolesForUpdate.values()) {
            auditService.audit(role, ActionType.UPDATE);
        }

        em.flush();

        Map<Long, UserRole> newUserRolesForUpdate = new HashMap<Long, UserRole>();
        for (UserRole userRole : newUserRoles) {
            for (PolicyEntry policyEntry : userRole.getPolicyEntries()) {
                if (existingPolicies.contains(policyEntry)) {
                    PolicyEntry existingPolicy = existingPolicies.get(existingPolicies.indexOf(policyEntry));
                    if (existingPolicy.getUserRole().equals(policyEntry.getUserRole())) {
                        continue;
                    }
                }

                UserRole existingRole;
                if (userRolesForUpdate.containsKey(policyEntry.getUserRole().getId())) {
                    existingRole = userRolesForUpdate.get(policyEntry.getUserRole().getId());
                } else {
                    existingRole = findById(policyEntry.getUserRole().getId());
                    EntityUtils.checkEntityVersion(existingRole, userRole.getVersion());
                }

                existingRole.getPolicyEntries().add(policyEntry);
                policyEntry.setId(null);
                policyEntry.setVersion(null);
                policyEntry.setUserRole(existingRole);
                newUserRolesForUpdate.put(existingRole.getId(), existingRole);
            }
        }

        for (UserRole role : newUserRolesForUpdate.values()) {
            auditService.audit(role, ActionType.UPDATE);
        }

        permissionService.removePolicyCache();
    }

    @Override
    public Collection<PolicyEntry> findPolicyEntriesIncludeGlobal(String objectType, String parameter) {
        Query q;
        if (parameter != null) {
            q = em.createQuery("select p from PolicyEntry p where p.type = :type and (p.parameter = :id or p.parameter is null)");
            q.setParameter("id", parameter);
        } else {
            q = em.createQuery("select p from PolicyEntry p where p.type = :type and p.parameter is null");
        }
        q.setParameter("type", objectType);
        return q.getResultList();
    }

    @Override
    public Collection<PolicyEntry> findPolicyEntries(String objectType, String parameter) {
        // noinspection unchecked
        return em.createQuery("select p from PolicyEntry p where p.type = :type and p.parameter = :id")
                .setParameter("type", objectType)
                .setParameter("id", parameter)
                .getResultList();
    }

    @Override
    public Collection<PolicyEntry> findPolicyEntries(Long id) {
        UserRole userRole = super.findById(id);
        userRole.getPolicyEntries().size();
        return userRole.getPolicyEntries();
    }

    @Override
    public List<UserRole> find(AccountRole accountRole) {
        Query query = em.createNamedQuery("UserRole.findByAccountRole");
        query.setParameter("accountRole", accountRole);

        return (List<UserRole>) query.getResultList();
    }

    @Override
    @Restrict(restriction = "UserRole.view")
    public UserRole view(Long id) {
        UserRole role = super.findById(id);
        PersistenceUtils.initialize(role.getPolicyEntries());
        return role;
    }

    @Override
    @Restrict(restriction = "UserRole.view")
    public List<UserRole> findAll() {
        return super.findAll();
    }

    @Override
    public boolean hasManagedAccounts(UserRole internalUserRole, AccountRole managedRole) {
        if (!internalUserRole.isAccountManager()) {
            return false;
        }

        Number res = (Number) em.createQuery(
                "SELECT COUNT(a.id) FROM ExternalAccount a WHERE a.accountManager.role.id = :id AND a.role = :role")
                .setParameter("id", internalUserRole.getId())
                .setParameter("role", managedRole)
                .getSingleResult();

        return res.intValue() != 0;
    }

    private void validateRoleChangeAllowed(UserRole newEntity, UserRole oldEntity) {
        for (AccountRole role : AccountRole.values() ) {
            if ( !newEntity.isAccountManager(role) && hasManagedAccounts(oldEntity, role) ) {
                throw new BusinessException("Changing Account Manager is not permitted.");
            }
        }
    }

    @Override
    public boolean isAdvertisingFinanceUser(Long userRoleId) {

        Number number = (Number) em.createQuery(
                " select count(p.id) " +
                        " from PolicyEntry p" +
                        " where p.userRole.id = :userRoleId" +
                        "   and p.userRole.accountRole = :accountRole" +
                        "   and p.action = 'edit_finance'" +
                        "   and p.type = 'advertising_account'")
                .setParameter("userRoleId", userRoleId)
                .setParameter("accountRole", AccountRole.INTERNAL)
                .getSingleResult();

        return number.intValue() != 0;
    }

    @Override
    public Set<PermissionDescriptor> getAvailablePermissions(UserRole userRole) {
        return permissionRegistryService.finder(userRole.getAccountRole())
                .all()
                .except(getUnavailablePermissions(userRole))
                .find();
    }

    @Override
    public Set<PermissionDescriptor> getUnavailablePermissions(UserRole userRole) {
        HashSet<PermissionDescriptor> result = new HashSet<PermissionDescriptor>();

        if (userRole.isAccountManager()) {
            if (!userRole.isAdvertiserAccountManager()) {
                result.addAll(getAdvertiserPermissions(userRole.getAccountRole()));
            }

            if (!userRole.isPublisherAccountManager()) {
                result.addAll(getPublisherPermissions(userRole.getAccountRole()));
            }

            if (!userRole.isISPAccountManager()) {
                result.addAll(getIspPermissions(userRole.getAccountRole()));
            }

            if (!userRole.isCMPAccountManager()) {
                result.addAll(getCmpPermissions(userRole.getAccountRole()));
            }
        }

        return result;
    }

    private Collection<PermissionDescriptor> getAdvertiserPermissions(AccountRole accountRole) {
        PermissionDescriptorFinder finder = permissionRegistryService.finder(accountRole);

        for (String permission : AdvertiserFilter.getPolicy()) {
            finder = finder.permissions(permission);
        }

        return finder.find();
    }

    private Collection<PermissionDescriptor> getPublisherPermissions(AccountRole accountRole) {
        PermissionDescriptorFinder finder = permissionRegistryService.finder(accountRole);

        for (String permission : PublisherFilter.POLICY) {
            finder = finder.permissions(permission);
        }

        return finder.find();
    }

    private Collection<PermissionDescriptor> getIspPermissions(AccountRole accountRole) {
        PermissionDescriptorFinder finder = permissionRegistryService.finder(accountRole);

        for (String permission : IspFilter.POLICY) {
            finder = finder.permissions(permission);
        }

        return finder.find();
    }

    private Collection<PermissionDescriptor> getCmpPermissions(AccountRole accountRole) {
        PermissionDescriptorFinder finder = permissionRegistryService.finder(accountRole);

        for (String permission : CmpFilter.POLICY) {
            finder = finder.permissions(permission);
        }

        return finder.find();
    }

}
