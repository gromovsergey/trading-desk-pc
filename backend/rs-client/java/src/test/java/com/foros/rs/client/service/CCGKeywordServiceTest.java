package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.campaign.CCGKeyword;
import com.foros.rs.client.model.advertising.campaign.CCGKeywordSelector;
import com.foros.rs.client.model.advertising.campaign.CCGKeywordTriggerType;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class CCGKeywordServiceTest extends AbstractUnitTest {
    @Test
    public void testCRUD() throws Exception {
        // Create one CCGKeyword
        Operations<CCGKeyword> operations = new Operations<CCGKeyword>();
        CCGKeyword ccgKeyword = prepareCCGKeyword();
        ccgKeyword.setTriggerType(CCGKeywordTriggerType.page);
        operations.setOperations(Arrays.asList(
                operation(ccgKeyword, OperationType.CREATE)
        ));

        OperationsResult operationsResult = ccgKeywordService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(1, operationsResult.getIds().size());

        // Check it created
        CCGKeywordSelector selector = new CCGKeywordSelector();
        selector.setKeywordIds(operationsResult.getIds());

        Result<CCGKeyword> result = ccgKeywordService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        assertEquals(operationsResult.getIds().get(0), result.getEntities().get(0).getId());

        // Update CCGKeyword we just created and create one more
        CCGKeyword forUpdate = result.getEntities().get(0);
        forUpdate.setStatus(Status.ACTIVE);

        operations.setOperations(Arrays.asList(
                operation(prepareCCGKeyword(), OperationType.CREATE),
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        List<Long> ids = ccgKeywordService.perform(operations).getIds();

        selector = new CCGKeywordSelector();
        selector.setKeywordIds(ids);
        result = ccgKeywordService.get(selector);

        assertEquals(2, result.getEntities().size());

        // Check that CCGKeyword was really updated
        CCGKeyword updated = find(result, forUpdate.getId());
        assertNotNull(updated);
        assertEquals(forUpdate.getOriginalKeyword(), updated.getOriginalKeyword());
        assertEquals(forUpdate.getStatus(), updated.getStatus());

        // Create two CCGKeywords with the same original keyword and different types
        CCGKeyword ccgKeyword1 = prepareCCGKeyword();
        CCGKeyword ccgKeyword2 = prepareCCGKeyword();
        ccgKeyword1.setTriggerType(CCGKeywordTriggerType.page);
        ccgKeyword2.setTriggerType(CCGKeywordTriggerType.search);
        ccgKeyword2.setOriginalKeyword(ccgKeyword1.getOriginalKeyword());
        operations.setOperations(Arrays.asList(
                operation(ccgKeyword1, OperationType.CREATE),
                operation(ccgKeyword2, OperationType.CREATE)
        ));

        operationsResult = ccgKeywordService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(2, operationsResult.getIds().size());
    }

    private CCGKeyword createCCGKeyword() throws Exception {
        Operations<CCGKeyword> operations = new Operations<CCGKeyword>();
        operations.setOperations(Arrays.asList(
                operation(prepareCCGKeyword(), OperationType.CREATE)
        ));

        OperationsResult operationsResult = ccgKeywordService.perform(operations);
        CCGKeywordSelector selector = new CCGKeywordSelector();
        selector.setKeywordIds(operationsResult.getIds());

        Result<CCGKeyword> result = ccgKeywordService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        CCGKeyword persisted = createCCGKeyword();
        CCGKeyword forUpdate = new CCGKeyword();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());
        forUpdate.setMaxCpcBid(BigDecimal.TEN);

        Operations<CCGKeyword> operations = new Operations<CCGKeyword>();
        operations.setOperations(Arrays.asList(
                operation(forUpdate, OperationType.UPDATE)
        ));

        try {
            ccgKeywordService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        CCGKeyword persisted = createCCGKeyword();
        CCGKeyword forUpdate = new CCGKeyword();
        forUpdate.setId(persisted.getId());
        forUpdate.setMaxCpcBid(BigDecimal.TEN);

        Operations<CCGKeyword> operations = new Operations<CCGKeyword>();
        operations.setOperations(Arrays.asList(
                operation(forUpdate, OperationType.UPDATE)
        ));

        try {
            ccgKeywordService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    private CCGKeyword find(Result<CCGKeyword> result, Long id) {
        for (CCGKeyword ccgKeyword : result.getEntities()) {
            if (id.equals(ccgKeyword.getId())) {
                return ccgKeyword;
            }

        }
        return null;
    }

    private CCGKeyword prepareCCGKeyword(){
        CCGKeyword ccgKeyword = new CCGKeyword();
        ccgKeyword.setCreativeGroup(link(longProperty("foros.test.creativeGroup.id")));
        ccgKeyword.setOriginalKeyword("OriginalKeyword" + String.valueOf(System.currentTimeMillis()));
        ccgKeyword.setTriggerType(CCGKeywordTriggerType.page);
        ccgKeyword.setMaxCpcBid(new BigDecimal("0.01"));
        ccgKeyword.setClickURL("http://click.ru");
        return ccgKeyword;
    }

    private static CCGKeyword updateRandom(CCGKeyword ccgKeyword) {
        ccgKeyword.setMaxCpcBid(ccgKeyword.getMaxCpcBid().add(new BigDecimal("0.01")));
        return ccgKeyword;
    }
}
