package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.service.mock.MockValidationServiceBean;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.channel.service.BulkChannelService;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.util.RandomUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationMode;

import group.Db;
import group.Validation;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class BulkChannelValidationsTest extends AbstractValidationsTest {

    @Autowired
    private MockValidationServiceBean mockValidationServiceBean;


    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private BulkChannelValidations bulkChannelValidations;

    @Autowired
    private BulkChannelService bulkChannelService;

    Account account;

    @Test
    public void testValidateMerge() {
        Operations<Channel> channelOperations = null;
        channelOperations = generateOperationList(generateUpdateOperation(), channelOperations);
        channelOperations = generateOperationList(generateUpdateOperation(), channelOperations);
        assertValidateOperations(channelOperations);
    }

    @Test
    public void testPathExistsOnUniqueConstraintViolationCreate() {
        BehavioralChannel existing = behavioralChannelTF.createPersistent();
        account = existing.getAccount();

        mockValidationServiceBean.savePoint();

        Operations<Channel> createChannelOperations = generateOperationList(generateSameNameCreateOperation(existing), null);
        assertValidateOperations(createChannelOperations);
        assertPerformOperations(createChannelOperations);

    }

    @Test
    public void testPathExistsOnUniqueConstraintViolationUpdate() {
        BehavioralChannel existing = behavioralChannelTF.createPersistent();
        account = existing.getAccount();

        mockValidationServiceBean.savePoint();

        Operations<Channel> updateChannelOperations = generateOperationList(generateSameNameUpdateOperation(existing), null);
        assertValidateOperations(updateChannelOperations);
        assertPerformOperations(updateChannelOperations);
    }

    private Operations<Channel> generateOperationList(Operation<Channel> operation, Operations<Channel> operations) {
        List<Operation<Channel>> operationsList = operations == null ? new LinkedList<Operation<Channel>>() :
                operations.getOperations();
        operationsList.add(operation);

        Operations<Channel> channelOperations = operations == null ? new  Operations<Channel>() : operations;
        channelOperations.setOperations(operationsList);

        return channelOperations;
    }

    private Operation<Channel> generateUpdateOperation() {
        Operation<Channel> result = new Operation<Channel>();

        BehavioralChannel existing = account == null ? behavioralChannelTF.createPersistent() :
                behavioralChannelTF.createPersistent(account);
        account = existing.getAccount();
        BehavioralChannel channel = new BehavioralChannel();
        channel.setId(existing.getId());
        //Test situation, when name is not provided
        //channel.setName(existing.getName());
        //Test situation, when account is not provided
        channel.setAccount(new AdvertiserAccount());

        //Providing id and description (modified) only
        channel.setDescription(existing.getDescription() + ": modified");

        result.setEntity(channel);
        result.setOperationType(OperationType.UPDATE);

        return result;
    }

    private Operation<Channel> generateSameNameUpdateOperation(Channel src) {
        Operation<Channel> result = generateUpdateOperation();
        result.getEntity().setName(src.getName());
        return result;
    }

    private Operation<Channel> generateCreateOperation(Channel src) {
        Operation<Channel> result = new Operation<Channel>();

        BehavioralChannel channel = new BehavioralChannel();
        channel.setAccount(account);
        channel.setName(RandomUtil.getRandomString() + Long.valueOf(System.currentTimeMillis()));
        channel.setCountry(src.getCountry());
        channel.setDescription("test");

        result.setEntity(channel);
        result.setOperationType(OperationType.CREATE);

        return result;
    }

    private Operation<Channel> generateSameNameCreateOperation(Channel src) {
        Operation<Channel> result = generateCreateOperation(src);
        result.getEntity().setName(src.getName());
        return result;
    }

    private void assertValidateOperations(Operations<Channel> operations) {
        ValidationContext context = createContext(null, ValidationMode.DEFAULT);
        bulkChannelValidations.validateMerge(context, operations);
        violations = context.getConstraintViolations();
        assertViolationsCount(0);
    }

    private void assertPerformOperations(Operations<Channel> operations) {
        try {
            bulkChannelService.perform(operations);
            commitChanges();
        } catch (Exception e) {
            if (! (e instanceof ConstraintViolationException)) {
                assertTrue("ConstraintViolationException is expected.", false);
            }
            violations = ((ConstraintViolationException)e).getConstraintViolations();
            assertViolationsCount(1);
            if ( !violations.iterator().next().getPropertyPath().toString().equals("operations[0].behavioralChannel.name") ) {
                assertTrue("The path must contain index of operation and behavioralChannel.name. Actual: " +
                        violations.iterator().next().getPropertyPath().toString(), false);
            }
            return;
        }
        assertTrue("ConstraintViolationException is expected.", false);
    }
}
