package app.programmatic.ui.common.restriction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.Account;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.Owned;
import app.programmatic.ui.common.model.OwnedStatusable;
import app.programmatic.ui.common.model.Statusable;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserOpts;
import app.programmatic.ui.user.dao.model.UserRole;

import java.util.List;

import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;

@Service
public class EntityRestrictions {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private AccountService accountService;

    public boolean canViewEdit(OwnedStatusable entity) {
        return !isDeleted(entity) && isEntityAcessable(entity);
    }

    public boolean isDeleted(Statusable entity) {
        return entity.getInheritedMajorStatus() == MajorDisplayStatus.DELETED;
    }

    public boolean isEntityAcessable(Owned entity) {
        User user = authorizationService.getAuthUser();
        UserRole userRole = user.getUserRole();

        switch (userRole.getAccountRole()) {
            case INTERNAL:
                Account account = accountService.findAccountUnchecked(entity.getAccountId());

                if (userRole.isAccountManager()) {
                    Long accountMangerId = account.getAccountManagerId();
                    return user.getId().equals(accountMangerId);
                }

                switch (userRole.getInternalAccessType()) {
                    case A:
                        return true;
                    case U:
                        return user.getAccountId().equals(account.getInternalAccountId());
                    case M:
                        Long entityInternalAccountId = account.getInternalAccountId();
                        return user.getAccountId().equals(entityInternalAccountId) ||
                                userRole.getAccessAccountIds().contains(entityInternalAccountId);
                    default:
                        throw new IllegalArgumentException("Unexpected internal access type: " + userRole.getInternalAccessType());
                }
            case ADVERTISER:
                return user.getAccountId().equals(entity.getAccountId());
            case AGENCY:
                AdvertisingAccount advertisingAccount = accountService.findAdvertisingUnchecked(entity.getAccountId());
                if (advertisingAccount == null) {
                    return false;
                }

                if (user.getAccountId().equals(advertisingAccount.getId())) {
                    return true;
                }

                if (user.getFlagsSet().contains(UserOpts.ADVERTISER_LEVEL_ACCESS)) {
                    List<Long> advertiserIds = user.getAdvertiserIds();
                    return advertiserIds.contains(advertisingAccount.getId());
                }

                return user.getAccountId().equals(advertisingAccount.getAgencyId());
            case PUBLISHER:
                return user.getAccountId().equals(entity.getAccountId());
        }

        throw new IllegalArgumentException(userRole.getAccountRole() + " role not supported yet");
    }

    public boolean isEntityAcessable(Long accountId) {
        AdvertisingAccount entityAccount = accountService.findAdvertisingUnchecked(accountId);
        return isEntityAcessable(entityAccount);
    }

    public boolean canView(AccountRole accountRole) {
        UserRole currentUserRole = authorizationService.getAuthUser().getUserRole();
        boolean accountManager = currentUserRole.isAccountManager();
        if (currentUserRole.getAccountRole() == INTERNAL) {
            switch (accountRole) {
                case INTERNAL:
                    return true; // Access to internal entities regulated by permissions
                case AGENCY:
                case ADVERTISER:
                    return !accountManager || currentUserRole.isAdvertiserAccountManager();
                case PUBLISHER:
                    return !accountManager || currentUserRole.isPublisherAccountManager();
                default:
                    return false;
            }
        } else {
            return accountRole == currentUserRole.getAccountRole();
        }
    }
}
