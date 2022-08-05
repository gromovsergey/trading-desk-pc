package com.foros.session.channel.service;

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
public class ChannelMatchTestRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private ChannelMatchTestRestrictions channelMatchTestRestrictions;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    private UserDefinition internalWithAdvertiserChannelsViewOnly;

    private UserDefinition internalWithDiscoverChannelsViewOnly;

    private UserDefinition internalWithoutChannelsView;

    @Override
    @org.junit.Before
    public void setUp() throws Exception {
        super.setUp();

        internalWithAdvertiserChannelsViewOnly = userDefinitionFactory.create(AccountRole.INTERNAL)
            .addCustomPermission(new PolicyEntry("channel_match_test", "run")).addCustomPermission(
                new PolicyEntry("advertiser_advertising_channel", "view"));

        internalWithDiscoverChannelsViewOnly = userDefinitionFactory.create(AccountRole.INTERNAL).addCustomPermission(
            new PolicyEntry("channel_match_test", "run")).addCustomPermission(
            new PolicyEntry("discoverChannel", "view"));

        internalWithoutChannelsView = userDefinitionFactory.create(AccountRole.INTERNAL).addCustomPermission(
            new PolicyEntry("channel_match_test", "run"));
    }

    @Test
    public void testCanRun() throws Exception {
        Callable callCanRun = new Callable("channel_match_test", "run") {
            @Override
            public boolean call() {
                return channelMatchTestRestrictions.canRun();
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalNoAccess, false);
        expectResult(advertiserAllAccess1, false);

        expectResult(internalWithAdvertiserChannelsViewOnly, true);
        expectResult(internalWithDiscoverChannelsViewOnly, true);
        expectResult(internalWithoutChannelsView, false);

        doCheck(callCanRun);
    }
}
