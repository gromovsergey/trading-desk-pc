package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ByteXmlAdapter extends XmlAdapter<String, Byte> {

    @Override
    public String marshal(Byte value) throws Exception {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    @Override
    public Byte unmarshal(String s) throws Exception {
        return StringUtil.isPropertyEmpty(s) ? null : Byte.parseByte(s);
    }
}