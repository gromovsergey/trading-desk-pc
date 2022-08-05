package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.model.publishing.Tag;
import com.foros.rs.client.model.publishing.TagEffectiveSizes;
import com.foros.rs.client.model.publishing.TagSelector;

import java.util.Arrays;

import org.junit.Test;

public class TagServiceTest extends AbstractUnitTest {

    @Test
    public void testTags() throws Exception {
        TagSelector selector = new TagSelector();
        selector.setSiteIds(Arrays.asList(longProperty("foros.test.site.id")));

        Result<Tag> result = tagService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());

        Long tagId = result.getEntities().iterator().next().getId();
        TagEffectiveSizes tagEffectiveSizes = tagService.effectiveSizes(tagId);
        assertNotNull(tagEffectiveSizes);
        assertEquals(tagId, tagEffectiveSizes.getTag().getId());
        assertNotNull(tagEffectiveSizes.getSizes());
        assertTrue(tagEffectiveSizes.getSizes().size() > 0);

        result = tagService.get(new TagSelector());
        assertNotNull(result);
    }

    @Test
    public void testTagEffectiveSizes() throws Exception {
        TagEffectiveSizes tagEffectiveSizes = tagService.effectiveSizes(longProperty("foros.test.tag.id"));
        assertNotNull(tagEffectiveSizes);
    }

}
