package com.foros.jaxb.adapters;

import com.foros.model.template.OptionEnumValue;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OptionEnumValueXmlAdapter extends XmlAdapter<NameValue, OptionEnumValue> {

    @Override
    public NameValue marshal(OptionEnumValue id) throws Exception {
        if (id == null) {
            return null;
        }
        return new NameValue(id.getName(), id.getValue());
    }

    @Override
    public OptionEnumValue unmarshal(NameValue nameValue) throws Exception {
        OptionEnumValue result = new OptionEnumValue();
        result.setName(nameValue.getName());
        result.setValue(nameValue.getValue());

        return result;
    }
}

