package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import com.foros.rs.client.model.advertising.campaign.CreativeLinkSelector;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;

import java.util.Arrays;

import org.junit.Test;

public class CreativeLinkServiceTest extends AbstractUnitTest {

    @Test
    public void testCRUD() throws Exception {
        Operations<CreativeLink> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
            operation(prepareCreative(new CreativeLink()), OperationType.CREATE)
        ));

        OperationsResult res = foros.getCreativeLinkService().perform(operations);
        assertNotNull(res);
        assertEquals(1, res.getIds().size());

        CreativeLinkSelector selector = new CreativeLinkSelector();
        selector.setCreativeLinkIds(res.getIds());
        Result<CreativeLink> creatives = foros.getCreativeLinkService().get(selector);

        CreativeLink updated = creatives.getEntities().get(0);

        operations.setOperations(Arrays.asList(
                operation(updated, OperationType.UPDATE),
            operation(prepareCreative(new CreativeLink()), OperationType.CREATE)
        ));

        res = foros.getCreativeLinkService().perform(operations);
        assertNotNull(res);
        assertEquals(2, res.getIds().size());
    }

    private CreativeLink prepareCreative(CreativeLink creative) {
        creative.setCreativeGroup(link(longProperty("foros.test.displayGroup.id")));
        creative.setCreative(link(longProperty("foros.test.creative.id")));
        return creative;
    }
}
