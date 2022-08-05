package com.foros.session.account;

import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.AGENCY;
import static com.foros.security.AccountRole.CMP;
import static com.foros.security.AccountRole.INTERNAL;
import static com.foros.security.AccountRole.ISP;
import static com.foros.security.AccountRole.PUBLISHER;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.GenericAccount;
import com.foros.model.account.InternalAccount;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.UtilityService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "agency_advertiser_account", action = "create", accountRoles = { INTERNAL, AGENCY }),
        @Permission(objectType = "agency_advertiser_account", action = "view", accountRoles = { INTERNAL, AGENCY }),
        @Permission(objectType = "agency_advertiser_account", action = "edit", accountRoles = { INTERNAL, AGENCY }),
        @Permission(objectType = "agency_advertiser_account", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertising_account", action = "create", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertising_account", action = "edit", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertising_account", action = "edit_finance", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertising_account", action = "view", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertising_account", action = "view_system_finance", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertising_account", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "publisher_account", action = "create", accountRoles = { INTERNAL }),
        @Permission(objectType = "publisher_account", action = "edit", accountRoles = { INTERNAL, PUBLISHER }),
        @Permission(objectType = "publisher_account", action = "edit_finance", accountRoles = { INTERNAL }),
        @Permission(objectType = "publisher_account", action = "view", accountRoles = { INTERNAL, PUBLISHER }),
        @Permission(objectType = "publisher_account", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "isp_account", action = "create", accountRoles = { INTERNAL }),
        @Permission(objectType = "isp_account", action = "edit", accountRoles = { INTERNAL, ISP }),
        @Permission(objectType = "isp_account", action = "edit_finance", accountRoles = { INTERNAL }),
        @Permission(objectType = "isp_account", action = "view", accountRoles = { INTERNAL, ISP }),
        @Permission(objectType = "isp_account", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "cmp_account", action = "create", accountRoles = { INTERNAL }),
        @Permission(objectType = "cmp_account", action = "edit", accountRoles = { INTERNAL, CMP }),
        @Permission(objectType = "cmp_account", action = "edit_finance", accountRoles = { INTERNAL }),
        @Permission(objectType = "cmp_account", action = "view", accountRoles = { INTERNAL, CMP }),
        @Permission(objectType = "cmp_account", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "internal_account", action = "create", accountRoles = { INTERNAL }),
        @Permission(objectType = "internal_account", action = "edit", accountRoles = { INTERNAL }),
        @Permission(objectType = "internal_account", action = "edit_finance", accountRoles = { INTERNAL }),
        @Permission(objectType = "internal_account", action = "view", accountRoles = { INTERNAL }),
        @Permission(objectType = "internal_account", action = "undelete", accountRoles = { INTERNAL })
})
public class AccountRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AdvertisingAccountRestrictions advertisingAccountRestrictions;

    @EJB
    private AgencyAdvertiserAccountRestrictions agencyAdvertiserAccountRestrictions;

    @EJB
    private UtilityService utilityService;

    @Restriction
    public boolean canSetTestFlag(Account account) {
        if (account instanceof CmpAccount || account instanceof InternalAccount) {
            return false;
        }

        if (account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser()) {
            return false;
        }

        return (account == null || account.getId() == null || !account.getTestFlag());
    }

    @Restriction
    public boolean canUpdateBillingContactDetails(Account account) {
        if (SecurityContext.isAgencyOrAdvertiser()) {
            if (account instanceof AdvertisingAccountBase) {
                return canUpdate(account) && !((AdvertisingAccountBase) account).getFinancialSettings().isFrozen();
            }
        }
        return canUpdate(account);
    }

    @Restriction
    public boolean canCreate(AccountRole role) {
        return permissionService.isGranted(getPermissionObjectType(role), "create");
    }

    @Restriction
    public boolean canCreateAgencyAccount(AgencyAccount account) {
        return canCreate(account.getRole()) && advertisingAccountRestrictions.canUpdateCommission(account);
    }

    @Restriction
    public boolean canCreateStandaloneAdvertiserAccount(AdvertiserAccount account) {
        return canCreate(account.getRole()) && account.isStandalone();
    }

    @Restriction
    public boolean canCreate(String roleName) {
        return permissionService.isGranted(getPermissionObjectType(roleName), "create");
    }

    @Restriction
    public boolean canUpdate(Account account) {
        return permissionService.isGranted(getPermissionObjectType(account), "edit") &&
                entityRestrictions.canUpdate(account);
    }

    @Restriction
    public boolean canUpdateAgencyAccount(AgencyAccount account) {
        return canUpdate(account) && advertisingAccountRestrictions.canUpdateCommission(account);
    }

    @Restriction
    public boolean canUpdate(AccountRole accountRole) {
        return permissionService.isGranted(getPermissionObjectType(accountRole), "edit");
    }

    public boolean isUpdateFinanceGranted(Account account) {
        return permissionService.isGranted(getPermissionObjectType(account), "edit_finance");
    }

    @Restriction
    public boolean canView(String roleName) {
        return permissionService.isGranted(getPermissionObjectType(roleName), "view");
    }

    @Restriction
    public boolean canView(AccountRole role) {
        return permissionService.isGranted(getPermissionObjectType(role), "view");
    }

    @Restriction
    public boolean canView(Long accountId) {
        Account account = utilityService.find(Account.class, accountId);
        return permissionService.isGranted(getPermissionObjectType(account), "view") &&
                entityRestrictions.canView(account);
    }

    @Restriction
    public boolean canView(Account account) {
        if (account instanceof GenericAccount)
            account = utilityService.find(Account.class, account.getId());
        return permissionService.isGranted(getPermissionObjectType(account), "view") &&
                entityRestrictions.canView(account);
    }

    @Restriction
    /**
     *  Top level (Agency | Standalone Adv) documents' view / edit abilities
     *  are regulated by single 'advertising_account.edit' permission
     */
    public boolean canViewDocuments(Account account) {
        if (account instanceof AdvertiserAccount) {
            if (((AdvertiserAccount)account).isInAgencyAdvertiser()) {
                return agencyAdvertiserAccountRestrictions.canView((AdvertiserAccount)account);
            }

            return canUpdate(account);
        }

        if (account instanceof AgencyAccount) {
            return canUpdate(account);
        }

        return false;
    }

    @Restriction
    public boolean canUpdateDocuments(Account account) {
        if (account instanceof AdvertiserAccount) {
            if (((AdvertiserAccount)account).isInAgencyAdvertiser()) {
                return agencyAdvertiserAccountRestrictions.canUpdate((AdvertiserAccount)account);
            }
            return canUpdate(account);
        }

        if (account instanceof AgencyAccount) {
            return canUpdate(account);
        }

        return false;
    }

    @Restriction
    public boolean canSearchAccountApi() {
        return SecurityContext.isInternal() &&
                (permissionService.isGranted("internal_account", "view") ||
                        permissionService.isGranted("advertising_account", "view") ||
                        permissionService.isGranted("publisher_account", "view") ||
                        permissionService.isGranted("isp_account", "view") ||
                        permissionService.isGranted("cmp_account", "view"));
    }

    @Restriction
    public boolean canActivate(AccountRole accountRole) {
        return permissionService.isGranted(getPermissionObjectType(accountRole), "edit");
    }

    @Restriction
    public boolean canActivate(Account account) {
        return canActivate(account.getRole()) &&
                entityRestrictions.canActivate(account);
    }

    @Restriction
    public boolean canInactivate(AccountRole accountRole) {
        return permissionService.isGranted(getPermissionObjectType(accountRole), "edit");
    }

    @Restriction
    public boolean canInactivate(Account account) {
        return canInactivate(account.getRole()) &&
                entityRestrictions.canInactivate(account);
    }

    @Restriction
    public boolean canDelete(AccountRole accountRole) {
        return permissionService.isGranted(getPermissionObjectType(accountRole), "edit");
    }

    @Restriction
    public boolean canDelete(Account account) {
        return canDelete(account.getRole()) &&
                entityRestrictions.canDelete(account);
    }

    @Restriction
    public boolean canUndelete(AccountRole accountRole) {
        return permissionService.isGranted(getPermissionObjectType(accountRole), "undelete");
    }

    @Restriction
    public boolean canUndelete(Account account) {
        return canUndelete(account.getRole()) &&
                entityRestrictions.canUndelete(account);
    }

    @Restriction
    public boolean canUpdateTerms(Account account) {
        return currentUserService.isInternal() && canUpdate(account);
    }

    private static String getPermissionObjectType(String roleName) {
        return getPermissionObjectType(AccountRole.byName(roleName));
    }

    private static String getPermissionObjectType(Account account) {
        return getPermissionObjectType(account.getRole());
    }

    private static String getPermissionObjectType(AccountRole role) {
        switch (role) {
        case INTERNAL:
            return "internal_account";
        case ADVERTISER:
        case AGENCY:
            return "advertising_account";
        case PUBLISHER:
            return "publisher_account";
        case ISP:
            return "isp_account";
        case CMP:
            return "cmp_account";
        default:
            throw new IllegalArgumentException("Invalid account role: " + role);
        }
    }
}
