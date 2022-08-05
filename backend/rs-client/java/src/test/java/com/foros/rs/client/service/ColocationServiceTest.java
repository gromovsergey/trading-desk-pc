package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.isp.Colocation;
import com.foros.rs.client.model.isp.ColocationSelector;
import com.foros.rs.client.model.operation.Result;
import org.junit.Test;

import java.util.Arrays;

public class ColocationServiceTest extends AbstractUnitTest {
    @Test
    public void testRead() throws Exception {
        Long colocationId = longProperty("foros.test.colocation.id");

        ColocationSelector selector = new ColocationSelector();
        selector.setColocationIds(Arrays.asList(colocationId));
        Result<Colocation> result = foros.getColocationService().get(selector);
        check(result, null);

        Colocation colocation = result.getEntities().get(0);

        selector = new ColocationSelector();
        selector.setName(colocation.getName());
        result = foros.getColocationService().get(selector);
        check(result, colocation);

        selector.setAccountIds(Arrays.asList(colocation.getAccount().getId()));
        selector.setColocationStatuses(Arrays.asList(colocation.getStatus()));

        result = foros.getColocationService().get(selector);
        check(result, colocation);
    }

    private void check(Result<Colocation> result, Colocation expected) {
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());

        if (expected != null) {
            assertEquals(expected.getId(), result.getEntities().get(0).getId());
        }
    }
}
