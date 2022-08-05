package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.session.channel.service.DiscoverChannelUtils;
import com.foros.test.factory.DiscoverChannelListTestFactory;

import java.util.Collections;

import group.Db;
import group.Validation;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class DiscoverChannelListValidationsTest extends AbstractValidationsTest {
    @Autowired
    private DiscoverChannelListTestFactory discoverChannelListTF;

    @Test
    public void testDuplicateKeywords() throws Exception {
        DiscoverChannelList channel = discoverChannelListTF.create();
        channel.setKeywordList("First\nSecond\nFirst");
        DiscoverChannelUtils.createChildChannelsFromKeywordList(channel);

        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(0);
        assertHasNoViolation("childChannels[2]");
    }

    @Test
    public void testNameMacro() throws Exception {
        DiscoverChannelList channel = discoverChannelListTF.create();
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(0);

        // has no ##KEWORD##
        channel.setChannelNameMacro("123");
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(1);
        assertHasViolation("channelNameMacro");

        // too long
        channel.setChannelNameMacro("##KEYWORD##" + StringUtils.repeat("1", 200));
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(1);
        assertHasViolation("channelNameMacro");

        // too long & has no ##KEWORD##
        channel.setChannelNameMacro(StringUtils.repeat("1", 201));
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(2);
        assertHasViolation("channelNameMacro");

        // null
        channel.setChannelNameMacro(null);
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(1);
        assertHasViolation("channelNameMacro");

        // empty
        channel.setChannelNameMacro("");
        validate("DiscoverChannelList.create", channel, Collections.<DiscoverChannel>emptyList());
        assertViolationsCount(1);
        assertHasViolation("channelNameMacro");
    }
}
