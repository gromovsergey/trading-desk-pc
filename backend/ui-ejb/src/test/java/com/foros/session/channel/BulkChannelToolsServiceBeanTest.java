package com.foros.session.channel;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.TooManyRowsException;
import com.foros.session.UploadContext;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.ExpressionChannelTestFactory;
import com.foros.util.UploadUtils;
import com.foros.util.expression.ExpressionHelper;

import group.Bulk;
import group.Db;
import group.Validation;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Bulk.class, Validation.class })
public class BulkChannelToolsServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    public BulkChannelToolsService bulkChannelToolsService;

    @Autowired
    public BehavioralChannelTestFactory behavioralChannelTestFactory;

    @Autowired
    public ExpressionChannelTestFactory expressionChannelTestFactory;

    @Test
    public void testValidateExpressionChannel() throws IOException {
        ExpressionChannel channel = expressionChannelTestFactory.createPersistent();

        entityManager.flush();
        entityManager.clear();
        channel.setStatus(Status.PENDING_INACTIVATION);
        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.EXPRESSION, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());


        Collection<Channel> validated = bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
        assertNotNull(validated);
        assertEquals(1, validated.size());
        ExpressionChannel validatedChannel = (ExpressionChannel) validated.iterator().next();
        assertTrue(UploadUtils.getUploadContext(validatedChannel).getWrongPaths().contains("expression"));
        assertTrue(UploadUtils.getUploadContext(validatedChannel).getWrongPaths().contains("status"));
    }

    @Test
    public void testValidateExpressionValid() throws IOException {
        ExpressionChannel channel = expressionChannelTestFactory.createPersistent();
        BehavioralChannel behavioralChannel = behavioralChannelTestFactory.createPersistent(channel.getAccount());

        channel.setExpression(ExpressionHelper.getEditableHumanName(behavioralChannel));

        entityManager.flush();
        entityManager.clear();

        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.EXPRESSION, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(0, validationResultTO.getLineWithErrors());
        bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
    }

    @Test
    public void testValidateExpressionInvalidChannel() throws IOException {
        ExpressionChannel channel = expressionChannelTestFactory.createPersistent();
        BehavioralChannel behavioralChannel = behavioralChannelTestFactory.createPersistent();

        channel.setExpression(ExpressionHelper.getEditableHumanName(behavioralChannel));

        entityManager.flush();
        entityManager.clear();

        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.EXPRESSION, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());

        Collection<Channel> validated = bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
        assertNotNull(validated);
        ExpressionChannel validatedChannel = (ExpressionChannel) validated.iterator().next();
        assertEquals("expression", UploadUtils.getUploadContext(validatedChannel).getWrongPaths().iterator().next());
    }

    @Test
    public void testValidateExpressionInvalidFormat() throws IOException {
        ExpressionChannel channel = expressionChannelTestFactory.createPersistent();
        BehavioralChannel behavioralChannel = behavioralChannelTestFactory.createPersistent(channel.getAccount());

        channel.setExpression(ExpressionHelper.formatChannelName(behavioralChannel));

        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.EXPRESSION, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());

        Collection<Channel> validated = bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
        assertNotNull(validated);
        ExpressionChannel validatedChannel = (ExpressionChannel) validated.iterator().next();
        assertEquals("expression", UploadUtils.getUploadContext(validatedChannel).getWrongPaths().iterator().next());
    }

    @Test
    public void testValidateBehavioralChannel() throws IOException {
        BehavioralChannel channel = behavioralChannelTestFactory.createPersistent();
        getEntityManager().clear();
        channel.setName(null);
        channel.setStatus(Status.PENDING_INACTIVATION);
        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.BEHAVIORAL, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());


        Collection<Channel> validated = bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
        assertNotNull(validated);
        assertEquals(1, validated.size());
        BehavioralChannel validatedChannel = (BehavioralChannel) validated.iterator().next();
        assertTrue(UploadUtils.getUploadContext(validatedChannel).getWrongPaths().contains("name"));
        assertTrue(UploadUtils.getUploadContext(validatedChannel).getWrongPaths().contains("status"));
    }

    @Test
    public void testValidateDuplicateChannel() throws IOException {
        ExpressionChannel expressionChannel = expressionChannelTestFactory.createPersistent();
        Channel channel = behavioralChannelTestFactory.createPersistent(expressionChannel.getAccount());
        channel.setName(expressionChannel.getName());
        clearContext();
        channel.setStatus(Status.PENDING_INACTIVATION);
        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.BEHAVIORAL, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());

        Collection<Channel> validated = bulkChannelToolsService.getValidatedResults(validationResultTO.getId());
        assertNotNull(validated);
        assertEquals(1, validated.size());
        Channel validatedChannel = validated.iterator().next();
        assertTrue(UploadUtils.getUploadContext(validatedChannel).getWrongPaths().contains("name"));
    }

    @Test
    public void testFindForExport() throws TooManyRowsException {
        Channel found = behavioralChannelTestFactory.createPersistent();
        entityManager.clear();

        Collection<? extends Channel> channels = bulkChannelToolsService.findForExport(
                found.getAccount().getId(), AdvertisingChannelType.BEHAVIORAL, Collections.singleton(found.getId()), 2);

        assertNotNull(channels);
        assertFalse(channels.isEmpty());


        found = expressionChannelTestFactory.createPersistent();
        entityManager.clear();

        channels = bulkChannelToolsService.findForExport(
                found.getAccount().getId(), AdvertisingChannelType.EXPRESSION, Collections.singleton(found.getId()), 2);

        assertNotNull(channels);
        assertFalse(channels.isEmpty());

    }

    @Test
    public void testValidateFatalEntities() throws IOException {
        ExpressionChannel channel = expressionChannelTestFactory.createPersistent();
        entityManager.clear();

        channel.setName(null);
        channel.setCountry(null);

        UploadContext currentStatus = new UploadContext();
        currentStatus.addFatal("name");
        channel.setProperty(UPLOAD_CONTEXT, currentStatus);


        ValidationResultTO validationResultTO = bulkChannelToolsService.validateAll(AdvertisingChannelType.EXPRESSION, Arrays.asList(channel));

        assertNotNull(validationResultTO);
        assertNotNull(validationResultTO.getId());
        assertEquals(1, validationResultTO.getLineWithErrors());
    }
}

