package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.test.factory.TagsTestFactory;

public class TagGeneratorFactory extends BaseGeneratorFactory<Tag, TagsTestFactory> {
    private Site site;
    private CreativeSize size;

    public TagGeneratorFactory(TagsTestFactory tagsTestFactory, Site site, CreativeSize size) {
        super(tagsTestFactory);
        this.site = site;
        this.size = size;
    }

    @Override
    protected Tag createDefault() {
        return factory.create(site, size);
    }
}
