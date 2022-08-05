package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.Country;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.test.factory.DiscoverChannelListTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.util.StringUtil;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class DiscoverChannelValidationsTest extends AbstractValidationsTest {
    @Autowired
    private DiscoverChannelTestFactory discoverChannelTestFactory ;

    @Autowired
    private DiscoverChannelListTestFactory discoverChannelListTestFactory;

    @Autowired
    private InternalAccountTestFactory accountTF ;

    private InternalAccount account;
    private DiscoverChannel existing;
    private DiscoverChannel channel;
    private String validationName;

    @Test
    public void testValidate() {
        account = accountTF.createPersistent();
        existing = discoverChannelTestFactory.createPersistent(account);

        validationName = "DiscoverChannel.update";
        channel = good();
        doValidate();
        assertViolationsCount(0);

        validateAll();
        validateChannelList();

        validationName = "DiscoverChannel.create";
        channel = good();
        doValidate();
        assertViolationsCount(0);

        validateAll();
    }

    @Test
    public void testValidateMergeWithEmptyAccount(){
        Operations<DiscoverChannel> ops = new Operations<DiscoverChannel>();

        DiscoverChannel bad = new DiscoverChannel();
        Operation<DiscoverChannel> op = new Operation<DiscoverChannel>();
        op.setEntity(bad);
        op.setOperationType(OperationType.CREATE);
        ops.getOperations().add(op);

        DiscoverChannel good = new DiscoverChannel();
        InternalAccount internal = accountTF.createPersistent();
        good.setAccount(new InternalAccount(internal.getId()));
        good.setName("name");
        good.setDiscoverQuery("query");
        good.setDiscoverAnnotation("annotation");
        good.setCountry(new Country("GB"));
        op = new Operation<DiscoverChannel>();
        op.setEntity(good);
        op.setOperationType(OperationType.CREATE);
        ops.getOperations().add(op);

        validate("DiscoverChannel.merge", ops);
        assertViolationsCount(1);
        assertHasViolation("operations[0].discoverChannel.account");
    }

    private void validateAll() {
        validateDiscoverQuery();
        validateDiscoverAnnotation();
        validateLanguage();
        validateCountry();
    }

    private void validateDiscoverAnnotation() {
        channel = good();
        channel.setDiscoverAnnotation(StringUtil.replicate('q', 4001));
        validate("DiscoverChannel.update", channel);
        assertHasViolation("discoverAnnotation");
        assertViolationsCount(1);
    }

    private void validateDiscoverQuery() {
        channel = good();
        channel.setDiscoverQuery(StringUtil.replicate('q', 4001));
        validate("DiscoverChannel.update", channel);
        assertHasViolation("discoverQuery");
        assertViolationsCount(1);

        channel = good();
        channel.setDiscoverQuery("with line \n break");
        validate("DiscoverChannel.update", channel);
        assertHasViolation("discoverQuery");
        assertViolationsCount(1);

        channel = good();
        channel.setDiscoverQuery("with line \r break");
        validate("DiscoverChannel.update", channel);
        assertHasViolation("discoverQuery");
        assertViolationsCount(1);

        channel = good();
        channel.setDiscoverQuery("with line \r\n break");
        validate("DiscoverChannel.update", channel);
        assertHasViolation("discoverQuery");
        assertViolationsCount(1);
    }

    private void validateLanguage() {
        channel = good();
        channel.setLanguage("!@#");
        validate("DiscoverChannel.update", channel);
        assertHasViolation("language");

        channel = good();
        channel.setLanguage("ru");
        validate("DiscoverChannel.update", channel);
        assertViolationsCount(0);

        channel = good();
        channel.setLanguage(null);
        validate("DiscoverChannel.update", channel);
        assertHasViolation("language");
    }

    private void validateCountry() {
        channel = good();
        channel.setCountry(null);
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("country");

        channel.setCountry(new Country(""));
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("country.countryCode");

        channel.setCountry(new Country("GB"));
        doValidate();
        assertViolationsCount(0);

        channel.setCountry(new Country("ZZZ"));
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("country.countryCode");
    }

    private void validateChannelList() {
        channel = good();
        channel.setChannelList(discoverChannelListTestFactory.createPersistent());
        doValidate();
        assertViolationsCount(0);

        existing.setChannelList(discoverChannelListTestFactory.createPersistent());
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("channelList");
        existing.setChannelList(null);
    }

    private void doValidate() {
        validate(validationName, channel);
    }

    private DiscoverChannel good() {
        DiscoverChannel dc = new DiscoverChannel();
        if (validationName.equals("DiscoverChannel.update")) {
            dc.setId(existing.getId());
        } else if (validationName.equals("DiscoverChannel.create")) {
            dc.setName("name");
            dc.setDiscoverQuery("query");
            dc.setDiscoverAnnotation("annotation");
            dc.setAccount(new InternalAccount(account.getId()));
            dc.setCountry(new Country("GB"));
            dc.setLanguage("en");
        }
        return dc;
    }
}
