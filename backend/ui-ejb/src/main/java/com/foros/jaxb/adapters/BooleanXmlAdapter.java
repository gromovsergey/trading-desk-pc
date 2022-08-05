package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanXmlAdapter extends XmlAdapter<String, Boolean> {

    @Override
    public String marshal(Boolean v) throws Exception {
        return v.toString();
    }

    @Override
    public Boolean unmarshal(String v) throws Exception {
        if (StringUtil.isPropertyEmpty(v)) {
            return null;
        }

        if (Boolean.TRUE.toString().equals(v)) {
            return Boolean.TRUE;
        }

        if (Boolean.FALSE.toString().equals(v)) {
            return Boolean.FALSE;
        }

        throw new IllegalArgumentException(v);
    }
}
