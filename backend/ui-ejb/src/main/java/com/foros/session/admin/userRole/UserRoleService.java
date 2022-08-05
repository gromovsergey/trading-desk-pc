package com.foros.session.admin.userRole;

import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.security.AccountRole;
import com.foros.service.ByIdLocatorService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface UserRoleService extends ByIdLocatorService<UserRole> {

    void create(UserRole userRole);

    UserRole update(UserRole userRole);

    void refresh(Long id);

    UserRole view(Long id);

    List<UserRole> findAll();

    Collection<PolicyEntry> findPolicyEntries(Long userRoleId);

    Collection<PolicyEntry> findPolicyEntries(String objectType, String parameter);

    Collection<PolicyEntry> findPolicyEntriesIncludeGlobal(String objectType, String parameter);

    void mergePolicies(List<UserRole> userRoles, String objectType, String parameter);

    void removePolicy(String objectTpe, String parameter);

    List<UserRole> find(AccountRole accountRole);

    void updatePolicy(UserRole userRole);

    /**
     * Returns true if exists internal users of specified user role, which are set as Account Managers for some external accounts
     * of specified accountRole, False otherwise.
     *
     * This check helps to restrict removing Account Manager flag in case of existing managed external accounts.   
     *
     * @param role - Account Manager's user role, with is checked for bound external accounts.
     * @param accountRole - managed account role, to be checked for managed external accounts.
     * @return true if external accounts exits, false othewise.
     */
    boolean hasManagedAccounts(UserRole role, AccountRole accountRole);

    boolean isAdvertisingFinanceUser(Long userRoleId);


    /**
     * @param userRole user role
     * @return permission available for this user role
     */
    Set<PermissionDescriptor> getAvailablePermissions(UserRole userRole);

    /**
     * @param userRole user role
     * @return permission unavailable for this user role
     */
    Set<PermissionDescriptor> getUnavailablePermissions(UserRole userRole);

}
