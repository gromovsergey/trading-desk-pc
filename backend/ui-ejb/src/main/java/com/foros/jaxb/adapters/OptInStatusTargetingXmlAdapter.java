package com.foros.jaxb.adapters;

import com.foros.model.campaign.OptInStatusTargeting;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OptInStatusTargetingXmlAdapter extends XmlAdapter<XmlOptInStatusTargeting, OptInStatusTargeting> {

    @Override
    public OptInStatusTargeting unmarshal(XmlOptInStatusTargeting v) throws Exception {
        if (v == null ||
            // all null - empty tag
            (v.getOptedInUsers() == null && v.getOptedOutUsers() == null && v.getUnknownUsers() == null)) {
            return null;
        }
        return new OptInStatusTargeting(v.getOptedInUsers(), v.getOptedOutUsers(), v.getUnknownUsers());
    }

    @Override
    public XmlOptInStatusTargeting marshal(OptInStatusTargeting v) throws Exception {
        return v == null ? null :
                new XmlOptInStatusTargeting(v.isOptedInUsers(), v.isOptedOutUsers(), v.isUnknownUsers());
    }
}
