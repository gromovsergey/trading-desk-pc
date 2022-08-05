package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.QueryParam;

public class CreativeSizeGeneratorFactory extends BaseGeneratorFactory<CreativeSize, CreativeSizeTestFactory> {
    public CreativeSizeGeneratorFactory(CreativeSizeTestFactory creativeSizeTestFactory) {
        super(creativeSizeTestFactory);
    }

    @Override
    protected CreativeSize createDefault() {
        return factory.create();
    }

    @Override
    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("defaultName", name);
    }

    @Override
    protected void setName(CreativeSize entity, String name) {
        entity.setDefaultName(name);
    }
}
