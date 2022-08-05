package com.foros.jaxb.adapters;

import com.foros.model.action.ConversionCategory;
import com.foros.validation.code.InputErrors;

import java.util.Arrays;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ConversionCategoryXmlAdapter extends XmlAdapter<String, ConversionCategory> {

    @Override
    public String marshal(ConversionCategory entity) throws Exception {
        return entity.toString();
    }

    @Override
    public ConversionCategory unmarshal(String s) throws Exception {
        try {
            return ConversionCategory.valueOf(s);
        } catch (Exception e) {
            throw new LocalizedParseException(
                InputErrors.XML_ENUM_PARSE_ERROR,
                "errors.unexpectedEnumValue",
                Arrays.toString(ConversionCategory.values()));
        }
    }

}