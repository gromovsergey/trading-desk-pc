package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.QueryParam;

public class OptionGroupGeneratorFactory extends BaseGeneratorFactory<OptionGroup, OptionGroupTestFactory> {
    private CreativeSize size;
    private CreativeTemplate template;

    public OptionGroupGeneratorFactory(OptionGroupTestFactory optionGroupTestFactory, CreativeSize size, CreativeTemplate template) {
        super(optionGroupTestFactory);
        this.size = size;
        this.template = template;
    }

    @Override
    protected OptionGroup createDefault() {
        return factory.create(size, template, OptionGroupType.Advertiser);
    }

    @Override
    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("defaultName", name);
    }

    @Override
    protected void setName(OptionGroup entity, String name) {
        entity.setDefaultName(name);
    }
}
