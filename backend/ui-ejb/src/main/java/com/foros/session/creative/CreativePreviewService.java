package com.foros.session.creative;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.fileman.ContentSource;
import com.foros.util.preview.PreviewModel;

import java.io.OutputStream;
import javax.ejb.Local;

@Local
public interface CreativePreviewService {
    public void deleteObsoletePreviews();

    void deletePreview(Creative creative);

    void deletePreview(CreativeSize size);

    void deletePreview(CreativeTemplate template);

    void deleteAllTemporaryCreativePreviews();

    PreviewInfoTO generateCreativePreviewInfo(Long creativeId, Long sizeId, Long templateId);

    PreviewInfoTO generateCreativePreviewInfo(Long creativeId);

    ContentSource dcreative(String fileName, Long creativeId);

    String generateTemporaryPreview(Creative creative);

    ContentSource generatePreview(String previewPath);

    ContentSource getTemporaryPreview(String path);

    PreviewModel buildPreviewModel(CreativeTemplate template, CreativeSize size);

    void generatePreview(CreativePreviewOptions previewOptions, OutputStream output);
}
