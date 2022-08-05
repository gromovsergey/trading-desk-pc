package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.ChannelRate;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.bulk.OperationType;
import com.foros.session.channel.service.BehavioralChannelServiceBean;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;

import group.Db;
import group.Validation;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


@Category({ Db.class, Validation.class })
public class AdvertisingChannelValidationsTest extends AbstractValidationsTest {

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private CmpAccountTestFactory cmpAccountTF;

    @Autowired
    private AdvertisingChannelValidations advertisingChannelValidations;

    @Autowired
    private BehavioralChannelServiceBean behavioralChannelServiceBean;

    private BehavioralChannel existing;
    private BehavioralChannel channel;
    private OperationType operationType;
    private Account account;

    @Test
    public void testAdvertiser() {
        account = advertiserAccountTF.createPersistent();
        // UPDATE
        operationType = OperationType.UPDATE;
        existing = behavioralChannelTF.createPersistent(account);
        channel = good();

        validate();
        assertViolationsCount(0);

        validateDescription();
        validateName();
        validateSupersededByUpdate();
        validateAdvertiserVisibility();

        // CREATE
        operationType = OperationType.CREATE;
        existing = null;

        channel = good();
        validate();
        assertViolationsCount(0);

        validateDescription();
        validateName();
        validateCountryCreate();
        validateSupersededByCreate();
        validateAdvertiserVisibility();
    }

    @Test
    public void testCmp() {
        account = cmpAccountTF.createPersistent();
        existing = behavioralChannelTF.createPersistent(account);
        assertEquals(ChannelVisibility.PRI, existing.getVisibility());
        operationType = OperationType.UPDATE;

        channel = good();
        validate();
        assertViolationsCount(0);

        channel = good();
        validate();
        assertViolationsCount(0);

        // make it CMP
        channel = goodCmp();
        commitChanges();
        ChannelRate rate = new ChannelRate();
        rate.setRate(new BigDecimal("999999.00"), RateType.CPM);
        existing.setChannelRate(rate);
        behavioralChannelServiceBean.submitToCmp(existing);
        entityManager.flush();
        existing = behavioralChannelTF.refresh(existing);

        //Test wrong status for cmp channel
        channel.setStatus(Status.INACTIVE);
        validate();
        assertViolationsCount(2);
        assertHasViolation("status");

        channel.setStatus(Status.ACTIVE);
        channel.getChannelRate().setRate(new BigDecimal("999.00"), RateType.CPM);
        validate();
        assertViolationsCount(0);

        channel.getChannelRate().setRateType(RateType.CPA);
        validate();
        assertViolationsCount(1);
        assertHasViolation("channelRate.rateType");

        // CREATE CMP WITH Bad Status
        operationType = OperationType.CREATE;
        existing = null;

        channel = good();
        channel.setStatus(Status.PENDING_INACTIVATION);
        validate();
        assertViolationsCount(1);
        assertHasViolation("status");
    }

    @Test
    public void testSubmitToCmp() {
        account = cmpAccountTF.createPersistent();
        existing = behavioralChannelTF.createPersistent(account);
        assertEquals(ChannelVisibility.PRI, existing.getVisibility());

        channel = goodCmp();
        validateSubmit();
        assertViolationsCount(0);

        // wrong channel rate type
        channel = goodCmp();
        channel.getChannelRate().setRateType(RateType.CPA);
        validateSubmit();
        assertViolationsCount(1);
        assertHasViolation("channelRate.rateType");

        // channel rate is zero
        channel = goodCmp();
        channel.getChannelRate().setCpc(BigDecimal.ZERO);
        validateSubmit();
        assertViolationsCount(1);
        assertHasViolation("channelRate.value");

        // negative channel rate
        channel = goodCmp();
        channel.getChannelRate().setCpc(new BigDecimal("-10"));
        validateSubmit();
        assertViolationsCount(1);
        assertHasViolation("channelRate.value");

        // null channel rate
        channel = goodCmp();
        channel.setChannelRate(null);
        validateSubmit();
        assertViolationsCount(1);
        assertHasViolation("channelRate");

        // nothing is set (except id)
        channel = goodCmp();
        channel.setChannelRate(null);
        channel.unregisterChanges();
        validateSubmit();
        assertViolationsCount(1);
        assertHasViolation("channelRate");
    }

    private BehavioralChannel goodCmp() {
        BehavioralChannel ch = new BehavioralChannel();
        ch.setId(existing.getId());
        ChannelRate rate = new ChannelRate();
        rate.setRate(new BigDecimal("999.00"), RateType.CPC);
        ch.setChannelRate(rate);
        return ch;
    }

    private void validateAdvertiserVisibility() {
        channel = good();

        channel.setVisibility(ChannelVisibility.PRI);
        validate();
        assertViolationsCount(0);

        channel.setVisibility(null);
        validate();
        assertViolationsCount(1);
        assertHasViolation("visibility");

        channel.setVisibility(ChannelVisibility.CMP);
        validate();
        assertViolationsCount(1);
        assertHasViolation("visibility");

        channel.setVisibility(ChannelVisibility.PUB);
        validate();
        assertViolationsCount(1);
        assertHasViolation("visibility");
    }

    private void validateSupersededByCreate() {
        channel = good();

        BehavioralChannel ch = new BehavioralChannel();
        ch.setId(1L);
        channel.setSupersededByChannel(ch);
        validate();
        assertViolationsCount(1);
        assertHasViolation("supersededByChannel");

        channel.setSupersededByChannel(null);
        validate();
        assertViolationsCount(0);
    }

    private void validateSupersededByUpdate() {
        channel = good();

        BehavioralChannel ch = behavioralChannelTF.createPersistent(account);
        BehavioralChannel supersededBy = new BehavioralChannel();
        supersededBy.setId(ch.getId());

        channel.setSupersededByChannel(supersededBy);
        validate();
        assertViolationsCount(0);

        // null id
        channel.setSupersededByChannel(new BehavioralChannel());
        validate();
        assertViolationsCount(1);

        // non-existent id
        supersededBy.setId(Long.MAX_VALUE);
        channel.setSupersededByChannel(supersededBy);
        validate();
        assertViolationsCount(1);
        assertHasViolation("supersededByChannel.id");

        // wrong country & account
        ch.setAccount(new AdvertiserAccount(Long.MAX_VALUE));
        ch.setCountry(new Country("RU"));
        channel.setSupersededByChannel(ch);
        validate();
        assertViolationsCount(2);
        assertHasViolation("supersededByChannel");
        ch.setAccount(existing.getAccount());
        ch.setCountry(existing.getCountry());

        // self
        channel.setSupersededByChannel(channel);
        validate();
        assertViolationsCount(1);
        assertHasViolation("supersededByChannel");

        // deleted
        ch.setStatus(Status.DELETED);
        channel.setSupersededByChannel(ch);
        validate();
        assertViolationsCount(1);
        assertHasViolation("supersededByChannel");
    }

    private void validateCountryCreate() {
        channel = good();

        channel.setCountry(null);
        validate();
        assertViolationsCount(1);
        assertHasViolation("country");

        // null code
        channel.setCountry(new Country());
        validate();
        assertViolationsCount(1);
        assertHasViolation("country.countryCode");

        // bad code
        channel.setCountry(new Country("BAD"));
        validate();
        assertViolationsCount(1);
        assertHasViolation("country.countryCode");

    }

    private void validateDescription() {
        channel = good();

        channel.setDescription(null);
        validate();
        assertViolationsCount(0);

        channel.setDescription("");
        validate();
        assertViolationsCount(0);

        channel.setDescription(StringUtils.repeat("1", 2000));
        validate();
        assertViolationsCount(0);

        channel.setDescription(StringUtils.repeat("1", 2001));
        validate();
        assertViolationsCount(1);
        assertHasViolation("description");
    }

    private void validateName() {
        channel = good();

        channel.setName("name");
        validate();
        assertViolationsCount(0);

        channel.setName(null);
        validate();
        assertViolationsCount(1);
        assertHasViolation("name");

        channel.setName("");
        validate();
        assertViolationsCount(1);
        assertHasViolation("name");

        channel.setName(StringUtils.repeat("1", 100));
        validate();
        assertViolationsCount(0);

        channel.setName(StringUtils.repeat("1", 101));
        validate();
        assertViolationsCount(1);
        assertHasViolation("name");

        char[] forbiddenChars = "][|><".toCharArray();
        for (char forbiddenChar : forbiddenChars) {
            channel.setName(String.valueOf(forbiddenChar));
            validate();
            assertViolationsCount(1);
            assertHasViolation("name");
        }
    }

    private void validate() {
        ValidationContext context = ValidationUtil
                .validationContext(channel)
                .withMode(operationType.toValidationMode())
                .build();

        advertisingChannelValidations.validate(context, channel, existing);
        violations = context.getConstraintViolations();
    }

    private void validateSubmit() {
        ValidationContext context = createContext(channel);
        advertisingChannelValidations.validateSubmitToCmp(context, channel);
        violations = context.getConstraintViolations();
    }

    private BehavioralChannel good() {
        BehavioralChannel ch;
        if (operationType == OperationType.CREATE) {
            ch = new BehavioralChannel();
            ch.setName("name");
            ch.setCountry(new Country(account.getCountry().getCountryCode()));
        } else {
            ch = new BehavioralChannel();
            ch.setId(existing.getId());
        }
        ch.setAccount(account);
        return ch;
    }
}
