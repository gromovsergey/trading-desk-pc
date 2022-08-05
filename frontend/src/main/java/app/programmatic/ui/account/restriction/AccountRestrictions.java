package app.programmatic.ui.account.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.permission.dao.model.PermissionAction;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;
import app.programmatic.ui.user.dao.model.UserOpts;
import app.programmatic.ui.user.dao.model.UserRole;

import static app.programmatic.ui.account.dao.model.AccountRole.ADVERTISER;
import static app.programmatic.ui.account.dao.model.AccountRole.AGENCY;
import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.*;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.*;

@Service
@Restrictions
public class AccountRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorizationService authorizationService;

    @Restriction("account.viewAdvertising")
    public boolean canViewAdvertising(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        if (!checkAdvertisingPermissions(account, VIEW)) {
            return false;
        }
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("account.viewAdvertisingFinance")
    public boolean canViewAdvertisingFinance(Long accountId) {
        if (!canViewAdvertising(accountId)) {
            return false;
        }
        return permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW_SYSTEM_FINANCE);
    }

    @Restriction("account.viewAdvertisingFinance")
    public boolean canViewAdvertisingFinance() {
        return permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW_SYSTEM_FINANCE);
    }

    @Restriction("account.viewAdvertisingDocuments")
    public boolean canViewAdvertisingDocuments(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);

        // Top level (Agency | Standalone Adv) documents' view / edit abilities
        // are regulated with single 'advertising_account.edit' permission
        return account.getAgencyId() == null ? canUpdateAdvertising(accountId) :
                canViewAdvertising(accountId);
    }

    @Restriction("account.updateAdvertisingDocuments")
    public boolean canUpdateAdvertisingDocuments(Long accountId) {
        return canUpdateAdvertising(accountId);
    }

    @Restriction("account.updateAdvertising")
    public boolean canUpdateAdvertising(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        if (!checkAdvertisingPermissions(account, EDIT)) {
            return false;
        }
        return entityRestrictions.canViewEdit(account);
    }

    private boolean checkAdvertisingPermissions(AdvertisingAccount account, PermissionAction action) {
        if (account.getRole() == ADVERTISER && account.getAgencyId() != null) {
            return permissionService.isGranted(AGENCY_ADVERTISER_ACCOUNT, action);
        } else {
            return permissionService.isGranted(ADVERTISING_ACCOUNT, action);
        }
    }

    @Restriction("account.viewAdvertising")
    public boolean canViewAdvertising() {
        return permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW);
    }

    @Restriction("account.updateAdvertisersInAgency")
    public boolean canUpdateAdvertisersInAgency(Long agencyId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(agencyId);
        if (account.getRole() != AGENCY) {
            return false;
        }
        if (!permissionService.isGranted(AGENCY_ADVERTISER_ACCOUNT, EDIT)) {
            return false;
        }
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("account.createAdvertiserInAgency")
    public boolean canCreateAdvertiserInAgency(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        if (account.getRole() != AGENCY) {
            return false;
        }
        if (!permissionService.isGranted(AGENCY_ADVERTISER_ACCOUNT, CREATE)) {
            return false;
        }
        return entityRestrictions.canViewEdit(account) && !hasRestrictedAdvertisersSet();
    }

    @Restriction("account.viewAdvertiserInAgency")
    public boolean canViewAdvertiserInAgency() {
        return permissionService.isGranted(AGENCY_ADVERTISER_ACCOUNT, VIEW);
    }

    @Restriction("account.searchAdvertising")
    public boolean canSearchAdvertising() {
        if (permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW) || permissionService.isGranted(ADVERTISER_ENTITY, VIEW)) {
            return true;
        }

        if (!permissionService.isGranted(ADVERTISER_ADVERTISING_CHANNEL, VIEW)) {
            return false;
        }

        UserRole userRole = authorizationService.getAuthUser().getUserRole();
        switch (userRole.getAccountRole()) {
            case INTERNAL:
                return !userRole.isAccountManager() || userRole.isAdvertiserAccountManager();
            case ADVERTISER:
                return true;
            default:
                return false;
        }
    }

    @Restriction("account.searchAdvertiserInAgency")
    public boolean canSearchAdvertiserInAgency() {
        return permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW) || permissionService.isGranted(ADVERTISER_ENTITY, VIEW);
    }

    @Restriction("account.findPublishers")
    public boolean canFindPublishers() {
        return authorizationService.getAuthUser().getUserRole().getAccountRole() == INTERNAL;
    }

    private boolean hasRestrictedAdvertisersSet() {
        return authorizationService.getAuthUser().getFlagsSet().contains(UserOpts.ADVERTISER_LEVEL_ACCESS);
    }
}
