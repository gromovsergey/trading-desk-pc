package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.TemplateFileType;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.QueryParam;

public class CreativeTemplateGeneratorFactory extends BaseGeneratorFactory<CreativeTemplate, CreativeTemplateTestFactory> {
    private CreativeSize size;

    public CreativeTemplateGeneratorFactory(CreativeTemplateTestFactory creativeTemplateTestFactory, CreativeSize size) {
        super(creativeTemplateTestFactory);
        this.size = size;
    }

    @Override
    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("defaultName", name);
    }

    @Override
    protected void setName(CreativeTemplate entity, String name) {
        entity.setDefaultName(name);
    }

    @Override
    protected void updatePersistentEntity(CreativeTemplate template) {
        factory.createPersistentTemplateFile(template, TemplateFileType.TEXT, "js", size, "/file.foros-ui");
    }

    @Override
    protected CreativeTemplate createDefault() {
        return factory.create();
    }
}
