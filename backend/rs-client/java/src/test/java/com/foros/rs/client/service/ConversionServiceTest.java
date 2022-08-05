package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.conversion.Conversion;
import com.foros.rs.client.model.advertising.conversion.ConversionCategory;
import com.foros.rs.client.model.advertising.conversion.ConversionSelector;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class ConversionServiceTest extends AbstractUnitTest {
    @Test
    public void testGetByStatuses() throws Exception {
        ConversionSelector selector = new ConversionSelector();
        selector.setAdvertiserIds(Arrays.asList(longProperty("foros.test.advertiser.id")));
        selector.setConversionStatuses(Arrays.asList(Status.DELETED, Status.ACTIVE));

        Result<Conversion> result = conversionService.get(selector);
        assertNotNull(result);
        assertTrue(!result.getEntities().isEmpty());

        boolean deletedExist = false;
        boolean activeExist = false;

        for (Conversion conversion : result.getEntities()) {
            if (conversion.getStatus().equals(Status.DELETED)) {
                deletedExist = true;
            }

            if (conversion.getStatus().equals(Status.ACTIVE)) {
                activeExist = true;
            }

            if (deletedExist && activeExist) {
                break;
            }
        }

        assertTrue(deletedExist);
        assertTrue(activeExist);

        selector.setAdvertiserIds(Arrays.asList(longProperty("foros.test.advertiser.id")));
        selector.setConversionStatuses(Arrays.asList(Status.DELETED));

        result = conversionService.get(selector);
        assertNotNull(result);
        assertTrue(!result.getEntities().isEmpty());

        for (Conversion conversion : result.getEntities()) {
            if (!conversion.getStatus().equals(Status.DELETED)) {
                fail("Only DELETED conversions");
            }
        }

    }

    @Test
    public void testCRUD() throws Exception {
        // Create one conversion
        Operations<Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(prepareConversion(), OperationType.CREATE)
            ));

        OperationsResult operationsResult = conversionService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(1, operationsResult.getIds().size());

        // Check it created
        ConversionSelector selector = new ConversionSelector();
        selector.setConversionIds(operationsResult.getIds());

        Result<Conversion> result = conversionService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        assertEquals(operationsResult.getIds().get(0), result.getEntities().get(0).getId());

        // Update conversion we just created and create one more
        Conversion forUpdate = result.getEntities().get(0);
        forUpdate.setValue(BigDecimal.ONE);

        operations.setOperations(Arrays.asList(
            operation(prepareConversion(), OperationType.CREATE),
            operation(updateRandom(forUpdate), OperationType.UPDATE)
            ));

        List<Long> ids = conversionService.perform(operations).getIds();

        selector = new ConversionSelector();
        selector.setConversionIds(ids);
        result = conversionService.get(selector);

        assertEquals(2, result.getEntities().size());

        // Check that conversion was really updated
        Conversion updated = find(result, forUpdate.getId());
        assertNotNull(updated);
        assertEquals(forUpdate.getName(), updated.getName());
        assertEquals(forUpdate.getValue(), updated.getValue());
    }

    private Conversion createConversion() throws Exception {
        Operations<Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(prepareConversion(), OperationType.CREATE)
            ));

        OperationsResult operationsResult = conversionService.perform(operations);
        ConversionSelector selector = new ConversionSelector();
        selector.setConversionIds(operationsResult.getIds());

        Result<Conversion> result = conversionService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        Conversion persisted = createConversion();
        Conversion forUpdate = new Conversion();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());

        Operations<Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(updateRandom(forUpdate), OperationType.UPDATE)
            ));

        try {
            conversionService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        Conversion persisted = createConversion();
        Conversion forUpdate = new Conversion();
        forUpdate.setId(persisted.getId());

        Operations<Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(updateRandom(forUpdate), OperationType.UPDATE)
            ));

        try {
            conversionService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    @Test
    public void testUpdateOnlyName() throws Exception {
        Conversion persisted = createConversion();
        Conversion forUpdate = new Conversion();
        forUpdate.setId(persisted.getId());
        updateRandom(forUpdate);

        String name = forUpdate.getName();
        Operations<Conversion> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(forUpdate, OperationType.UPDATE)
            ));

        try {
            conversionService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }

        ConversionSelector selector = new ConversionSelector();
        selector.setConversionIds(Arrays.asList(forUpdate.getId()));
        assertEquals(name, conversionService.get(selector).getEntities().get(0).getName());
    }

    private Conversion find(Result<Conversion> result, Long id) {
        for (Conversion conversion : result.getEntities()) {
            if (id.equals(conversion.getId())) {
                return conversion;
            }

        }
        return null;
    }

    private Conversion prepareConversion() {
        Conversion conversion = new Conversion();
        conversion.setName("Test conversion " + String.valueOf(System.currentTimeMillis()));
        conversion.setStatus(Status.ACTIVE);
        conversion.setAccount(advertiserLink(longProperty("foros.test.advertiser.id")));
        conversion.setClickWindow(12);
        conversion.setConversionCategory(ConversionCategory.LEAD);
        conversion.setImpWindow(22);
        conversion.setUrl("http://ua.de");
        conversion.setValue(BigDecimal.TEN);

        return conversion;
    }

    private static Conversion updateRandom(Conversion conversion) {
        conversion.setName(String.valueOf(new Random().nextGaussian()));
        return conversion;
    }
}
