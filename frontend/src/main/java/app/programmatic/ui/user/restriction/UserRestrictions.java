package app.programmatic.ui.user.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.restriction.AccountRestrictions;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.service.UserService;

@Service
@Restrictions
public class UserRestrictions {

    @Autowired
    private AccountRestrictions accountRestrictions;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Restriction("user.view")
    public boolean canView(Long userId) {
        User user = userService.findUnrestricted(userId);
        return canView(user);
    }

    @Restriction("user.view")
    public boolean canView(String authToken) {
        User user = userService.findUserByRsKeyUnrestricted(authToken);
        return canView(user);
    }

    private boolean canView(User user) {
        return accountRestrictions.canViewAdvertising(user.getAccountId()) &&
                !entityRestrictions.isDeleted(user);
    }

    @Restriction("user.create")
    public boolean canCreate(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return accountRestrictions.canUpdateAdvertising(account.getId()) &&
                account.getAgencyId() == null;
    }

    @Restriction("user.update")
    public boolean canUpdate(User user) {
        return accountRestrictions.canUpdateAdvertising(user.getAccountId()) &&
                entityRestrictions.canViewEdit(user);
    }

    @Restriction("user.update")
    public boolean canUpdate(Long userId) {
        User user = userService.findUnrestricted(userId);
        return canUpdate(user);
    }
}
