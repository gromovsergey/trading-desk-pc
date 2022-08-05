package com.foros.session.security;

import com.foros.AbstractRestrictionsBeanTest;
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
public class ContextRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private ContextRestrictions contextRestrictions;

    private UserDefinition internalAdvertiserAccountPermissionOnly;
    private UserDefinition internalAdvertiserChannelPermissionOnly;
    private UserDefinition internalAdvertiserEntityPermissionOnly;

    private UserDefinition internalPublisherAccountPermissionOnly;

    private UserDefinition internalISPAccountPermissionOnly;
    private UserDefinition internalColocationViewPermissionOnly;

    private UserDefinition internalCMPAccountPermissionOnly;
    private UserDefinition internalCMPChannelPermissionOnly;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();

        internalAdvertiserAccountPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("advertising_account", "view"));
        internalAdvertiserChannelPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("advertiser_advertising_channel", "view"));
        internalAdvertiserEntityPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("advertiser_entity", "view"));

        internalPublisherAccountPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("publisher_account", "view"));

        internalISPAccountPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("isp_account", "view"));
        internalColocationViewPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("colocation", "view"));

        internalCMPAccountPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("cmp_account", "view"));
        internalCMPChannelPermissionOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
                .addCustomPermission(new PolicyEntry("cmp_advertising_channel", "view"));
    }

    @Test
    public void testCanSwitchToAdvertiser() throws Exception {
        Callable callCanSwitch = new Callable() {
            @Override
            public boolean call() {
                return contextRestrictions.canSwitch("Advertiser");
            }
        };

        expectResult(internalAdvertiserAccountPermissionOnly, true);
        expectResult(internalAdvertiserChannelPermissionOnly, true);
        expectResult(internalAdvertiserEntityPermissionOnly, true);
        expectResult(internalNoAccess, false);

        doCheck(callCanSwitch);
    }

    @Test
    public void testCanSwitchToPublisher() throws Exception {
        Callable callCanSwitch = new Callable() {
            @Override
            public boolean call() {
                return contextRestrictions.canSwitch("Publisher");
            }
        };

        expectResult(internalPublisherAccountPermissionOnly, true);
        expectResult(internalNoAccess, false);

        doCheck(callCanSwitch);
    }

    @Test
    public void testCanSwitchToISP() throws Exception {
        Callable callCanSwitch = new Callable() {
            @Override
            public boolean call() {
                return contextRestrictions.canSwitch("ISP");
            }
        };

        expectResult(internalISPAccountPermissionOnly, true);
        expectResult(internalColocationViewPermissionOnly, true);
        expectResult(internalNoAccess, false);

        doCheck(callCanSwitch);
    }

    @Test
    public void testCanSwitchToCMP() throws Exception {
        Callable callCanSwitch = new Callable() {
            @Override
            public boolean call() {
                return contextRestrictions.canSwitch("CMP");
            }
        };

        expectResult(internalCMPAccountPermissionOnly, true);
        expectResult(internalCMPChannelPermissionOnly, true);
        expectResult(internalNoAccess, false);

        doCheck(callCanSwitch);
    }

    @Test
    public void testCanSwitchToAccount() throws Exception {
        Callable callCanSwitch = new Callable() {
            @Override
            public boolean call() {
                return contextRestrictions.canSwitch(advertiserAllAccess1.getUser().getAccount());
            }
        };

        expectResult(internalAdvertiserAccountPermissionOnly, true);
        expectResult(internalAdvertiserChannelPermissionOnly, true);
        expectResult(internalAdvertiserEntityPermissionOnly, true);
        expectResult(internalNoAccess, false);

        expectResult(advertiserAllAccess1, true);
        expectResult(advertiserManagerAllAccess1, true);
        expectResult(advertiserManagerAllAccess2, false);

        doCheck(callCanSwitch);
    }
}
