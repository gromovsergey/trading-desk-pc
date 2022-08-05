package com.foros.session.admin.bannedChannel;

import com.foros.AbstractValidationsTest;
import com.foros.model.channel.BannedChannel;
import com.foros.session.channel.TriggerListValidationRules;
import com.foros.util.StringUtil;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class BannedChannelValidationsTest extends AbstractValidationsTest {
    private BannedChannel bannedChannel = new BannedChannel();
    private TriggerListValidationRules rules = TriggerListValidationRules.DEFAULT_RULES;

    @Test
    public void testValidate() {
        bannedChannel.setId(BannedChannel.NO_ADV_CHANNEL_ID);
        prepareTriggerLists("keyword", "google.com");
        validate("BannedChannel.update", bannedChannel);
        assertViolationsCount(0);

        // keyword too long
        prepareTriggerLists(StringUtil.replicate('z', (int)(rules.getMaxKeywordLength() + 1)), null);
        validate("BannedChannel.update", bannedChannel);
        assertHasViolation("pageKeywords.positive[0]");
        assertHasViolation("urlKeywords.positive[0]");
        assertViolationsCount(2);

        // longest possible url
        String host = "g.com/";
        prepareTriggerLists(null, host + StringUtil.replicate('z', (int)(rules.getMaxUrlLength() - host.length())));
        validate("BannedChannel.update", bannedChannel);
        assertViolationsCount(0);

        // url too long
        prepareTriggerLists(null, host + StringUtil.replicate('z', (int)(rules.getMaxUrlLength() - host.length() + 1)));
        validate("BannedChannel.update", bannedChannel);
        assertHasViolation("urls.positive[0]");
        assertViolationsCount(1);

        // invalid url
        prepareTriggerLists(null, "invalid url");
        validate("BannedChannel.update", bannedChannel);
        assertHasViolation("urls.positive[0]");
        assertViolationsCount(1);
    }

    private void prepareTriggerLists(String keywords, String urls) {
        prepareTriggerLists(keywords, urls, keywords);
    }

    private void prepareTriggerLists(String keywords, String urls, String urlKeywords) {
        bannedChannel.getPageKeywords().setPositiveString(keywords);
        bannedChannel.getUrls().setPositiveString(urls);
        bannedChannel.getUrlKeywords().setPositiveString(urlKeywords);
    }
}

