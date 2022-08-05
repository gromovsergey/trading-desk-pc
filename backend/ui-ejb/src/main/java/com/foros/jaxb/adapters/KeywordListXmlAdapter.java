package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;

public class KeywordListXmlAdapter extends XmlAdapter<XmlTriggers, String> {
    @Override
    public XmlTriggers marshal(String keywords) throws Exception {
        if (keywords == null) {
            return null;
        }
        String[] triggers = StringUtil.splitAndTrim(keywords);
        return new XmlTriggers(Arrays.asList(triggers));
    }

    @Override
    public String unmarshal(XmlTriggers keywords) throws Exception {
        if (keywords == null || keywords.getList() == null) {
            return null;
        }
        return StringUtil.trimLinesAndJoin(keywords.getList());
    }
}
