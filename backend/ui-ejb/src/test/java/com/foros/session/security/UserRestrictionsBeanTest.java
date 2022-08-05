package com.foros.session.security;

import com.foros.config.ConfigParameters;
import com.foros.config.MockConfigService;
import com.foros.session.account.AccountService;
import com.foros.test.factory.UserTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.Account;
import com.foros.model.security.User;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class UserRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private UserRestrictions userRestrictions;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MockConfigService configService;

    private User callableUser;

    private Account callableAccount;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
    }

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        enablePasswordAuthorization();
    }

    private void enablePasswordAuthorization() {
        configService.set(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED, true);
    }

    private void disablePasswordAuthorization() {
        configService.set(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED, false);
    }

    @Test
    public void testUpdateMyPreferenceRestrictions() {
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return userRestrictions.canUpdateMyPreferences(callableUser);
            }
        };

        // internal user checks
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callableUser = internalAllAccess.getUser();
        expectResult(internalAllAccess, true);
        doCheck(callable);

        // external user checks
        callableUser = userTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testResetPassword() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canResetPassword(callableUser);
            }
        };
        // internal user checks
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        disablePasswordAuthorization();

        setUpAllExpectations(false);
        doCheck(callable);

        enablePasswordAuthorization();

        // delete user, then check access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        userService.undelete(callableUser.getId());
        accountService.delete(intAc.getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("isp_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canResetPassword(callableUser);
            }
        };

        // external user checks
        callableUser = userTF.createPersistent(ispAllAccess1.getUser().getAccount());
        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(ispAllAccess1, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(publisherAllAccess1, false);
        doCheck(callable);

        // delete user, check access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(ispAllAccess1, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete account, check access
        userService.undelete(callableUser.getId());
        accountService.delete(ispAllAccess1.getUser().getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(ispAllAccess1, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testChangePasswordRestrictions() {
        Callable callable = new Callable() {
            @Override
            public boolean call() {
                return userRestrictions.canChangePassword(callableUser);
            }
        };

        // internal user checks
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callableUser = internalAllAccess.getUser();
        expectResult(internalAllAccess, true);
        doCheck(callable);

        // external user checks
        callableUser = userTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testActivateRestrictions() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canActivate(callableUser);
            }
        };

        // internal user checks
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        userService.inactivate(callableUser.getId());

        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete account, check inactivate user access
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete user, the check inactivate user access
        accountService.undelete(callableUser.getAccount().getId());
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canActivate(callableUser);
            }
        };

        // external user checks
        callableUser = userTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        userService.inactivate(callableUser.getId());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(agencyAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete account, check inactivate user access
        // userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        // delete user, check inactivate access
        accountService.undelete(callableUser.getAccount().getId());
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testInactivateRestrictions() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canInactivate(callableUser);
            }
        };

        // internal user checks
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, the check inactivate user access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete account, check inactivate user access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canInactivate(callableUser);
            }
        };

        // external user checks
        callableUser = userTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(agencyAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, check inactivate access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        // delete account, check inactivate user access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testUndeleteRestrictions() {
        Callable callable = new Callable("internal_account", "undelete") {
            @Override
            public boolean call() {
                return userRestrictions.canUndelete(callableUser);
            }
        };

        // check internal user undelete
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        userService.delete(callableUser.getId());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // undelete user, check access
        userService.undelete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete internal account, check user undelete access
        userService.delete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("advertising_account", "undelete") {
            @Override
            public boolean call() {
                return userRestrictions.canUndelete(callableUser);
            }
        };

        // check external user delete
        callableUser = userTF.createPersistent(agencyAllAccess1.getUser().getAccount());
        userService.delete(callableUser.getId());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(agencyAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyNoAccess, false);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // undelete user, check access
        userService.undelete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        // delete account, then check undelete user access
        userService.delete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testDeleteRestrictions() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canDelete(callableUser);
            }
        };

        // check internal user delete
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, check access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete internal account, check user delete access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("advertising_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canDelete(callableUser);
            }
        };

        // check external user delete
        callableUser = userTF.createPersistent(agencyAllAccess1.getUser().getAccount());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(agencyAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(agencyAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(agencyNoAccess, false);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, check access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        // delete account, then check delete user access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(agencyAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testUpdateRestrictions() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canUpdate(callableUser);
            }
        };

        // check internal user update
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(ispManagerAllAccess1, true);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, check update access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(ispManagerAllAccess1, false);
        doCheck(callable);

        // delete internal account, check user update access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        doCheck(callable);

        callable = new Callable("publisher_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canUpdate(callableUser);
            }
        };

        // check external user update
        callableUser = userTF.createPersistent(publisherAllAccess1.getUser().getAccount());
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherManagerAllAccess1, true);
        expectResult(publisherAllAccess2, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherNoAccess, false);
        expectResult(publisherManagerAllAccess2, false);
        expectResult(ispManagerAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(callable);

        // delete user, check update access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, false);
        doCheck(callable);

        // delete external account, check update access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, false);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testCreateRestrictions() {
        Callable callable = new Callable("internal_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canCreate(callableAccount);
            }
        };

        // create internal account user
        callableAccount = internalAccountTF.createPersistent();

        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
        doCheck(callable);

        // delete account, then check create access
        accountService.delete(callableAccount.getId());
        expectResult(internalAllAccess, false);
        expectResult(advertiserManagerAllAccess1, false);
        doCheck(callable);

        callable = new Callable("cmp_account", "edit") {
            @Override
            public boolean call() {
                return userRestrictions.canCreate(callableAccount);
            }
        };

        // create external account user
        callableAccount = cmpAllAccess1.getUser().getAccount();
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, false);
        expectResult(cmpAllAccess1, true);
        expectResult(cmpAllAccess2, false);
        expectResult(cmpManagerAllAccess1, true);
        expectResult(cmpManagerAllAccess2, false);
        doCheck(callable);

        // delete account and check create user access
        accountService.delete(callableAccount.getId());
        expectResult(internalAllAccess, false);
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        doCheck(callable);
    }

    @Test
    public void testViewRestrictions() {
        Callable callable = new Callable("internal_account", "view") {
            @Override
            public boolean call() {
                return userRestrictions.canView(callableUser);
            }
        };

        // view internal user
        Account intAc = internalAccountTF.createPersistent();
        callableUser = userTF.createPersistent(intAc);
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(publisherAllAccess1, false);
        doCheck(callable);

        // check with users account deleted
        accountService.delete(intAc.getId());
        doCheck(callable);

        // undelete account, delete user, then check
        accountService.undelete(intAc.getId());
        userService.delete(callableUser.getId());
        doCheck(callable);

        callable = new Callable("publisher_account", "view") {
            @Override
            public boolean call() {
                return userRestrictions.canView(callableUser);
            }
        };

        // view external user
        //Account extAc = publisherAccountTF.createPersistent();
        callableUser = userTF.createPersistent(publisherAllAccess1.getUser().getAccount());

        // check for internal role
        expectResult(internalNoAccess, false);
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, false);
        expectResult(publisherAllAccess1, true);
        expectResult(publisherAllAccess2, false);
        expectResult(publisherManagerNoAccess, false);
        expectResult(publisherNoAccess, false);
        expectResult(publisherManagerAllAccess1, true);
        doCheck(callable);

        // delete external user and check access
        userService.delete(callableUser.getId());
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, true);
        doCheck(callable);

        // delete account, have undeleted user and check access
        userService.undelete(callableUser.getId());
        accountService.delete(callableUser.getAccount().getId());
        expectResult(internalAllAccess, true);
        expectResult(publisherAllAccess1, false);
        expectResult(publisherManagerAllAccess1, true);
        doCheck(callable);
    }

    private void setUpAllExpectations(boolean result) {
        expectResult(internalAllAccess, result);
        expectResult(advertiserManagerAllAccess1, result);
        expectResult(internalNoAccess, result);
        expectResult(advertiserAllAccess1, result);
    }
}
