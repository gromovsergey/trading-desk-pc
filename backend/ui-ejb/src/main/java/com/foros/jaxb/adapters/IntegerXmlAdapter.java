package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerXmlAdapter extends XmlAdapter<String, Integer> {

    @Override
    public String marshal(Integer v) throws Exception {
        return v.toString();
    }

    @Override
    public Integer unmarshal(String v) throws Exception {
        return StringUtil.isPropertyEmpty(v) ? null : Integer.parseInt(v);
    }
}
