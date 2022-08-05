package com.foros.session.creative;

import com.foros.util.preview.OptionValueSource;

import java.util.ArrayList;
import java.util.List;

public class CreativePreviewOptions {
    private OptionValueSource tagSource = OptionValueSource.NULL;
    private List<? extends OptionValueSource> creativeSources = new ArrayList<>();
    private Long creativeId;
    private Long templateId;
    private Long sizeId;
    private String adFooterUrl;

    public List<? extends OptionValueSource> getCreativeSources() {
        return creativeSources;
    }

    public void setCreativeSources(List<? extends OptionValueSource> creativeSources) {
        this.creativeSources = creativeSources;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public OptionValueSource getTagSource() {
        return tagSource;
    }

    public void setTagSource(OptionValueSource tagSource) {
        this.tagSource = tagSource;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getAdFooterUrl() {
        return adFooterUrl;
    }

    public void setAdFooterUrl(String adFooterUrl) {
        this.adFooterUrl = adFooterUrl;
    }
}
