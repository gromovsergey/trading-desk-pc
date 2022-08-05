package com.foros.jaxb.adapters;

import com.foros.model.channel.KeywordTriggerType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class KeywordTriggerTypeAdapter extends XmlAdapter<String, KeywordTriggerType> {

    public KeywordTriggerTypeAdapter() {
    }

    @Override
    public KeywordTriggerType unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }

        return KeywordTriggerType.byName(v);
    }

    @Override
    public String marshal(KeywordTriggerType v) throws Exception {
        return v == null ? null : v.getName();
    }
}
