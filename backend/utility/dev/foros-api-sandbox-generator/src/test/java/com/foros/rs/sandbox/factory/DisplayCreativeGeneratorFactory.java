package com.foros.rs.sandbox.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.test.factory.DisplayCreativeTestFactory;

public class DisplayCreativeGeneratorFactory extends BaseGeneratorFactory<Creative, DisplayCreativeTestFactory> {
    private AdvertiserAccount account;
    private CreativeTemplate template;
    private CreativeSize size;
    private CreativeCategory visualCategory;
    CreativeCategory contentCategory;

    public DisplayCreativeGeneratorFactory(DisplayCreativeTestFactory displayCreativeTestFactory, AdvertiserAccount account,
            CreativeSize size, CreativeTemplate template, CreativeCategory visualCategory, CreativeCategory contentCategory) {
        super(displayCreativeTestFactory);
        this.account = account;
        this.template = template;
        this.size = size;
        this.visualCategory = visualCategory;
        this.contentCategory = contentCategory;
    }

    @Override
    protected Creative createDefault() {
        Creative entity = factory.create(account, template, size);
        entity.getCategories().add(visualCategory);
        entity.getCategories().add(contentCategory);
        return entity;
    }
}
