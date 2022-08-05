package com.foros.test.factory;

import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.session.template.OptionService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class OptionTestFactory extends TestFactory<Option> {
    @EJB
    private OptionService creativeOptionService;

    @Override
    @Deprecated
    public Option create() {
        throw new UnsupportedOperationException("create");
    }

    public Option create(OptionGroup optionGroup, OptionType optionType) {
        Option option = new Option();
        option.setDefaultName(getTestEntityRandomName());
        option.setType(optionType);
        option.setOptionGroup(optionGroup);
        option.setToken(getTestEntityRandomName().replaceAll("-", ""));

        switch (optionType) {
            case INTEGER:
                option.setDefaultValue("1");
                break;
            case FILE: case DYNAMIC_FILE: case HTML:
                break;
            case ENUM:
                Set<OptionEnumValue> values= new LinkedHashSet<OptionEnumValue>();

                OptionEnumValue v1 = new OptionEnumValue();
                v1.setOption(option);
                v1.setName("o1");
                v1.setValue("o1");

                OptionEnumValue v2 = new OptionEnumValue();
                v2.setOption(option);
                v2.setName("o2");
                v2.setValue("o2");

                v1.setDefault(true);
                option.setDefaultValue(v1.getValue());
                values.add(v1);
                values.add(v2);
                option.setValues(values);
                break;
            default:
                option.setDefaultValue("Test default value");
        }
        option.setSortOrder(optionGroup.getOptions().size());
        return option;
    }

    @Override
    public void persist(Option option) {
        creativeOptionService.create(option);
    }

    public void update(Option option) {
        creativeOptionService.update(option);
    }

    public void remove(Option option) {
        creativeOptionService.remove(option.getId());
    }

    @Override
    @Deprecated
    public Option createPersistent() {
        throw new UnsupportedOperationException("create");
    }

    public Option createPersistent(OptionGroup optionGroup, OptionType optionType) {
        Option co = create(optionGroup, optionType);
        persist(co);
        return co;
    }

    public String newOptionValue(Option option) {
        String value;
        switch (option.getType()) {
            case STRING:
            case TEXT:
                value = "string" + option.getId();
                break;
            case URL:
            case FILE_URL:
                value = "http://url.com/" + option.getId();
                break;
            case URL_WITHOUT_PROTOCOL:
                value = "url.com/" + option.getId();
                break;
            case INTEGER:
                value = "7" + option.getId();
                break;
            case COLOR:
                value = String.format("%06x", option.getId() & 0xFFFFFF);
                break;
            case ENUM:
                value = option.getDefaultValue();
                break;
            case HTML:
                value = String.format("<div>%d</div>", option.getId());
                break;
            default:
                throw new IllegalArgumentException("unsupported " + option.getType());
        }
        return value;
    }
}
