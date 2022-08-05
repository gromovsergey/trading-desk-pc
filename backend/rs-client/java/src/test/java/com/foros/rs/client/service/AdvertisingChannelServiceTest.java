package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.channel.AudienceChannel;
import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.advertising.channel.BehavioralParameters;
import com.foros.rs.client.model.advertising.channel.Channel;
import com.foros.rs.client.model.advertising.channel.ChannelSelector;
import com.foros.rs.client.model.advertising.channel.ChannelType;
import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import com.foros.rs.client.model.advertising.channel.Visibility;
import com.foros.rs.client.model.channel.BehavioralParametersTriggerType;
import com.foros.rs.client.model.channel.TriggersType;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class AdvertisingChannelServiceTest extends AbstractUnitTest {
    @Test
    public void testCRUD() throws Exception {
        Operations<Channel> operations = new Operations<Channel>();
        operations.setOperations(Arrays.asList(
                operation(prepareExpression(), OperationType.CREATE),
                operation(prepareBehavioral(), OperationType.CREATE),
                operation(prepareAudience(), OperationType.CREATE)

        ));
        OperationsResult operationsResult = advertisingChannelService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(3, operationsResult.getIds().size());

        // Check it created
        ChannelSelector selector = new ChannelSelector();

        selector.setChannelIds(operationsResult.getIds());
        Result<Channel> result = advertisingChannelService.get(selector);
        assertNotNull(result);
        assertEquals(3, result.getEntities().size());

        // Only Behaviour
        selector.setType(ChannelType.BEHAVIORAL);
        Result<Channel> result2 = advertisingChannelService.get(selector);
        assertNotNull(result2);
        assertEquals(1, result2.getEntities().size());

        // Only Behaviour and PUB
        selector.setVisibility(Visibility.PUB);
        Result<Channel> result3 = advertisingChannelService.get(selector);
        assertNotNull(result3);
        assertEquals(0, result3.getEntities().size());

        // Update entity we just created
        BehavioralChannel forUpdate = (BehavioralChannel) result.getEntities().get(1);
        forUpdate.setStatus(Status.ACTIVE);
        
        List<EntityLink> categories = new ArrayList<EntityLink>();
        categories.add(link(longProperty("foros.test.category.create")));
        categories.add(link(longProperty("foros.test.category.update")));
        forUpdate.setCategories(categories);

        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        List<Long> ids = advertisingChannelService.perform(operations).getIds();

        selector = new ChannelSelector();
        selector.setChannelIds(ids);
        result = advertisingChannelService.get(selector);

        assertEquals(1, result.getEntities().size());

        // Check that entity was really updated
        BehavioralChannel updated = (BehavioralChannel) find(result, forUpdate.getId());
        assertNotNull(updated);
        assertEquals(forUpdate.getName(), updated.getName());
        assertEquals(forUpdate.getStatus(), updated.getStatus());
        assertEquals(forUpdate.getCategories().size(), updated.getCategories().size());

        // Check search by name
        selector = new ChannelSelector();
        selector.setName(forUpdate.getName());
        result = advertisingChannelService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
    }

    private Channel prepareExpression() throws DatatypeConfigurationException {
        ExpressionChannel channel = new ExpressionChannel();

        channel.setName("Test expression channel " + String.valueOf(System.currentTimeMillis()));
        channel.setAccount(link(longProperty("foros.test.agency.id")));
        channel.setCountry("GB");
        channel.setStatus(Status.INACTIVE);
        channel.setVisibility("PRI");
        channel.setExpression(stringProperty("foros.test.channel.advertising.id"));

        return channel;
    }

    private Channel prepareBehavioral() throws DatatypeConfigurationException {
        BehavioralChannel channel = new BehavioralChannel();

        channel.setName("Test behavioral channel " + String.valueOf(System.currentTimeMillis()));
        channel.setAccount(link(longProperty("foros.test.agency.id")));
        channel.setCountry("GB");
        channel.setLanguage("en");
        channel.setStatus(Status.INACTIVE);
        channel.setVisibility("PRI");
        TriggersType searchKeywords = new TriggersType();
        searchKeywords.setPositive(Arrays.asList("Testkeyword"));
        channel.setSearchKeywords(searchKeywords);

        BehavioralParameters bp = new BehavioralParameters();
        bp.setTriggerType(BehavioralParametersTriggerType.S);
        bp.setMinimumVisits(1L);
        bp.setTimeFrom(0L);
        bp.setTimeTo(360L);

        channel.setBehavioralParameters(Arrays.asList(bp));
        
        List<EntityLink> categories = new ArrayList<EntityLink>();
        categories.add(link(longProperty("foros.test.category.create")));
        channel.setCategories(categories);

        return channel;
    }

    private Channel prepareAudience() throws DatatypeConfigurationException {
        AudienceChannel channel = new AudienceChannel();

        channel.setName("Test audience channel " + String.valueOf(System.currentTimeMillis()));
        channel.setAccount(link(longProperty("foros.test.agency.id")));
        channel.setCountry("GB");
        channel.setStatus(Status.ACTIVE);
        channel.setVisibility("PRI");

        return channel;
    }

    private BehavioralChannel createBehavioralChannel() throws Exception {
        Operations<Channel> operations = new Operations<Channel>();
        operations.setOperations(Arrays.asList(
                operation(prepareBehavioral(), OperationType.CREATE)
        ));

        OperationsResult operationsResult = advertisingChannelService.perform(operations);
        ChannelSelector selector = new ChannelSelector();
        selector.setChannelIds(operationsResult.getIds());

        Result<Channel> result = advertisingChannelService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return (BehavioralChannel)result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        BehavioralChannel persisted = createBehavioralChannel();
        BehavioralChannel forUpdate = new BehavioralChannel();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());

        Operations<Channel> operations = new Operations<Channel>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            advertisingChannelService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {
            System.out.println(e.getConstraintViolations());
        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        BehavioralChannel persisted = createBehavioralChannel();
        BehavioralChannel forUpdate = new BehavioralChannel();
        forUpdate.setId(persisted.getId());

        Operations<Channel> operations = new Operations<Channel>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            advertisingChannelService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    private static Channel updateRandom(Channel channel) {
        channel.setName(String.valueOf(new Random().nextGaussian()));
        return channel;
    }

    private Channel find(Result<Channel> result, Long id) {
        for (Channel channel : result.getEntities()) {
            if (id.equals(channel.getId())) {
                return channel;
            }

        }
        return null;
    }
}
