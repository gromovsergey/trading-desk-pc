package com.foros.session.site;

import com.foros.model.site.Tag;

import java.io.ByteArrayOutputStream;

public class MockTagsPreviewServiceBean implements TagsPreviewService {

    @Override
    public ByteArrayOutputStream getLivePreview(Tag tag) {
        return null;
    }
}
