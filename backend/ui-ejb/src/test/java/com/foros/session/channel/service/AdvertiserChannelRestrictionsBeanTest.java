package com.foros.session.channel.service;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.security.AccountType;
import com.foros.model.security.PolicyEntry;
import com.foros.security.AccountRole;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class AdvertiserChannelRestrictionsBeanTest extends AbstractChannelRestrictionsBeanTest {

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTestFactory;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    private UserDefinition internalWithoutViewCategories;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        AccountType at = advertiserAllAccess1.getUser().getAccount().getAccountType();
        at.setCPAFlag(CCGType.DISPLAY, true);

        expressionChannel = expressionChannelTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        behavioralChannel = behavioralChannelTF.createPersistent(advertiserAllAccess1.getUser().getAccount());
        audienceChannel = audienceChannelTF.createPersistent(advertiserAllAccess1.getUser().getAccount());

        internalWithoutViewCategories = userDefinitionFactory.create(AccountRole.INTERNAL, PermissionsSet.ALL)
                .removePermission(new PolicyEntry("categoryChannel", "view"));
    }

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(cmpAllAccess1, false);
        expectResult(cmpManagerAllAccess1, false);
        expectResult(ispAllAccess2, false);
        expectResult(advertiserAllAccess2, false);
        expectResult(advertiserManagerAllAccess2, false);
    }

    @Test
    public void testCreateEditViewDelete() {
        Callable canViewExpr = new Callable("advertiser_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(expressionChannel);
            }
        };

        Callable canViewBeh = new Callable("advertiser_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(behavioralChannel);
            }
        };

        Callable canViewAud = new Callable("advertiser_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(audienceChannel);
            }
        };

        Callable canUpdateExpr = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(expressionChannel);
            }
        };

        Callable canUpdateBeh = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(behavioralChannel);
            }
        };

        Callable canDeleteExpr = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(expressionChannel);
            }
        };

        Callable canUpdateAud = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpdate(audienceChannel);
            }
        };

        Callable canDeleteBeh = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(behavioralChannel);
            }
        };

        Callable canDeleteAud = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canDelete(audienceChannel);
            }
        };

        Callable canList = new Callable("advertiser_advertising_channel", "view") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canView(behavioralChannel.getAccount());
            }
        };

        Callable canCopyBeh = new Callable("advertiser_advertising_channel", "create") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canCreateCopy(behavioralChannel);
            }
        };

        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(internalAllAccess, true);

        doCheck(canViewExpr);
        doCheck(canViewBeh);
        doCheck(canViewAud);

        doCheck(canUpdateExpr);
        doCheck(canUpdateBeh);

        doCheck(canDeleteExpr);
        doCheck(canDeleteBeh);

        doCheck(canList);

        doCheck(canCopyBeh);

        expectResult(advertiserAllAccess1, false);
        doCheck(canUpdateAud);
        doCheck(canDeleteAud);

        // deleted
        expressionChannelTF.delete(expressionChannel);
        behavioralChannelTF.delete(behavioralChannel);
        audienceChannelTF.delete(audienceChannel);

        expectResult(advertiserAllAccess1, false);

        doCheck(canViewExpr);
        doCheck(canViewBeh);
        doCheck(canViewAud);

        expectResult(advertiserManagerAllAccess1, false);
        expectResult(internalAllAccess, false);

        doCheck(canUpdateExpr);
        doCheck(canUpdateBeh);
        doCheck(canUpdateAud);

        doCheck(canDeleteExpr);
        doCheck(canDeleteBeh);
        doCheck(canDeleteAud);

        doCheck(canCopyBeh);
    }

    @Test
    public void testCanCreateForWrongAccount() {
        final AgencyAccount agency = (AgencyAccount) agencyAllAccess1.getUser().getAccount();
        agency.getAccountType().setCPCFlag(CCGType.DISPLAY, true);
        final AdvertiserAccount advertiser = advertiserAccountTestFactory.createPersistentAdvertiserInAgency(agency);

        Callable canCreateAgency = new Callable("advertiser_advertising_channel", "create") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canCreate(agency);
            }
        };

        expectResult(agencyAllAccess1, true);
        expectResult(internalAllAccess, true);

        doCheck(canCreateAgency);

        Callable canCreateAgencyAdvertiser = new Callable("advertiser_advertising_channel", "create") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canCreate(advertiser);
            }
        };

        expectResult(agencyAllAccess1, false);
        expectResult(internalAllAccess, false);

        doCheck(canCreateAgencyAdvertiser);
    }

    @Test
    public void testUndelete() {
        Callable canUndeleteExpr = new Callable("advertiser_advertising_channel", "undelete") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUndelete(expressionChannel);
            }
        };

        Callable canUndeleteBeh = new Callable("advertiser_advertising_channel", "undelete") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUndelete(behavioralChannel);
            }
        };

        Callable canUndeleteAud = new Callable("advertiser_advertising_channel", "undelete") {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUndelete(audienceChannel);
            }
        };

        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        expectResult(internalAllAccess, false);

        doCheck(canUndeleteExpr);
        doCheck(canUndeleteBeh);
        doCheck(canUndeleteAud);

        // deleted
        expressionChannelTF.delete(expressionChannel);
        behavioralChannelTF.delete(behavioralChannel);
        audienceChannelTF.delete(audienceChannel);

        expectResult(advertiserManagerAllAccess1, true);
        expectResult(internalAllAccess, true);

        doCheck(canUndeleteExpr);
        doCheck(canUndeleteBeh);
        doCheck(canUndeleteAud);
    }

    @Test
    public void testEditCategories() {
        Callable canEditCategories = new Callable("advertiser_advertising_channel", "edit") {
            @Override
            public boolean call() {
                return channelRestrictions.canEditCategories(behavioralChannel);
            }
        };

        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(internalAllAccess, true);
        expectResult(internalWithoutViewCategories, false);

        doCheck(canEditCategories);

        // deleted
        behavioralChannelTF.delete(behavioralChannel);

        expectResult(advertiserManagerAllAccess1, false);
        expectResult(internalAllAccess, false);

        doCheck(canEditCategories);
    }

    @Test
    public void testUpload() {
        final Account account = behavioralChannel.getAccount();

        Callable canUpload = new Callable() {
            @Override
            public boolean call() {
                return advertisingChannelRestrictions.canUpload(account);
            }
        };

        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(internalAllAccess, true);

        doCheck(canUpload);

        // deleted
        advertiserAccountTF.delete(account);

        expectResult(advertiserAllAccess1, false);
        expectResult(advertiserManagerAllAccess1, false);
        expectResult(internalAllAccess, false);

        doCheck(canUpload);
    }
}
