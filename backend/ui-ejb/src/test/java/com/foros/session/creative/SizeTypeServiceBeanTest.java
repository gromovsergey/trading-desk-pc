package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Flags;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.SizeTypeTestFactory;

import java.util.List;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.junit.Test;

public class SizeTypeServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @EJB
    private SizeTypeService sizeTypeService;

    @EJB
    private SizeTypeTestFactory sizeTypeTF;

    @EJB
    private CreativeSizeTestFactory creativeSizeTestFactory;

    @EJB
    private AdvertiserAccountTypeTestFactory accountTypeTestFactory;

    @Test
    public void testFindByName() {
        SizeType other = sizeTypeService.findByName("Other");
        Assert.assertNotNull(other);
    }

    @Test
    public void testFindByAccountType() {
        CreativeSize creativeSize = creativeSizeTestFactory.createPersistent();
        AccountType accountType = accountTypeTestFactory.createPersistent(creativeSize, null);

        List<SizeTypeTO> types = sizeTypeService.findByAccountType(accountType.getId());
        Assert.assertNotNull(types);
        SizeType sizeType = creativeSize.getSizeType();
        Assert.assertTrue(types.contains(new SizeTypeTO(sizeType.getId(), sizeType.getDefaultName(), new Flags(1))));
    }

    @Test
    public void testCreate() {
        SizeType sizeType = new SizeType();
        sizeType.setDefaultName("test" + System.currentTimeMillis());
        sizeTypeService.create(sizeType);
        commitChanges();
        assertNotNull(sizeType.getId());
    }

    @Test
    public void testUpdate() {
        SizeType sizeType = sizeTypeTF.createPersistent();
        assertEquals(SizeType.MultipleSizes.ONE_SIZE, sizeType.getMultipleSizes());
        assertEquals(SizeType.AdvertiserSizeSelection.TYPE_LEVEL, sizeType.getAdvertiserSizeSelection());

        sizeType = blank(sizeType);
        sizeType.setMultipleSizes(SizeType.MultipleSizes.MULTIPLE_SIZES);
        sizeTypeService.update(sizeType);
        commitChanges();
        clearContext();

        sizeType = sizeTypeService.find(sizeType.getId());
        assertEquals(SizeType.MultipleSizes.MULTIPLE_SIZES, sizeType.getMultipleSizes());
        assertEquals(SizeType.AdvertiserSizeSelection.TYPE_LEVEL, sizeType.getAdvertiserSizeSelection());

        sizeType = blank(sizeType);
        sizeType.setDefaultName(sizeTypeTF.getTestEntityRandomName());
        sizeType.setMultipleSizes(SizeType.MultipleSizes.ONE_SIZE);
        sizeType.setAdvertiserSizeSelection(SizeType.AdvertiserSizeSelection.TYPE_AND_SIZE_LEVEL);
        sizeType.setTagTemplateBrPbFile("setTagTemplateBrPbFile");
        sizeType.setTagTemplateFile("setTagTemplateFile");
        sizeType.setTagTemplateIEstFile("setTagTemplateIEstFile");
        sizeType.setTagTemplateIframeFile("setTagTemplateIframeFile");
        sizeType.setTagTemplatePreviewFile("setTagTemplatePreviewFile");

        sizeTypeService.update(sizeType);
        commitChanges();
        clearContext();

        sizeType = sizeTypeService.find(sizeType.getId());
        assertEquals(SizeType.MultipleSizes.ONE_SIZE, sizeType.getMultipleSizes());
        assertEquals(SizeType.AdvertiserSizeSelection.TYPE_AND_SIZE_LEVEL, sizeType.getAdvertiserSizeSelection());
        assertEquals("setTagTemplateBrPbFile", sizeType.getTagTemplateBrPbFile());
        assertEquals("setTagTemplateFile", sizeType.getTagTemplateFile());
        assertEquals("setTagTemplateIEstFile", sizeType.getTagTemplateIEstFile());
        assertEquals("setTagTemplateIframeFile", sizeType.getTagTemplateIframeFile());
        assertEquals("setTagTemplatePreviewFile", sizeType.getTagTemplatePreviewFile());
    }

    @Test
    public void testFindAll() {
        List<SizeType> all = sizeTypeService.findAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);
    }

    private SizeType blank(SizeType sizeType) {
        SizeType blank = new SizeType();
        blank.setId(sizeType.getId());
        blank.unregisterChange("id");
        blank.setVersion(sizeType.getVersion());
        return blank;
    }
}
