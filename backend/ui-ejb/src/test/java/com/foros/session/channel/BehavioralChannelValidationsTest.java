package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.Status;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.bulk.OperationType;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.validation.ValidationContext;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class BehavioralChannelValidationsTest extends AbstractValidationsTest {

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private BehavioralChannelValidations behavioralChannelValidations;

    @Autowired
    private InternalAccountTestFactory internalAccountTestFactory;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTestFactory;

    private OperationType operationType;
    private BehavioralChannel channel;
    private BehavioralChannel existing;

    @Test
    public void testValidateUpdate() {
        existing = behavioralChannelTF.createPersistent();
        operationType = OperationType.UPDATE;
        channel = new BehavioralChannel();
        channel.setId(existing.getId());
        doValidate();
        assertViolationsCount(0);
    }

    @Test
    public void testValidateUpdateCMP() throws Exception {
        existing = behavioralChannelTF.createPersistent();

        behavioralChannelTF.submitToCmp(existing);

        // Keywords
        operationType = OperationType.UPDATE;
        channel = new BehavioralChannel();
        channel.setId(existing.getId());
        channel.setVisibility(ChannelVisibility.CMP);
        channel.unregisterChange("visibility");
        channel.setAccount(existing.getAccount());

        // pageKeywords
        operationType = OperationType.UPDATE;
        channel.getPageKeywords().setPositive("keyword test");
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("pageKeywords");
        channel.setPageKeywords(existing.getPageKeywords());

        // searchKeywords
        operationType = OperationType.UPDATE;
        channel.getSearchKeywords().setPositive("keyword test");
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("searchKeywords");
        channel.setSearchKeywords(existing.getSearchKeywords());

        // urlKeywords
        operationType = OperationType.UPDATE;
        channel.getUrlKeywords().setPositive("keyword test");
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("urlKeywords");
        channel.setUrlKeywords(existing.getUrlKeywords());

        // URLs
        operationType = OperationType.UPDATE;
        channel.getUrls().setPositive("123 www.test.com");
        doValidate();
        assertViolationsCount(1);
        assertHasViolation("urls");
    }

    @Test
    public void testValidateCreate() {
        InternalAccount account = internalAccountTestFactory.createPersistent();
        existing = null;
        operationType = OperationType.CREATE;
        channel = new BehavioralChannel();
        channel.setName(behavioralChannelTF.getTestEntityRandomName());
        channel.setCountry(account.getCountry());
        channel.setAccount(new InternalAccount(account.getId()));
        channel.setLanguage("ru");
        doValidate();
        assertViolationsCount(0);
    }

    @Test
    public void testCreateWithInvalidCategory() {
        testValidateCreate();
        setDeletedObjectsVisible(true);
        CategoryChannel parent = categoryChannelTestFactory.createPersistent();
        parent.setStatus(Status.DELETED);
        CategoryChannel child = categoryChannelTestFactory.createChild(parent);
        channel.getCategories().add(child);

        commitChanges();

        doValidate();
        assertViolationsCount(1);
    }

    private void doValidate() {
        ValidationContext context = createContext(channel, operationType.toValidationMode());
        behavioralChannelValidations.validate(context, channel, existing);
        violations = context.getConstraintViolations();
    }
}
