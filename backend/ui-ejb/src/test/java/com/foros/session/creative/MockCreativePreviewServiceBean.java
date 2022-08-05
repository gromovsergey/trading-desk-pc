package com.foros.session.creative;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.ContentSourceSupport;
import com.foros.util.preview.PreviewModel;
import com.foros.util.preview.TokenDefinition;

import java.io.OutputStream;
import java.util.Collections;

public class MockCreativePreviewServiceBean implements CreativePreviewService {
    @Override
    public void deleteObsoletePreviews() {
    }

    @Override
    public void deletePreview(Creative creative) {
    }

    @Override
    public void deletePreview(CreativeSize size) {
    }

    @Override
    public void deletePreview(CreativeTemplate size) {
    }

    @Override
    public void deleteAllTemporaryCreativePreviews() {
    }

    @Override
    public PreviewInfoTO generateCreativePreviewInfo(Long creativeId, Long sizeId, Long templateId) {
        return new PreviewInfoTO();
    }

    @Override
    public PreviewInfoTO generateCreativePreviewInfo(Long creativeId) {
        return new PreviewInfoTO();
    }

    @Override
    public ContentSource dcreative(String fileName, Long creativeId) {
        return ContentSourceSupport.create(new byte[0], "");
    }

    @Override
    public String generateTemporaryPreview(Creative creative) {
        return null;
    }

    @Override
    public ContentSource generatePreview(String fileName) {
        return ContentSourceSupport.create(new byte[0], "");
    }

    @Override
    public ContentSource getTemporaryPreview(String path) {
        return null;
    }

    @Override
    public PreviewModel buildPreviewModel(CreativeTemplate template, CreativeSize size) {
        return new PreviewModel(
                Collections.<String, TokenDefinition>emptyMap(),
                Collections.<String, TokenDefinition>emptyMap()
        );
    }

    @Override
    public void generatePreview(CreativePreviewOptions creativePreview, OutputStream output) {

    }
}
