package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class BaseTriggerListValidationsTest extends AbstractValidationsTest {

    @Autowired
    private BaseTriggerListValidations triggerListValidations;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTestFactory;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    private BehavioralChannel channel;
    private TriggersChannel existing;
    private Collection<BehavioralParameters> effectiveParameters;

    private AdvertiserAccount account;

    @Test
    public void testValidate() {
        account = advertiserAccountTF.createPersistent();
        TriggerListValidationRules rules = new TriggerListValidationRules(account);
        channel = createBehavioralChannel("keyword", "google.com");
        doValidate();
        assertViolationsCount(0);

        // keyword too long
        channel = createBehavioralChannel(StringUtil.replicate('z', (int)(rules.getMaxKeywordLength() + 1)), null);
        doValidate();
        assertHasViolation("pageKeywords.positive[0]");
        assertHasViolation("searchKeywords.positive[0]");
        assertViolationsCount(2);

        String host = "g.com/";

        // longest possible url
        channel = createBehavioralChannel(null, host + StringUtil.replicate('z', (int)(rules.getMaxUrlLength() - host.length())));
        doValidate();
        assertViolationsCount(0);

        // longest possible negative url
        channel = createBehavioralChannel(null, null);
        channel.getUrls().setNegative(host + StringUtil.replicate('z', (int) (rules.getMaxUrlLength() - host.length())));
        doValidate();
        assertViolationsCount(0);

        // url too long
        channel = createBehavioralChannel(null, host + StringUtil.replicate('z', (int)(rules.getMaxUrlLength() - host.length() + 1)));
        doValidate();
        assertHasViolation("urls.positive[0]");
        assertViolationsCount(1);

        // invalid url
        channel = createBehavioralChannel(null, "invalid url");
        doValidate();
        assertHasViolation("urls.positive[0]");
        assertViolationsCount(1);

        // invalid url
        channel = createBehavioralChannel(null, "-" + host);
        doValidate();
        assertHasViolation("urls.positive[0]");
        assertViolationsCount(1);

        // quotes
        channel = createBehavioralChannel("middle \" quote", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("middle \\\" quote", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("middle \\\\\" quote", null);
        doValidate();
        assertViolationsCount(2);

        //square brackets
        channel = createBehavioralChannel("]some text[", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("some[ggg]text", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("some [ggg] text", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("[some text", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("some text]", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("[[some text]", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("[some text]]", null);
        doValidate();
        assertViolationsCount(2);

        channel = createBehavioralChannel("[[some text]]", null);
        doValidate();
        assertViolationsCount(2);

        //valid only for search keyword
        channel = createBehavioralChannel("[some text]", null);
        doValidate();
        assertViolationsCount(1);
    }

    @Test
    public void testValidateTriggersRequired() {
        account = advertiserAccountTF.createPersistent();
        existing = behavioralChannelTestFactory.createPersistent();
        existing.setPageKeywords(null);
        existing.setSearchKeywords(null);
        existing.setUrls(null);

        // no params and both triggers
        channel = createBehavioralChannel("kw", "url.com");
        effectiveParameters = createParameters();
        doValidateTriggersRequired();
        assertViolationsCount(0);

        // all params and both triggers
        channel = createBehavioralChannel("kw", "url.com");
        effectiveParameters = createParameters(
                TriggerType.URL.getLetter(),
                TriggerType.PAGE_KEYWORD.getLetter(),
                TriggerType.SEARCH_KEYWORD.getLetter()
        );
        doValidateTriggersRequired();
        assertViolationsCount(0);

        // no params and no triggers
        channel = createBehavioralChannel(null, null);
        effectiveParameters = null;
        doValidateTriggersRequired();
        assertViolationsCount(0);

        // both null and required
        channel = createBehavioralChannel(null, null);
        effectiveParameters = createParameters(
                TriggerType.URL.getLetter(),
                TriggerType.PAGE_KEYWORD.getLetter(),
                TriggerType.SEARCH_KEYWORD.getLetter()
        );
        doValidateTriggersRequired();
        assertViolationsCount(3);
        assertHasViolation("pageKeywords");
        assertHasViolation("searchKeywords");
        assertHasViolation("urls");
    }

    private List<BehavioralParameters> createParameters(Character... type) {
        List<BehavioralParameters> list = new ArrayList<BehavioralParameters>();
        for (Character character : type) {
            BehavioralParameters behavioralParameters = new BehavioralParameters();
            behavioralParameters.setTriggerType(character);
            list.add(behavioralParameters);
        }
        return list;
    }

    private BehavioralChannel createBehavioralChannel(String keyword, String urls) {
        keyword = keyword != null ? keyword.trim() : null;
        urls = urls != null ? urls.trim() : null;

        BehavioralChannel channel = new BehavioralChannel();

        if (keyword != null) {
            channel.getPageKeywords().setPositive(keyword);

            channel.getSearchKeywords().setPositive(keyword);
        }

        if (urls != null) {
            channel.getUrls().setPositive(urls);
        }

        channel.setAccount(account);
        if (existing != null) {
            channel.setId(existing.getId());
            channel.setAccount(existing.getAccount());
        }
        return channel;
    }

    private void doValidate() {
        ValidationContext context = createUpdateContext(channel);
        triggerListValidations.validate(context, channel, null);
        violations = context.getConstraintViolations();
    }

    private void doValidateTriggersRequired() {
        ValidationContext context = createUpdateContext(channel);
        triggerListValidations.validateTriggersRequired(context, channel, existing, effectiveParameters);
        violations = context.getConstraintViolations();
    }
}
