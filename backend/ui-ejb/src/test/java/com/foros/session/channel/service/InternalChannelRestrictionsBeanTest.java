package com.foros.session.channel.service;

import com.foros.model.account.Account;
import com.foros.model.security.PolicyEntry;
import com.foros.security.AccountRole;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class InternalChannelRestrictionsBeanTest extends AbstractChannelRestrictionsBeanTest {

    private UserDefinition advertiserManagerWithInternalChannelsOnly;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    private Account account;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        advertiserManagerWithInternalChannelsOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .advertiserManager()
                .addCustomPermission(new PolicyEntry("internal_advertising_channel", "view"))
                .addCustomPermission(new PolicyEntry("internal_advertising_channel", "edit"));

        account = internalAllAccess.getUser().getAccount();
        expressionChannel = expressionChannelTF.createPersistent(account);
        behavioralChannel = behavioralChannelTF.createPersistent(account);
        audienceChannel = audienceChannelTF.createPersistent(account);
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();

        expectResult(ispAllAccess2, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(cmpAllAccess2, false);
    }

    @Test
    public void testCreateEditViewDelete() {
        Callable canViewRole = new Callable("internal_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(account.getRole());
            }
        };

        Callable canViewAccount = new Callable("internal_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(account);
            }
        };

        Callable canViewExpr = new Callable("internal_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(expressionChannel);
            }
        };

        Callable canViewBeh = new Callable("internal_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(behavioralChannel);
            }
        };

        Callable canViewAud = new Callable("internal_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(audienceChannel);
            }
        };

        Callable canUpdateExpr = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(expressionChannel);
            }
        };

        Callable canUpdateBeh = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(behavioralChannel);
            }
        };

        Callable canUpdateAud = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(audienceChannel);
            }
        };

        Callable canDeleteExpr = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(expressionChannel);
            }
        };

        Callable canDeleteBeh = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(behavioralChannel);
            }
        };

        Callable canDeleteAud = new Callable("internal_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(audienceChannel);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess2, true);
        expectResult(advertiserManagerWithInternalChannelsOnly, true);
        expectResult(cmpManagerAllAccess2, true);

        doCheck(canViewAccount);
        doCheck(canViewExpr);
        doCheck(canViewBeh);
        doCheck(canViewAud);

        doCheck(canUpdateExpr);
        doCheck(canUpdateBeh);
        doCheck(canUpdateAud);

        doCheck(canDeleteExpr);
        doCheck(canDeleteBeh);
        doCheck(canDeleteAud);

        // deleted
        expressionChannelTF.delete(expressionChannel);
        behavioralChannelTF.delete(behavioralChannel);
        audienceChannelTF.delete(audienceChannel);

        doCheck(canViewExpr);
        doCheck(canViewBeh);
        doCheck(canViewAud);
        doCheck(canViewRole);

        expectResult(internalAllAccess, false);
        expectResult(advertiserManagerAllAccess2, false);
        expectResult(advertiserManagerWithInternalChannelsOnly, false);
        expectResult(cmpManagerAllAccess2, false);

        doCheck(canUpdateExpr);
        doCheck(canUpdateBeh);
        doCheck(canUpdateAud);

        doCheck(canDeleteExpr);
        doCheck(canDeleteBeh);
        doCheck(canDeleteAud);
    }

    @Test
    public void testList() {
        Callable canList = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserManagerAllAccess2, true);
        expectResult(cmpManagerAllAccess2, true);
        expectResult(advertiserAllAccess2, true);
        expectResult(cmpAllAccess2, true);

        doCheck(canList);
    }
}