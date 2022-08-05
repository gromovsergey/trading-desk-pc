package com.foros.util.preview;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;

public abstract class PreviewDimensionsSetter<T> {
    public void setWidthHeight(CreativeTemplate template, CreativeSize size, OptionValueSource optionValueSource, T target) {
        PreviewModel previewModel = getPreviewModel(template, size);
        PreviewContext previewContext = PreviewContextBuilder.empty()
                .withTemplate(template)
                .withSize(size)
                .withOptionValueSource(optionValueSource)
                .build(previewModel.getAllDefinitions());

        setDimensions(toLong(previewContext.evaluateToken(CreativeToken.WIDTH)),
                      toLong(previewContext.evaluateToken(CreativeToken.HEIGHT)),
                      target);
    }

    protected abstract PreviewModel getPreviewModel(CreativeTemplate template, CreativeSize size);
    protected abstract void setDimensions(Long width, Long height, T target);

    private static Long toLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

