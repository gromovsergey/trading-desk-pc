package com.foros.util.preview;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;

import java.util.Map;

public class PreviewContextBuilder {
    private CreativeSize size;
    private CreativeTemplate template;
    private Long creativeId;
    private OptionValueSource optionValueSource;
    private PreviewContext parent;
    private String adFooterUrl;

    public static PreviewContextBuilder empty() {
        return new PreviewContextBuilder();
    }

    public static PreviewContextBuilder of(CreativeTemplate template, CreativeSize size, OptionValueSource optionValueSource) {
        PreviewContextBuilder builder = new PreviewContextBuilder();
        builder.size = size;
        builder.template = template;
        builder.optionValueSource = optionValueSource;
        return builder;
    }

    public PreviewContextBuilder withTemplate(CreativeTemplate template) {
        this.template = template;
        return this;
    }

    public PreviewContextBuilder withSize(CreativeSize size) {
        this.size = size;
        return this;
    }

    public PreviewContextBuilder withCreativeId(Long creativeId) {
        this.creativeId = creativeId;
        return this;
    }

    public PreviewContextBuilder withParent(PreviewContext parent) {
        this.parent = parent;
        return this;
    }

    public PreviewContextBuilder withOptionValueSource(OptionValueSource optionValueSource) {
        this.optionValueSource = optionValueSource;
        return this;
    }

    public PreviewContextBuilder withAdFooter(String adFooterUrl) {
        this.adFooterUrl = adFooterUrl;
        return this;
    }

    public PreviewContext build(Map<String, TokenDefinition> definitions) {
        PreviewContext previewContext = new PreviewContext(definitions, optionValueSource);
        previewContext.putContextValue("WIDTH", size.getWidth() != null ? size.getWidth().toString() : null);
        previewContext.putContextValue("HEIGHT", size.getHeight() != null ? size.getHeight().toString() : null);
        previewContext.putContextValue("SIZE", size.getDefaultName());
        previewContext.putContextValue("TEMPLATE", template != null ? template.getDefaultName() : null);
        previewContext.putContextValue("IMAGE-PATH", optionValueSource.getImagesPath());
        previewContext.putContextValue("AD_FOOTER_URL", adFooterUrl);
        previewContext.putContextValue("CREATIVE_ID", creativeId != null ? creativeId.toString() : null);
        previewContext.setParent(parent);
        return previewContext;

    }
}
