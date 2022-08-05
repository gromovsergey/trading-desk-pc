package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CharacterXmlAdapter extends XmlAdapter<String, Character> {

    @Override
    public Character unmarshal(String v) throws Exception {
        return v.charAt(0);
    }

    @Override
    public String marshal(Character v) throws Exception {
        return String.valueOf(v);
    }

}
