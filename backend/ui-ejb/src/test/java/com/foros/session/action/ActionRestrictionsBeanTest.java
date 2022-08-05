package com.foros.session.action;

import com.foros.test.factory.ActionTestFactory;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.account.AccountService;

import com.foros.validation.ValidationContext;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class ActionRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Autowired
    private ActionTestFactory actionTF;

    @Autowired
    private ActionService actionService;

    private Action action;

    private AdvertiserAccount account;

    @Override @Before public void setUp() throws Exception {
        super.setUp();
        action = actionTF.createPersistent((AdvertiserAccount) advertiserAllAccess1.getUser().getAccount());
        account = action.getAccount();
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("advertiser_entity", "view") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canView(action);
            }
        };
        setUpAllExpectations(true);
        doCheck(callCanView);

        actionService.delete(action.getId());
        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanView);

        accountService.delete(account.getId());
        setUpExpectationsTrueForInternalsOnly();
        doCheck(callCanView);
    }

    @Test
    public void testUpdate() throws Exception {
        Callable callCanUpdate = new Callable("advertiser_entity", "edit") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canUpdate(action);
            }
        };
        setUpAllExpectations(true);
        doCheck(callCanUpdate);

        actionService.delete(action.getId());
        setUpAllExpectations(false);
        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUndelete() throws Exception {
        Callable call = new ContextCall("advertiser_entity", "undelete") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canUndelete(context, action);
            }
        };

        setUpAllExpectations(false);
        doCheck(call);

        actionService.delete(action.getId());
        setUpExpectationsTrueForInternalsOnly();
        doCheck(call);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable call = new Callable("advertiser_entity", "create") {
            @Override
            public boolean call() {
                return advertiserEntityRestrictions.canCreate(account);
            }
        };

        setUpAllExpectations(true);
        doCheck(call);

        accountService.delete(account.getId());
        setUpAllExpectations(false);
        doCheck(call);
    }

    @Test
    public void testCanDelete() {
        Callable callCanDelete = new ContextCall("advertiser_entity", "edit") {
            @Override
            public void call(ValidationContext context) {
                advertiserEntityRestrictions.canDelete(context, action);
            }
        };

        setUpAllExpectations(true);
        doCheck(callCanDelete);

        actionService.delete(action.getId());
        setUpAllExpectations(false);
        doCheck(callCanDelete);
    }

    private void setUpAllExpectations(boolean result) {
        expectResult(internalAllAccess, result);
        expectResult(advertiserManagerAllAccess1, result);
        expectResult(advertiserAllAccess1, result);
    }

    private void setUpExpectationsTrueForInternalsOnly() {
        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserAllAccess1, false);
    }
}
