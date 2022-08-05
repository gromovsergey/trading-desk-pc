package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.creativeCategory.CreativeCategory;
import com.foros.rs.client.model.creativeCategory.CreativeCategorySelector;
import com.foros.rs.client.model.creativeCategory.CreativeCategoryType;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;

import java.util.Collections;
import junit.framework.Assert;
import org.junit.Test;

public class CreativeCategoryServiceTest extends AbstractUnitTest {

    @Test
    public void testSearch() throws Exception {

        CreativeCategorySelector selector = new CreativeCategorySelector();

        selector.setType(CreativeCategoryType.CONTENT);
        PagingSelector paging = new PagingSelector();
        paging.setCount(3L);
        paging.setFirst(2L);
        selector.setPaging(paging);

        Result<CreativeCategory> result = creativeCategoryService.get(selector);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getEntities().size());
        CreativeCategory category = result.getEntities().get(0);
        Assert.assertEquals(category.getType(), CreativeCategoryType.CONTENT);

        selector.setIds(Collections.singletonList(category.getId()));
        selector.setPaging(null);

        result = creativeCategoryService.get(selector);
        Assert.assertEquals(1, result.getEntities().size());
    }
}
