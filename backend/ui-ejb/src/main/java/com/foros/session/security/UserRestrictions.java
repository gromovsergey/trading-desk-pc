package com.foros.session.security;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.User;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class UserRestrictions {
    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private APIRestrictions apiRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private PermissionService permissionService;

    @EJB
    private UserRoleService userRoleService;

    @EJB
    private ConfigService configService;

    @Restriction
    public boolean canView(User user) {
        return accountRestrictions.canView(user.getAccount()) &&
            entityRestrictions.canView(user);
    }

    @Restriction
    public boolean canUpdate(User user) {
        return accountRestrictions.canUpdate(user.getAccount()) &&
            entityRestrictions.canUpdate(user);
    }

    @Restriction
    public boolean canCreate(Account account) {
        return accountRestrictions.canUpdate(account) &&
                (!(account instanceof AdvertiserAccount) || !((AdvertiserAccount) account).isInAgencyAdvertiser());
    }

    @Restriction
    public boolean canActivate(User user) {
        return accountRestrictions.canActivate(user.getAccount().getRole()) &&
            entityRestrictions.canActivate(user);
    }

    @Restriction
    public boolean canInactivate(User user) {
        return accountRestrictions.canInactivate(user.getAccount().getRole()) &&
            entityRestrictions.canInactivate(user);
    }

    @Restriction
    public boolean canUndelete(User user) {
        return accountRestrictions.canUndelete(user.getAccount().getRole()) &&
            entityRestrictions.canUndelete(user);
    }

    @Restriction
    public boolean canDelete(User user) {
        return accountRestrictions.canDelete(user.getAccount().getRole()) &&
            entityRestrictions.canDelete(user);
    }

    @Restriction
    public boolean canChangePassword(User user) {
        return AuthenticationType.PSWD.equals(user.getAuthType()) && isPasswordAuthorizationAllowed(user) && currentUserService.is(user);
    }

    @Restriction
    public boolean canUpdateAdvertisers(User user) {
        return canUpdate(user) && user.isAdvLevelAccessFlag();
    }

    @Restriction
    public boolean canResetPassword(User user) {
        return AuthenticationType.PSWD.equals(user.getAuthType()) && isPasswordAuthorizationAllowed(user) && canUpdate(user) && user.getInheritedStatus() == Status.ACTIVE;
    }

    private boolean isPasswordAuthorizationAllowed(User user) {
        return user.getAccount().getRole() != AccountRole.INTERNAL || configService.get(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED);
    }

    @Restriction
    public boolean canUpdateMyPreferences(User user) {
        return currentUserService.is(user);
    }

    @Restriction
    public boolean canUpdateMaxCreditLimit(Long userRoleId) {
        return permissionService.isGranted("internal_account", "edit_finance") && userRoleService.isAdvertisingFinanceUser(userRoleId);
    }

    @Restriction
    public boolean canChangeRsCredentials(User user) {
        // currently only SELF credentials may be changed
        return currentUserService.is(user) && apiRestrictions.canRun();
    }

    @Restriction
    public boolean canFindAccountManagers() {
        return currentUserService.isInternal();
    }
}
