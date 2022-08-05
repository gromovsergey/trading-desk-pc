package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.bulk.OperationType;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


@Category({ Db.class, Validation.class })
public class AudienceChannelValidationsTest  extends AbstractValidationsTest {

    @Autowired
    private AudienceChannelTestFactory audienceChannelTF;

    @Autowired
    private AudienceChannelValidations audienceChannelValidations;

    @Autowired
    private InternalAccountTestFactory internalAccountTestFactory;

    private OperationType operationType;
    private AudienceChannel channel;
    private AudienceChannel existing;

    @Test
    public void testValidateUpdate() {
        existing = audienceChannelTF.refresh(audienceChannelTF.createPersistent());
        getEntityManager().clear();

        operationType = OperationType.UPDATE;
        channel = new AudienceChannel();
        channel.setId(existing.getId());

        channel.setName(existing.getName() + "2");
        channel.setVisibility(ChannelVisibility.PUB);

        doValidate();
        assertViolationsCount(0);

        channel.setVisibility(ChannelVisibility.CMP);
        channel.setAccount(existing.getAccount());
        doValidate();
        assertViolationsCount(1);
    }

    @Test
    public void testValidateCreate() {
        InternalAccount account = internalAccountTestFactory.createPersistent();
        existing = null;
        operationType = OperationType.CREATE;
        channel = new AudienceChannel();
        channel.setName(audienceChannelTF.getTestEntityRandomName());
        channel.setCountry(account.getCountry());
        channel.setAccount(new InternalAccount(account.getId()));
        doValidate();
        assertViolationsCount(0);

        channel.setVisibility(ChannelVisibility.PUB);
        doValidate();
        assertViolationsCount(0);
    }

    private void doValidate() {
        ValidationContext context = createContext(channel, operationType.toValidationMode());
        switch (operationType) {
            case CREATE:
                audienceChannelValidations.validateCreate(context, channel);
                break;
            case UPDATE:
                audienceChannelValidations.validateUpdate(context, channel);
        }
        violations = context.getConstraintViolations();
    }
}
