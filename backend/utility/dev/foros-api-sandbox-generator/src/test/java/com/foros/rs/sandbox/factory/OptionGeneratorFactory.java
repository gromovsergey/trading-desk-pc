package com.foros.rs.sandbox.factory;

import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.QueryParam;
import com.foros.util.CollectionMerger;

public class OptionGeneratorFactory extends BaseGeneratorFactory<Option, OptionTestFactory> {
    private OptionGroup group;
    private OptionType type;

    public OptionGeneratorFactory(OptionTestFactory optionTestFactory, OptionGroup group) {
        super(optionTestFactory);
        this.group = group;
    }

    public Option findOrCreate(String entityName, OptionType type) {
        this.type = type;
        return findOrCreate(entityName);
    }

    @Override
    protected Option createDefault() {
        Option option = factory.create(group, type);
        option.setToken(type.name() + "_TOKEN");
        return option;
    }

    @Override
    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("defaultName", name);
    }

    @Override
    protected void setName(Option entity, String name) {
        entity.setDefaultName(name);
    }


    @Override
    protected void update(Option entity, Option existing) {
        (new CollectionMerger<OptionEnumValue>(existing.getValues(), entity.getValues()){
            @Override
            protected Object getId(OptionEnumValue optionEnumValue, int index) {
                return optionEnumValue.getValue();
            }

            @Override
            protected void update(OptionEnumValue persistent, OptionEnumValue updated) {
                updated.setId(persistent.getId());
            }
        }).merge();
        super.update(entity, existing);
    }
}
