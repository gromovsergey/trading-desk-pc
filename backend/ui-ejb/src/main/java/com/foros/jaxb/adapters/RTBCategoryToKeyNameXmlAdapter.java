package com.foros.jaxb.adapters;

import com.foros.model.creative.RTBCategory;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RTBCategoryToKeyNameXmlAdapter extends XmlAdapter<KeyName, RTBCategory> {

    @Override
    public KeyName marshal(RTBCategory rtbCategory) throws Exception {
        return new KeyName(rtbCategory.getName(), rtbCategory.getRtbConnector().getName());
    }

    @Override
    public RTBCategory unmarshal(KeyName keyName) throws Exception {
        return null;
    }
}
