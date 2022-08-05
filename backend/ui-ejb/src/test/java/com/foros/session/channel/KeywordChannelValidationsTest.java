package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import group.Db;
import group.Validation;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class KeywordChannelValidationsTest extends AbstractValidationsTest {
    @Autowired
    private BehavioralParamsTestFactory behavioralParamsTF;

    @Test
    public void testUpdateAllInvalidNoParams() throws Exception {
        List<KeywordChannel> channels = new ArrayList<KeywordChannel>();
        channels.add(createKeywordChannel());

        try {
            validationService.validate("KeywordChannel.updateAll", channels).throwIfHasViolations();
            fail("Invalid behavioral parameters should not be allowed!");
        } catch (ConstraintViolationException e) {
            assertEquals(1, e.getConstraintViolations().size());
        }
    }

    @Test
    public void testUpdateAllInvalidWrongParams() throws Exception {
        List<KeywordChannel> channels = new ArrayList<KeywordChannel>();
        channels.add(createKeywordChannel());
        channels.add(createKeywordChannel());

        for (KeywordChannel channel : channels) {
            channel.getBehavioralParameters().add(behavioralParamsTF.createBParam('S', 62L, 120L));
        }

        try {
            validationService.validate("KeywordChannel.updateAll", channels).throwIfHasViolations();
            fail("Invalid behavioral parameters should not be allowed!");
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
        }

        for (KeywordChannel channel : channels) {
            channel.getBehavioralParameters().clear();
            channel.getBehavioralParameters().add(behavioralParamsTF.createBParam('S', 240L, 120L));
        }

        try {
            validationService.validate("KeywordChannel.updateAll", channels).throwIfHasViolations();
            fail("Invalid behavioral parameters should not be allowed!");
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
        }
    }

    private KeywordChannel createKeywordChannel() {
        Account account = new GenericAccount();
        account.setName("account");
        account.setCountry(new Country("GB"));
        KeywordChannel keywordChannel = new KeywordChannel();
        keywordChannel.setTriggerType(KeywordTriggerType.SEARCH_KEYWORD);
        keywordChannel.setAccount(account);
        keywordChannel.setName("channel");
        return keywordChannel;
    }
}
