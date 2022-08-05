package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Validation;

import org.joda.time.DateTimeConstants;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class BehavioralParametersValidationsTest extends AbstractValidationsTest {

    private static final long DAYS_179 = DateTimeConstants.SECONDS_PER_DAY * 179;
    private static final long DAYS_180 = DateTimeConstants.SECONDS_PER_DAY * 180;
    private static final long DAYS_181 = DateTimeConstants.SECONDS_PER_DAY * 181;

    @Autowired
    private BehavioralParametersValidations behavioralParametersValidations;

    @Test
    public void testValidateBehavioralParams() {
        BehavioralChannel channel = new BehavioralChannel();
        validate(channel);
        assertViolationsCount(0);

        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), DAYS_179, DAYS_180, 1L);
        validate(channel);
        assertViolationsCount(0);

        // from = to
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), 60L, 60L, 1L);
        validate(channel);
        assertViolationsCount(1);

        // > one minute
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), 60L, 90L, 1L);
        validate(channel);
        assertHasViolation("behavioralParameters[P]");
        assertViolationsCount(1);

        // > one hour
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), 600L, 300L, 1L);
        validate(channel);
        assertHasViolation("behavioralParameters[P]");
        assertViolationsCount(1);

        // > 180 days
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), DAYS_179, DAYS_181, 1L);
        validate(channel);
        assertHasViolation("behavioralParameters[P].timeTo");
        assertViolationsCount(1);

        // parameters < 0
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), -300L, -3600L, 1L);
        validate(channel);
        assertHasViolation("behavioralParameters[P].timeFrom","behavioralParameters[P].timeTo");
        assertViolationsCount(2);

        // minimumVisits < 0
        channel = createBehavioralChannel(TriggerType.PAGE_KEYWORD.getLetter(), 60L, 120L, -1L);
        validate(channel);
        assertHasViolation("behavioralParameters[P].minimumVisits");
        assertViolationsCount(1);

        // minimumVisits = 1 and time = 0
        channel = createBehavioralChannel(TriggerType.URL.getLetter(), 0L, 0L, 2L);
        validate(channel);
        assertHasViolation("behavioralParameters[U].minimumVisits");
        assertViolationsCount(1);

    }

    private void validate(BehavioralChannel channel) {
        ValidationContext context = createUpdateContext(channel);
        behavioralParametersValidations.validate(context, channel);
        violations = context.getConstraintViolations();
    }

    private BehavioralChannel createBehavioralChannel(Character triggerType, Long timeFrom, Long timeTo, Long minimumVisits) {
        BehavioralChannel channel = new BehavioralChannel();
        BehavioralParameters behavioralParameters = new BehavioralParameters();
        behavioralParameters.setTimeFrom(timeFrom);
        behavioralParameters.setTimeTo(timeTo);
        behavioralParameters.setTriggerType(triggerType);
        behavioralParameters.setChannel(channel);
        behavioralParameters.setMinimumVisits(minimumVisits);
        channel.getBehavioralParameters().add(behavioralParameters);
        return channel;
    }
}
