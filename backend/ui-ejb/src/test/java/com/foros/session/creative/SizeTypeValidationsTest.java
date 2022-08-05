package com.foros.session.creative;

import com.foros.AbstractValidationsTest;
import com.foros.model.creative.SizeType;
import com.foros.test.factory.SizeTypeTestFactory;

import javax.ejb.EJB;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class SizeTypeValidationsTest extends AbstractValidationsTest {

    @EJB
    private SizeTypeTestFactory sizeTypeTF;

    @Test
    public void testValidateCreate() throws Exception {
        SizeType st = goodForCreate();
        validateCreate(st);
        assertViolationsCount(0);

        st.setDefaultName(null);
        validateCreate(st);
        assertViolationsCount(1);
        assertHasViolation("defaultName");

        String goodHtml = StringUtils.repeat("s", 1024);
        st = goodForCreate();
        st.setTagTemplateFile(goodHtml);
        st.setTagTemplateIframeFile(goodHtml);
        st.setTagTemplateBrPbFile(goodHtml);
        st.setTagTemplateIEstFile(goodHtml);
        st.setTagTemplatePreviewFile(goodHtml);
        validateCreate(st);
        assertViolationsCount(0);

        String badHtml = StringUtils.repeat("s", 1024 + 1);
        st = goodForCreate();
        st.setTagTemplateFile(badHtml);
        st.setTagTemplateIframeFile(badHtml);
        st.setTagTemplateBrPbFile(badHtml);
        st.setTagTemplateIEstFile(badHtml);
        st.setTagTemplatePreviewFile(badHtml);
        validateCreate(st);
        assertViolationsCount(5);
        assertHasViolation(
                "tagTemplateFile",
                "tagTemplateIframeFile",
                "tagTemplateBrPbFile",
                "tagTemplateIEstFile",
                "tagTemplatePreviewFile"
        );
    }

    @Test
    public void testValidateUpdate() {
        SizeType existing = sizeTypeTF.create();
        existing.setMultipleSizes(SizeType.MultipleSizes.MULTIPLE_SIZES);
        existing.setAdvertiserSizeSelection(SizeType.AdvertiserSizeSelection.TYPE_AND_SIZE_LEVEL);
        sizeTypeTF.persist(existing);

        SizeType st;

        st = goodForUpdate(existing);
        validateUpdate(st);
        assertViolationsCount(0);

        st = goodForUpdate(existing);
        st.setMultipleSizes(SizeType.MultipleSizes.ONE_SIZE);
        st.setAdvertiserSizeSelection(SizeType.AdvertiserSizeSelection.TYPE_LEVEL);
        validateUpdate(st);
        assertViolationsCount(0);
    }

    private SizeType goodForUpdate(SizeType existing) {
        SizeType res = new SizeType();
        res.setId(existing.getId());
        res.setDefaultName(existing.getDefaultName());
        res.setMultipleSizes(existing.getMultipleSizes());
        res.setAdvertiserSizeSelection(existing.getAdvertiserSizeSelection());
        return res;
    }

    private SizeType goodForCreate() {
        return sizeTypeTF.create();
    }

    private void validateCreate(SizeType st) {
        validate("SizeType.create", st);
    }

    private void validateUpdate(SizeType st) {
        validate("SizeType.update", st);
    }
}
