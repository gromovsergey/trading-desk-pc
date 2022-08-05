package com.foros.rs.client.service;


import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.channel.TriggersType;
import com.foros.rs.client.model.discover.DiscoverChannel;
import com.foros.rs.client.model.discover.DiscoverChannelSelector;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Test;

public class DiscoverChannelServiceTest extends AbstractUnitTest {
    @Test
    public void testCRUD() throws Exception {
        Operations<DiscoverChannel> operations = new Operations<DiscoverChannel>();
        operations.setOperations(Arrays.asList(
                operation(prepareDiscover(), OperationType.CREATE)

        ));
        OperationsResult operationsResult = discoverChannelService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(1, operationsResult.getIds().size());

        // Check it created
        DiscoverChannelSelector selector = new DiscoverChannelSelector();
        selector.setChannelIds(operationsResult.getIds());

        Result<DiscoverChannel> result = discoverChannelService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        assertEquals(operationsResult.getIds().get(0), result.getEntities().get(0).getId());

        // Update discover we just created and create one more
        DiscoverChannel forUpdate = result.getEntities().get(0);
        forUpdate.setStatus(Status.ACTIVE);
        forUpdate.setCountry("BR");

        operations.setOperations(Arrays.asList(
                operation(prepareDiscover(), OperationType.CREATE),
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        List<Long> ids = discoverChannelService.perform(operations).getIds();

        selector = new DiscoverChannelSelector();
        selector.setChannelIds(ids);
        result = discoverChannelService.get(selector);

        assertEquals(2, result.getEntities().size());

        // Check that discover was really updated
        DiscoverChannel updated = find(result, forUpdate.getId());
        assertNotNull(updated);
        assertEquals(forUpdate.getName(), updated.getName());
        assertEquals(forUpdate.getStatus(), updated.getStatus());
        assertEquals(forUpdate.getCountry(), updated.getCountry());
    }

    private DiscoverChannel createDiscoverChannel() throws Exception {
        Operations<DiscoverChannel> operations = new Operations<DiscoverChannel>();
        operations.setOperations(Arrays.asList(
                operation(prepareDiscover(), OperationType.CREATE)
        ));

        OperationsResult operationsResult = discoverChannelService.perform(operations);
        DiscoverChannelSelector selector = new DiscoverChannelSelector();
        selector.setChannelIds(operationsResult.getIds());

        Result<DiscoverChannel> result = discoverChannelService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        DiscoverChannel persisted = createDiscoverChannel();
        DiscoverChannel forUpdate = new DiscoverChannel();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());

        Operations<DiscoverChannel> operations = new Operations<DiscoverChannel>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            discoverChannelService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {

        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        DiscoverChannel persisted = createDiscoverChannel();
        DiscoverChannel forUpdate = new DiscoverChannel();
        forUpdate.setId(persisted.getId());

        Operations<DiscoverChannel> operations = new Operations<DiscoverChannel>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            discoverChannelService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    private DiscoverChannel prepareDiscover() throws DatatypeConfigurationException {
        DiscoverChannel channel = new DiscoverChannel();

        channel.setName("Test behavioral channel " + String.valueOf(System.currentTimeMillis()));
        channel.setAccount(link(longProperty("foros.test.internal.id")));
        channel.setCountry("GB");
        channel.setStatus(Status.INACTIVE);
        channel.setVisibility("PRI");
        channel.setLanguage("en");
        channel.setBaseKeyword("TestKeyword");
        channel.setDiscoverAnnotation("test");
        channel.setDiscoverQuery("test");
        TriggersType searchKeywords = new TriggersType();
        searchKeywords.setPositive(Arrays.asList("Testkeyword"));
        channel.setSearchKeywords(searchKeywords);

        return channel;
    }

    private DiscoverChannel find(Result<DiscoverChannel> result, Long id) {
        for (DiscoverChannel channel : result.getEntities()) {
            if (id.equals(channel.getId())) {
                return channel;
            }

        }
        return null;
    }

    private static DiscoverChannel updateRandom(DiscoverChannel channel) {
        channel.setName(String.valueOf(new Random().nextGaussian()));
        return channel;
    }

}
