package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LongXmlAdapter extends XmlAdapter<String, Long> {

    @Override
    public String marshal(Long v) throws Exception {
        return v.toString();
    }

    @Override
    public Long unmarshal(String v) throws Exception {
        return StringUtil.isPropertyEmpty(v) ? null : Long.parseLong(v);
    }
}
