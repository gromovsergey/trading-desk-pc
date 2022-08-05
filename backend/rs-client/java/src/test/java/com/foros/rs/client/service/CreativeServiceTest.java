package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.campaign.AdOption;
import com.foros.rs.client.model.advertising.campaign.Creative;
import com.foros.rs.client.model.advertising.campaign.CreativeSelector;
import com.foros.rs.client.model.advertising.template.LivePreviewResult;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class CreativeServiceTest extends AbstractUnitTest {

    @Test
    public void testDisplayCRUD() throws Exception {
        testCRUDImpl(new DisplayCreativeCreator());
    }

    @Test
    public void testTextCRUD() throws Exception {
        testCRUDImpl(new TextCreativeCreator());
    }

    @Test
    public void testLivePreview() throws Exception {
        DisplayCreativeCreator creator = new DisplayCreativeCreator();

        Creative updated = testCRImpl(creator);
        updated.setId(null);

        LivePreviewResult livePreviewResult = foros.getCreativeService().livePreview(updated);
        assertNotNull(livePreviewResult);
        assertTrue(livePreviewResult.getWidth() > 0);
        assertTrue(livePreviewResult.getHeight() > 0);
        assertNotNull(livePreviewResult.getUrl());
    }

    private void testCRUDImpl(CreativeCreator creator) throws Exception {
        Creative updated = testCRImpl(creator);

        Operations<Creative> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
                operation(updated, OperationType.UPDATE),
                operation(creator.createCreative(), OperationType.CREATE)
        ));

        OperationsResult res = foros.getCreativeService().perform(operations);
        assertNotNull(res);
        assertEquals(2, res.getIds().size());
    }

    private Creative testCRImpl(CreativeCreator creator) throws Exception {
        Operations<Creative> operations = new Operations<>();
        operations.setOperations(Arrays.asList(
                operation(creator.createCreative(), OperationType.CREATE)
        ));

        OperationsResult res = foros.getCreativeService().perform(operations);
        assertNotNull(res);
        assertEquals(1, res.getIds().size());

        CreativeSelector selector = new CreativeSelector();
        selector.setCreativeIds(res.getIds());
        Result<Creative> creatives = foros.getCreativeService().get(selector);

        return creatives.getEntities().get(0);
    }

    private interface CreativeCreator {
        Creative createCreative();
    }

    private class DisplayCreativeCreator implements CreativeCreator {
        @Override
        public Creative createCreative() {
            Creative creative = new Creative();

            creative.setAccount(advertiserLink(longProperty("foros.test.advertiser.id")));
            creative.setSize(link(longProperty("foros.test.size.id")));
            creative.setTemplate(link(longProperty("foros.test.template.id")));
            creative.setName("Test Creative " + System.currentTimeMillis());
            creative.setCategories(Arrays.asList(
                    link(longProperty("foros.test.category.content.id")),
                    link(longProperty("foros.test.category.visual.id"))
            ));
            creative.setOptions(Arrays.asList(
                    option(longProperty("foros.test.template.option.string.id"), "str" + System.currentTimeMillis()),
                    option(longProperty("foros.test.template.option.integer.id"), String.valueOf(System.currentTimeMillis() % 1000)),
                    option(longProperty("foros.test.template.option.enum.id"), stringProperty("foros.test.template.option.enum.value"))
            ));
            return creative;
        }
    }

    private class TextCreativeCreator implements CreativeCreator {
        @Override
        public Creative createCreative () {
            Creative creative = new Creative();

            creative.setAccount(advertiserLink(longProperty("foros.test.advertiser.id")));
            creative.setSize(link(longProperty("foros.test.size.text.id")));
            creative.setTemplate(link(longProperty("foros.test.template.text.id")));
            creative.setName("Test Creative " + System.currentTimeMillis());
            creative.setCategories(Arrays.asList(
                    link(longProperty("foros.test.category.content.id"))
            ));
            creative.setEnableAllAvailableSizes(false);
            creative.setSizeTypes(Collections.<EntityLink>emptyList());
            creative.setTagSizes(Collections.<EntityLink>emptyList());
            creative.setOptions(Arrays.asList(
                    option(longProperty("foros.test.template.text.option.headline.id"), "str" + System.currentTimeMillis()),
                    option(longProperty("foros.test.template.text.option.descriptionLine1.id"), "str" + System.currentTimeMillis()),
                    option(longProperty("foros.test.template.text.option.descriptionLine2.id"), "str" + System.currentTimeMillis()),
                    option(longProperty("foros.test.template.text.option.displayUrl.id"), "str" + System.currentTimeMillis()),
                    option(longProperty("foros.test.template.text.option.clickUrl.id"), "http://" + System.currentTimeMillis())
            ));
            return creative;
        }
    }

    private AdOption option(Long id, String value) {
        AdOption option = new AdOption();
        option.setId(id);
        option.setValue(value);
        return option;
    }
}
